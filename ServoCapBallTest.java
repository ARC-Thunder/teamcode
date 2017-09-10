package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.ThreadPool;

/**
 * Created by Alex on 2/22/2017.
 */

@Autonomous(name="Cap Ball Servo Test", group="Servo")
public class ServoCapBallTest extends LinearOpMode{

    /** Declaring the motor variables **/
    private Servo capball1;                             // Cap Ball Servo 1
    private Servo capball2;                             // Cap Ball Servo 2



    public void runOpMode() throws InterruptedException{
        /** This is the method that executes the code and what the robot should do **/
        // Call any variables not stated before

        // Initializes the electronics
        initElectronics(0);

        telemetry.addData("Phase 1", "Init");
        telemetry.update();

        waitForStart();

        telemetry.addData("Started Robot", "Now");
        telemetry.update();

        capBallServo(0);

        Thread.sleep(1000);

        capBallServo(0.5);

        Thread.sleep(1000);

        capBallServo(1);


    }


    /** These methods control the robot's movement **/
    public void capBallServo(double position) {

        position = Range.clip(position, -0.2, 0.45);
        /* Servo Details:
            Position 0 = Cap ball system up
            Position 0.5 = Cap ball system down
        * */
        capball1.setPosition(1 - position);
        capball2.setPosition(position);

    }
    /** ----------------------------------------- **/


    /** These methods are used to set up the robot **/
    public void initElectronics(int mode) throws InterruptedException {
        // To make the initialization of electronics much easier and nicer to read
        /** Initializing and mapping electronics **/
        if (mode == 0) {

            capball1 = hardwareMap.servo.get("capball1");
            capball2 = hardwareMap.servo.get("capball2");

        }
        else if (mode == 1) {

        }

    }
    /** ----------------------------------------- **/
}
