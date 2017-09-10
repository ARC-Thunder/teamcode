package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

/**
 * Created by citruseel on 11/2/2016. Note, currently this code only works with one color sensor plugged in
 */

@Autonomous(name="ColorSensorTest", group="Color sensor")
public class ColorSensorTest extends LinearOpMode{ //apparetnly LinearOpMode does not equal step by step things but actually autonomous.... OpMode = controller inputs

//    private ServoController servoController;

//    private Servo colorSensorServo;

//    private double servoposition = 0.5;//start position

    private DeviceInterfaceModule interfaceModule; //stated interface module

    ColorSensor colorSensor; //stated colorsensor
//    ColorSensor beaconSensor;

    //Each color sensor has it's own I2cAddress, they need to have unique addresses so the systeme doesn't get confused.
    public static final I2cAddr COLOR_SENSOR_ORIGINAL_ADDRESS = I2cAddr.create8bit(0x3c);//this is to create our own i2c address for some reason
//    public static final I2cAddr COLOR_SENSOR_CHANGED_ADDRESS = I2cAddr.create8bit(0x3a);

    public String sensedColor = null;

    public void runOpMode(){


//        servoController = hardwareMap.servoController.get("SCP2"); //hardwaremapping the servo controller

//        colorSensorServo = hardwareMap.servo.get("servo"); //hardwaremapping the servo

        interfaceModule = hardwareMap.deviceInterfaceModule.get("DIM"); //hardware map the device interface module which controls the color sensor
        colorSensor = hardwareMap.colorSensor.get("colorBeacon"); //hardware map the color sensor
        colorSensor.setI2cAddress(COLOR_SENSOR_ORIGINAL_ADDRESS); //we made it so this one has to be this address, need seperate program to change this
//        beaconSensor = hardwareMap.colorSensor.get("Beacon Color sensor");
//        beaconSensor.setI2cAddress(COLOR_SENSOR_CHANGED_ADDRESS); //we made it so this one has to be this address


        waitForStart();

        colorSensor.enableLed(true);
//        beaconSensor.enableLed(false);

        while(opModeIsActive()){

            if (colorSensor.red() > colorSensor.blue() && colorSensor.red() > colorSensor.green()){
                sensedColor = "red";
            }
            else if (colorSensor.blue() > colorSensor.red() && colorSensor.blue() > colorSensor.green()){
                sensedColor = "blue";
            }
            else if (colorSensor.green() > colorSensor.blue() && colorSensor.green() > colorSensor.red()){
                sensedColor = "green";
            }
            /*
            //this small code down here verifies that this is an "if colorsensor sees white" statement
            if(colorSensor.red() < 10 && colorSensor.blue() < 10 && colorSensor.green() < 10 && colorSensor.red() >= 2 && colorSensor.blue() >= 2 && colorSensor.green() >= 2){
                telemetry.addData("Seen White: ", true);
            }

            //beacon color sensor servo stuff
            if (beaconSensor.red() > beaconSensor.blue()){ //if sense red
                servoposition = 0.95;
            }
            else if (beaconSensor.blue() > beaconSensor.red()){//if sense blue
                servoposition = 0.05;
            }
            else {
                servoposition = 0.5;
            }

            colorSensorServo.setPosition(servoposition); //constantly updates servo position and set's servo to the position
            */
            //hopefully shows on phone what colors are being shown
            telemetry.addData("Red  ", colorSensor.red());
            telemetry.addData("Green", colorSensor.green());
            telemetry.addData("Blue ", colorSensor.blue());
            telemetry.addData("Color", sensedColor);

            /*telemetry.addData("Beacon Red  ", beaconSensor.red());
            telemetry.addData("Beacon Blue ", beaconSensor.blue());
            telemetry.addData("Beacon Green ", beaconSensor.green());*/

            telemetry.update();

        }

    }

}
