package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;


/**
 * Created by citruseel on 10/26/2016.
 */
@Disabled
@Autonomous(name="Thunder2016-2017 Autonomous Blue/Right", group="Autonomous")
//Place robot backwards at atonomous because the beacon pressers are on the back, so we make the driving as if the robot was backwards

public class ThunderBasicAuto2016_2017LinearOpMode extends LinearOpMode {

    private DcMotorController motorControllerP0;    // Motor Controller in port 0 of Core
    private DcMotorController motorControllerP1;    // Motor Controller in port 1 of Core
    private DcMotorController motorControllerP4;    // Motor Controller in port 4 of Core

    private DcMotor motor1;                         // Motor 1: port 1 in Motor Controller 1
    private DcMotor motor2;                         // Motor 2: port 2 in Motor Controller 1
    private DcMotor motor3;                         // Motor 3: port 1 in Motor Controller 0
    private DcMotor motor4;                         // Motor 4: port 2 in Motor Controller 0
    private DcMotor sweeperMotor;                   // Sweeper motor: port 1 in Motor Controller 4
    private DcMotor launcherMotor;                  // Launcher motor: port 2 in Motor Controller 4

    private ServoController servoController;

    private Servo colorSensorServo;

    private double servoposition = 0.5;//start position
    private DeviceInterfaceModule interfaceModule; //stated interface module

    ColorSensor colorSensor; //stated colorsensor
    ColorSensor beaconSensor; //state beacon color sensor, it is on the right of our robot

    //Each color sensor has it's own I2cAddress; they need to have unique addresses so the system doesn't get confused.
    public static final I2cAddr COLOR_SENSOR_ORIGINAL_ADDRESS = I2cAddr.create8bit(0x3c);//this is to create our own i2c address
    public static final I2cAddr COLOR_SENSOR_CHANGED_ADDRESS = I2cAddr.create8bit(0x3a);


