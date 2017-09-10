package org.firstinspires.ftc.teamcode;

/**
 * Created by joshua krinsky on 4/5/17.
 */
import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.hardware.hitechnic.HiTechnicNxtGyroSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "SupercellgyroTest", group = "Supercell")
public class gyroTester extends LinearOpMode {

    private DcMotorController MC_M;
    private DcMotor motorR, motorL;
    HiTechnicNxtGyroSensor hiTechGyro;
    private DeviceInterfaceModule DIM;
    private GyroSensor sensorGyro;
    int xAccumulated=0;

    public void runOpMode() throws InterruptedException {

        MC_M = hardwareMap.dcMotorController.get("MC_M");       // Maps the Motor Controller
        sensorGyro = hardwareMap.gyroSensor.get("gyro");              // Maps the gyro sensor
        motorL = hardwareMap.dcMotor.get("motorL");             // Maps the Left Motor
        motorR = hardwareMap.dcMotor.get("motorR");             // Maps the Right Motor
        motorR.setDirection(DcMotorSimple.Direction.REVERSE);   // Reverses Left Motor (so that the
        // robot can go forward)
        motorL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);    // Initially sets the motors to run
        motorR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double turnSpeed = .15;
        double rotationFraction = 0;


        hiTechGyro = (HiTechnicNxtGyroSensor) sensorGyro;



        sleep(1000);
        hiTechGyro.calibrate(); //calibrates the gyro sensor for use and world domination


        waitForStart();

            while (hiTechGyro.isCalibrating()) { //causes the program to halt while callibration
                // commenses over the rising horizon
            }


            while(opModeIsActive()) {

                telemetry.addData("is calibrated", rotationFraction);
                telemetry.update();


                rotationFraction = hiTechGyro.getRotationFraction();

                while (Math.abs(rotationFraction) > 0) { // rotation conditional


                    if (rotationFraction > 0) {  //turn one way if too far above zero
                        motorL.setPower(turnSpeed);
                        motorR.setPower(-turnSpeed);
                    }

                    if (rotationFraction < 0) {   //turn the other if too far below
                        motorL.setPower(-turnSpeed);
                        motorR.setPower(turnSpeed);

                    }



                //waitOneFullHardwareCycle();

                rotationFraction = hiTechGyro.getRotationFraction();

                telemetry.addData("1. fraction", rotationFraction);
                    telemetry.update(); // update fraction
                //waitOneFullHardwareCycle();

            }
                motorL.setPower(0);//stop power
                motorR.setPower(0); //stop power
                telemetry.addData("1. fraction", rotationFraction); //fraction is zero
                telemetry.update(); //update fraction
        }


    }

}
