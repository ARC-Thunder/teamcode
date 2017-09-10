package org.firstinspires.ftc.teamcode;

import android.test.InstrumentationTestRunner;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by citruseel on 10/26/2016.
 */
@Disabled
@Autonomous(name="MovementAutonomous", group="Autonomous")

//Place robot backwards at atonomous because the beacon pressers are on the back, so we make the driving as if the robot was backwards

public class AutonomousMovementTest extends LinearOpMode {
    /* Note:
     * When you extend OpMode, you must declare the methods init() and loop()
     */


    /** Declaring electronics
     * This can be done with a separate class and can make creating code much easier / simpler. */
    private DcMotorController motorControllerP0;    // Motor Controller in port 0 of Core
    private DcMotorController motorControllerP1;    // Motor Controller in port 1 of Core

    private DcMotor motor1;                         // Motor 1: port 1 in Motor Controller 1
    private DcMotor motor2;                         // Motor 2: port 2 in Motor Controller 1
    private DcMotor motor3;                         // Motor 3: port 1 in Motor Controller 0
    private DcMotor motor4;                         // Motor 4: port 2 in Motor Controller 0

    /* Declaring variables */

    public void runOpMode() throws InterruptedException{
        /** Initializing and mapping electronics (motors, motor controllers, servos, etc.) */
        motorControllerP0 = hardwareMap.dcMotorController.get("MCP0");
        motorControllerP1 = hardwareMap.dcMotorController.get("MCP1");

        motor1 = hardwareMap.dcMotor.get("motorFrontR");
        motor2 = hardwareMap.dcMotor.get("motorFrontL");
        motor3 = hardwareMap.dcMotor.get("motorBack1");
        motor4 = hardwareMap.dcMotor.get("motorBack2");

        /**Setting channel modes
         *  When setting channel modes,  use the names that are declared to the motors. */
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor1.setDirection(DcMotorSimple.Direction.REVERSE);
        motor3.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        MoveForward(0.5, 2000/*milliseconds*/);
    }

    public void MoveForward(double power, long time)throws InterruptedException{
        motor1.setPower(power);
        motor2.setPower(power);
        motor3.setPower(power);
        motor4.setPower(power);

        Thread.sleep(time);
    }
    public void rotateLeft(double power, long time)throws InterruptedException{
        motor1.setPower(-power);
        motor2.setPower(power);
        motor3.setPower(-power);
        motor4.setPower(power);

        Thread.sleep(time);
    }

}