    public void runOpMode() throws InterruptedException{
         /* Initializing and mapping electronics*/
        motorControllerP0 = hardwareMap.dcMotorController.get("MCP0");
        motorControllerP1 = hardwareMap.dcMotorController.get("MCP1");
        motorControllerP4 = hardwareMap.dcMotorController.get("MCP4");


        motor1 = hardwareMap.dcMotor.get("motorFrontL");        //MCP4
        motor2 = hardwareMap.dcMotor.get("motorFrontR");        //MCP4
        motor3 = hardwareMap.dcMotor.get("motorBackL");         //MCP1          Back of motor 1
        motor4 = hardwareMap.dcMotor.get("motorBackR");         //MCP1          Back of motor 2

        launcherMotor = hardwareMap.dcMotor.get("motorLauncher"); //hardwaremapping the motor for the launcher MCP0 motor 1
        sweeperMotor = hardwareMap.dcMotor.get("motorSweeper"); //hardwaremapping the motor for the sweeper MCP0 motor 2

        /*Setting channel modes*/
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        launcherMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sweeperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor2.setDirection(DcMotorSimple.Direction.REVERSE);
        motor3.setDirection(DcMotorSimple.Direction.REVERSE);
        //launcherMotor.setDirection(DcMotorSimple.Direction.REVERSE);                  We did this so that when the joystick is pushed up, the ball will launch upwards; when the joystick is pushed down, the ball will launch downwards

        servoController = hardwareMap.servoController.get("SCP2"); //hardwaremapping the servo controller

        colorSensorServo = hardwareMap.servo.get("servo"); //hardwaremapping the servo


        interfaceModule = hardwareMap.deviceInterfaceModule.get("DIM"); //hardware map the device interface module which controls the color sensor

        colorSensor = hardwareMap.colorSensor.get("Color sensor"); //hardware map the color sensor
        colorSensor.setI2cAddress(COLOR_SENSOR_ORIGINAL_ADDRESS); //we made it so this one has to be this address, need seperate program to change this

        beaconSensor = hardwareMap.colorSensor.get("Beacon Color sensor");
        beaconSensor.setI2cAddress(COLOR_SENSOR_CHANGED_ADDRESS); //we made it so this one has to be this address

        //color sensor intial states
        colorSensor.enableLed(true); //makes it so shadows/lighting doesn't affect it's reading
        beaconSensor.enableLed(false); //makes it so it can see the led light behind the plastic of the beacon cover


        //variables for going to the tape and rotating
        boolean seenTape = false;
        boolean alignedToTape = false;
        long lastTime = System.currentTimeMillis(); //I believe gets time in milliseconds for when the robot enters the tape
        long newTime = System.currentTimeMillis(); //gets the time for when the robot leaves the tape
        long changeInTime = (newTime-lastTime); //gets the amount of time it took for the robot to travel across the tape
        double tapeLengthTraveled = 0; //temporary until it is changed later in the autonomous
        double tapeWidth = 2; //in inches; actual tape measurements found on http://www.andymark.com/FTC17-p/am-3160.htm
        double speed = 15.5; //robot's speed at 0.5 power in inches/seconds
        double turningAngle = Math.asin(tapeWidth/tapeLengthTraveled); //finds angle to turn(Note: I believe asin.(number) = inverse sine)
        long turningAngleLong = (long) turningAngle;//turn the turning angle into a long so that it can be used in the time
        long rotateSpeed = 162; //robot's rotational degrees per second at 0.5 power; needs to be long so it can be used in the calculation for time


        waitForStart(); //all code below is what the robot actually does

        while(opModeIsActive()){

            // What the robot will do to get to the Beacons and align to them

            //What happens until robot sees the tape
            if((colorSensor.red() > 10 || colorSensor.blue() > 10 || colorSensor.green() < 10) || (colorSensor.red() < 2 || colorSensor.blue() < 2 || colorSensor.green() < 2)){ //should see anything else but white
                MoveForward(0.5); //go until see white tape
            }

            else if (colorSensor.red() < 10 && colorSensor.blue() < 10 && colorSensor.green() < 10 && colorSensor.red() >= 2 && colorSensor.blue() >= 2 && colorSensor.green() >= 2){ //I believe this makes it see if white
                seenTape = true; //initiates other stuff; makes all this stuff within this block of code instantaneous rather than looped
                lastTime = System.currentTimeMillis(); //get the current time now when the robot enters the tape
            }

            //What happens when the robot sees the tape
            if (colorSensor.red() < 10 && colorSensor.blue() < 10 && colorSensor.green() < 10 && colorSensor.red() >= 2 && colorSensor.blue() >= 2 && colorSensor.green() >= 2 && (seenTape)){
                    MoveForward(0.5); //move foward
            }

            //what happens when the robot passes the tape
            if ((colorSensor.red() > 10 || colorSensor.blue() > 10 || colorSensor.green() < 10) || (colorSensor.red() < 2 || colorSensor.blue() < 2 || colorSensor.green() < 2) && (seenTape)){

                //update all variables
                newTime = System.currentTimeMillis();
                changeInTime = (newTime - lastTime) / 1000; //divide by 1000 to convert to seconds from milliseconds
                tapeLengthTraveled = changeInTime * speed;
                turningAngle = Math.asin(tapeWidth/tapeLengthTraveled);
                turningAngleLong = (long) turningAngle;

                BackUp(0.5, changeInTime * 1000 / 2); //I believe this will make it so the robot moves to the center of the white tape
                rotateRight(0.5, turningAngleLong/rotateSpeed);

                alignedToTape = true; //initiates poker stuff makes it so the robot doesn't constantly repeat this block of code
            }


            //poker stuff begins here; poker is on the right side of our robot
            if (alignedToTape == true){

                //if we are on the blue team
                if (beaconSensor.red() > beaconSensor.blue()){ //if sense red on the right
                    servoposition = 0.95; //left
                }
                else if (beaconSensor.blue() > beaconSensor.red()){//if sense blue on the right
                    servoposition = 0.05; //right
                }
                else {
                    MoveForward(0.5);
                }
            }


            colorSensorServo.setPosition(servoposition); //constantly updates servo position and set's servo to the position

            //hopefully shows on phone what colors are being shown
            telemetry.addData("Red  ", colorSensor.red());
            telemetry.addData("Green", colorSensor.green());
            telemetry.addData("Blue ", colorSensor.blue());

            telemetry.addData("Beacon Red  ", beaconSensor.red());
            telemetry.addData("Beacon Blue ", beaconSensor.blue());
            telemetry.addData("Beacon Green ", beaconSensor.green());

            telemetry.addData("Time over tape: ", changeInTime);

            telemetry.update();

        }
    }

    public void MoveForward(double power){
        motor1.setPower(power);
        motor2.setPower(power);
        motor3.setPower(power);
        motor4.setPower(power);
    }

    public void BackUp(double power, long time) throws InterruptedException{
        motor1.setPower(-power);
        motor2.setPower(-power);
        motor3.setPower(-power);
        motor4.setPower(-power);

        Thread.sleep(time);
    }

    public void rotateRight(double power, long time)throws InterruptedException{
        motor1.setPower(power);
        motor2.setPower(-power);
        motor3.setPower(power);
        motor4.setPower(-power);

        Thread.sleep(time);
    }

}

