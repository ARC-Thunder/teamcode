package org.firstinspires.ftc.teamcode;

/**
 * Created by Alex on 2/2/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@Autonomous(name="Red: Put the robot along the wall while having it target the middle of the white line or the corner of the same square it is in", group="Encoders")
public class EncoderToBeaconRed extends LinearOpMode {



    /** Declaring the motor variables **/
    private DcMotorController motorControllerL;         // Left Motor Controllers
    private DcMotorController motorControllerR;         // Right Motor Controllers
    private DcMotorController motorControllerA1;        // Auxiliary Motor Controller 1
    private DcMotorController motorControllerA2;        // Auxiliary Motor Controller 2
    private ServoController servoController;            // Servo Controller
    private DeviceInterfaceModule interfaceModule; //stated interface module

    private DcMotor motorFrontL;                        // Front Left Motor
    private DcMotor motorFrontR;                        // Front Right Motor
    private DcMotor motorBackL;                         // Back Left Motor
    private DcMotor motorBackR;                         // Back Right Motor
    private DcMotor sweeperMotor;                       // Sweeper Motor
    private DcMotor motorLauncher;                      // Continuous Catapult Launcher Motor
    private DcMotor motorStrafe;                        // Sideways Strafe Motor

    private Servo servo;                                // Ball Queue Servo
    private ColorSensor colorSensor; //stated colorsensor

    public static final I2cAddr COLOR_SENSOR_ORIGINAL_ADDRESS = I2cAddr.create8bit(0x3c);

    /** For Encoders and specific turn values **/
    int ticksPerRev = 1120;             // This is the specific value for AndyMark motors
    int ticksPer360Turn = 4870;         // The amount of ticks for a 360 degree turn
    int tickTurnRatio = ticksPer360Turn / 360;

    double wheelDiameter = 4.0;         // Diameter of the current omniwheels in inches
    double ticksPerInch = (ticksPerRev / (wheelDiameter * 3.14159265));






    public void runOpMode() throws  InterruptedException{
        /** This is the method that executes the code and what the robot should do **/
        // Initializes the electronics
        initElectronics(0);

        telemetry.addData("Phase 1", "Init");
        telemetry.update();

        waitForStart();

        colorSensor.enableLed(false);


        telemetry.addData("Started Robot", "Now");
        telemetry.update();

        runToPositionEncoders();
        telemetry.addData("Move Forward", "50 inches backwards");
        telemetry.update();
        encoderMove(0.4, -50, -50);


        runToPositionEncoders();
        telemetry.addData("Reached Corner", "Yes");
        telemetry.update();
        rotateDegreesRight(0.4, 45);


        runToPositionEncoders();
        telemetry.addData("Parallel", "Yes");
        telemetry.update();
        encoderMove(0.4, -11, -11);


        runToPositionEncoders();
        telemetry.addData("Centered", "Yes");
        telemetry.update();
        rotateDegreesLeft(0.4, 90);



        runToPositionEncoders();
        telemetry.addData("Aligned", "Yes");
        telemetry.update();
        encoderMove(0.4, -15, -15);


        if (colorSensor.red() > colorSensor.blue()){
            telemetry.addData("Color Sensed", "Red");
            telemetry.update();

            rotateDegreesLeft(0.1, 25);
        }
        else {
            telemetry.addData("Color Sensed", "Blue");
            telemetry.update();

            rotateDegreesRight(0.1, 25);
        }


    }

    public void initElectronics(int mode) throws InterruptedException {
        // To make the initialization of electronics much easier and nicer to read
        /** Initializing and mapping electronics **/
        if (mode == 0) {
            motorControllerL = hardwareMap.dcMotorController.get("MC_L");
            motorControllerR = hardwareMap.dcMotorController.get("MC_R");
            motorControllerA1 = hardwareMap.dcMotorController.get("MC_A1");
            motorControllerA2 = hardwareMap.dcMotorController.get("MC_A2");
            servoController = hardwareMap.servoController.get("SC");

            motorFrontL = hardwareMap.dcMotor.get("motorFrontL");        //P0 is actually the right
            motorFrontR = hardwareMap.dcMotor.get("motorFrontR");        //P1 is actually the left
            motorBackL = hardwareMap.dcMotor.get("motorBackL");          //P0
            motorBackR = hardwareMap.dcMotor.get("motorBackR");          //P1

            servo = hardwareMap.servo.get("servo");

            motorLauncher = hardwareMap.dcMotor.get("motorLauncher");   //P0
            sweeperMotor = hardwareMap.dcMotor.get("motorSweeper");     //P1

            motorStrafe = hardwareMap.dcMotor.get("motorStrafe");       //P0 A2

            /*Setting channel modes*/
            resetEncoders();
            runUsingEncoders();

            motorLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            sweeperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            motorStrafe.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            motorFrontL.setDirection(DcMotorSimple.Direction.REVERSE);
            motorBackL.setDirection(DcMotorSimple.Direction.REVERSE);
            motorLauncher.setDirection(DcMotorSimple.Direction.REVERSE);

            interfaceModule = hardwareMap.deviceInterfaceModule.get("DIM"); //hardware map the device interface module which controls the color sensor
            colorSensor = hardwareMap.colorSensor.get("colorBeacon"); //hardware map the color sensor
            colorSensor.setI2cAddress(COLOR_SENSOR_ORIGINAL_ADDRESS); //we made it so this one has to be this address, need seperate program to change this
        }
        else if (mode == 1) {

        }

    }

    public void launch(double power, long milliseconds) throws InterruptedException{
        motorLauncher.setPower(power);
        Thread.sleep(milliseconds);
    }

    public void encoderMove(double power,
                            double leftInches, double rightInches) {
        /** This method makes the motors move a certain distance **/
        resetEncoders();

        // Sets the power range
        power = Range.clip(power, -1, 1);

        // Setting the target positions
        motorFrontL.setTargetPosition((int)(leftInches * -ticksPerInch));
        motorFrontR.setTargetPosition((int)(rightInches * -ticksPerInch));
        motorBackL.setTargetPosition((int)(leftInches * -ticksPerInch));
        motorBackR.setTargetPosition((int)(rightInches * -ticksPerInch));

        runToPositionEncoders();

        // Sets the motors' position
        motorFrontL.setPower(power);
        motorFrontR.setPower(power);
        motorBackL.setPower(power);
        motorBackR.setPower(power);

        // While loop for updating telemetry
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()){

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }

        // Stops the motors
        stopMotion();

        // Resets to run using encoders mode
        runUsingEncoders();

    }

    public void rotateDegreesLeft(double power, int robotDegrees) throws  InterruptedException {
        /** Robot requires values of...
         *  360 degrees =~ 4600 ticks
         *  180 degrees =~ 2300 ticks  **/

        /** This method, given an input amount of degrees, makes the robot turn
         *  the amount of degrees specified around ITS center of rotation **/
        resetEncoders();

        // Sets the power range
        power = Range.clip(power, -1, 1);

        // Setting the target positions
        motorFrontL.setTargetPosition(robotDegrees * tickTurnRatio);
        motorFrontR.setTargetPosition(robotDegrees * -tickTurnRatio);
        motorBackL.setTargetPosition(robotDegrees * tickTurnRatio);
        motorBackR.setTargetPosition(robotDegrees * -tickTurnRatio);

        runToPositionEncoders();

        // Sets the motors' positions
        motorFrontL.setPower(power);
        motorFrontR.setPower(power);
        motorBackL.setPower(power);
        motorBackR.setPower(power);

        // While loop for updating telemetry
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()){

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }

        // Stops the motors
        stopMotion();

        // Resets to run using encoders mode
        runUsingEncoders();
    }
    public void rotateDegreesRight(double power, int robotDegrees) throws  InterruptedException {
        /** Robot requires values of...
         *  360 degrees =~ 4600 ticks
         *  180 degrees =~ 2300 ticks  **/

        /** This method, given an input amount of degrees, makes the robot turn
         *  the amount of degrees specified around ITS center of rotation **/
        resetEncoders();

        // Sets the power range
        power = Range.clip(power, -1, 1);

        // Setting the target positions
        motorFrontL.setTargetPosition(robotDegrees * -tickTurnRatio);
        motorFrontR.setTargetPosition(robotDegrees * tickTurnRatio);
        motorBackL.setTargetPosition(robotDegrees * -tickTurnRatio);
        motorBackR.setTargetPosition(robotDegrees * tickTurnRatio);

        runToPositionEncoders();

        // Sets the motors' positions
        motorFrontL.setPower(power);
        motorFrontR.setPower(power);
        motorBackL.setPower(power);
        motorBackR.setPower(power);

        // While loop for updating telemetry
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()){

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }

        // Stops the motors
        stopMotion();

        // Resets to run using encoders mode
        runUsingEncoders();
    }


    public void resetEncoders() {
        /** Resets the encoder values on each of the drive motors **/
        motorFrontL.setMode(DcMotor.RunMode.RESET_ENCODERS);
        motorFrontR.setMode(DcMotor.RunMode.RESET_ENCODERS);
        motorBackL.setMode(DcMotor.RunMode.RESET_ENCODERS);
        motorBackR.setMode(DcMotor.RunMode.RESET_ENCODERS);
    }

    public void runToPositionEncoders() {
        /** Sets the encoded motors to RUN_TO_POSITION **/
        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void runUsingEncoders() {
        /** Sets the encoders to RUN_USING_ENCODERS **/
        motorFrontL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void addTelemetryData(String string1, String string2) {
        telemetry.addData(string1, string2);
        telemetry.update();
    }

    public void stopMotion() {
        /** Stops all drive motor motion **/
        motorFrontL.setPower(0);
        motorFrontR.setPower(0);
        motorBackL.setPower(0);
        motorBackR.setPower(0);
    }
}
