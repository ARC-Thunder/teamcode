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

@TeleOp(name = "Supercell Single Double Angle", group = "Supercell")
public class SupercellSingleJoystickDoubleAngle extends OpMode {

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

        double joystickX = gamepad1.left_stick_x;
        double joystickY = - gamepad1.left_stick_y; // naturally: up = -1, down = 1

        double vectorMagnitude = joystickY > 0 ? Math.hypot(joystickX, joystickY): joystickY < 0 ? -1* Math.hypot(joystickX, joystickY): 0;  // The power of the motors

        double angleR = Math.atan(joystickY/joystickX);

        double angleL = joystickY > 0 ? Math.PI - angleR : joystickY < 0 ? -1 * Math.PI - angleR: 0;

        angleR = Range.clip(angleR, -Math.PI * 2, Math.PI * 2);
        angleL = Range.clip(angleL, -Math.PI * 2, Math.PI * 2);

        double leftPower = Math.pow((angleL / (2 * Math.PI)) * vectorMagnitude, 1/3 );
        double rightPower = Math.pow((angleR / (2 * Math.PI)) * vectorMagnitude, 1/3 );

        leftPower = Range.clip(leftPower, -1, 1);
        rightPower = Range.clip(rightPower, -1, 1);

        motorL.setPower(leftPower);                             // Sets the motor power equal to
        motorR.setPower(rightPower);                            // each's respective power
        /* -------------------------------------------------------------------------------------- */

        /** Add telemetry here **/
        /* -------------------------------------------------------------------------------------- */
        telemetry.addData("Angle", angleR / (2 * Math.PI) * 360 + " degrees");
        telemetry.addData("Magnitude", vectorMagnitude);
        telemetry.addData("Motor Power", vectorMagnitude);
        telemetry.addData("X", joystickX);
        telemetry.addData("Y", joystickY);
        telemetry.addData("Left Motor Power", leftPower);       // Adds telemetry
        telemetry.addData("Right Motor Power", rightPower);

        /* - UPDATE TELEMETRY - */
        telemetry.update();                                     // Updates telemetry
        /* -------------------------------------------------------------------------------------- */
    }

}