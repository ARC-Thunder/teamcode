package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Alex on 4/4/2017.
 */

@TeleOp(name = "Supercell Single Joystick Derp", group = "Supercell")
public class SupercellSingleJoystickDriveDerp extends OpMode {

    /**
     * Indicating robot components
     **/
    /* -------------------------------------------------------------------------------------- */
    private DcMotorController MC_M;
    private DcMotor motorR, motorL;

    private DeviceInterfaceModule DIM;
    /* -------------------------------------------------------------------------------------- */

    @Override
    public void init() {
        /* Initializing and mapping electronics */

        /** Drive Motors and Respective MC **/
        /* -------------------------------------------------------------------------------------- */
        MC_M = hardwareMap.dcMotorController.get("MC_M");       // Maps the Motor Controller

        motorL = hardwareMap.dcMotor.get("motorL");             // Maps the Left Motor
        motorR = hardwareMap.dcMotor.get("motorR");             // Maps the Right Motor

        motorR.setDirection(DcMotorSimple.Direction.REVERSE);   // Reverses Left Motor (so that the
        // robot can go forward)

        motorL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);    // Initially sets the motors to run
        motorR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);    // without encoders, but can be
        // changed later in the code.
        /* -------------------------------------------------------------------------------------- */
    }

    public void loop() {

        /** For the Drive Train **/
        /* -------------------------------------------------------------------------------------- */
        /** ORIGINAL DRIVE
        double speedModifier = gamepad1.right_trigger > 0 ? 0.25 : gamepad1.right_bumper ? 1 : 0.6;
        // Modifies the power (below) depending on the right trigger and bumper

        double leftPower = gamepad1.left_stick_y * speedModifier;     // Both set power according to
        double rightPower = gamepad1.right_stick_y * speedModifier;   // the joysticks on controller 1

        leftPower = Range.clip(leftPower, -1, 1);
        rightPower = Range.clip(rightPower, -1, 1);

        motorL.setPower(leftPower);                             // Sets the motor power equal to
        motorR.setPower(rightPower);                            // each's respective power **/

        double joystickX = gamepad1.left_stick_x;
        double joystickY = gamepad1.left_stick_y;

        boolean joystickYPositive = true;
        double upperAngle = 0;
        // Statements
        if (joystickY > 0) {
            upperAngle = Math.toDegrees(Math.atan(joystickX / joystickY));
            // Q1 is positive, Q4 is negative
        } else if (joystickY < 0) {
            upperAngle = -(Math.toDegrees(Math.atan(joystickX / joystickY)));
            // Q2 is negative, Q3 is positive
            joystickYPositive = false;
        } else {

        }


        double speedModifier = Math.pow(1 - (Math.abs(upperAngle) / 45), 1/3);
        double vectorMagnitude = Math.pow(Math.hypot(joystickX, joystickY), 1/3);  // The power of the motors

        double leftPower = 0;
        double rightPower = 0;
        // Determining which side
        if (upperAngle > 0 && upperAngle < 90) {
            if (joystickYPositive = true) {
                leftPower = vectorMagnitude;
                rightPower = speedModifier;
            } else if (joystickYPositive = false) {
                leftPower = vectorMagnitude;
                rightPower = speedModifier;
            }
        } else if (upperAngle < 0 && upperAngle > -90) {
            if (joystickYPositive = true) {
                leftPower = speedModifier;
                rightPower = vectorMagnitude;
            } else if (joystickYPositive = false) {
                leftPower = speedModifier;
                rightPower = vectorMagnitude;
            }
        } else {
            leftPower = 0;
            rightPower = 0;
        }

        // To make the robot move forward
        if (joystickY == 1) {
            leftPower = 1;
            rightPower = 1;
        } else if (joystickY == -1) {
            leftPower = -1;
            rightPower = -1;
        } else if (joystickX == 1) {
            leftPower = 1;
            rightPower = -1;
        } else if (joystickX == -1) {
            leftPower = -1;
            rightPower = 1;
        }

        leftPower = Range.clip(leftPower, -1, 1);
        rightPower = Range.clip(rightPower, -1, 1);

        motorL.setPower(leftPower);                             // Sets the motor power equal to
        motorR.setPower(rightPower);                            // each's respective power
        // Example scenario: x = 0, y = 1 -- robot should move forward
        // Example scenario: x = -1, y = 0 -- robot should rotate, motorR forward and motorL backwards

        /* -------------------------------------------------------------------------------------- */

        /** Add telemetry here **/
        /* -------------------------------------------------------------------------------------- */
        telemetry.addData("Angle", upperAngle);
        telemetry.addData("Motor Power", vectorMagnitude);
        telemetry.addData("Speed Modifier", speedModifier);

        telemetry.addData("Left Motor Power", leftPower);       // Adds telemetry
        telemetry.addData("Right Motor Power", rightPower);

        /* - UPDATE TELEMETRY - */
        telemetry.update();                                     // Updates telemetry
        /* -------------------------------------------------------------------------------------- */
    }

}