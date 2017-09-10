package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by citruseel on 4/7/2017.
 */
@TeleOp(name = "OmOmBotTelOp", group = "OmOmBot")
public class OmOmBot extends OpMode{
    /**
     * Indicating robot components
     **/
    /* -------------------------------------------------------------------------------------- */
    private DcMotor motorsSide, motorsForward;

    public void init(){
        /* -------------------------------------------------------------------------------------- */

        motorsSide = hardwareMap.dcMotor.get("motorT&B");             // Maps the Top and Bottom Motors
        motorsForward = hardwareMap.dcMotor.get("motorL&R");             // Maps the Left and Right Motors

//        motorsSide.setDirection(DcMotorSimple.Direction.REVERSE);   // Reverses Left Motor (so that the
//        // robot can go forward)
//        motorsForward.setDirection(DcMotorSimple.Direction.REVERSE);

        motorsForward.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);    // Initially sets the motors to run
        motorsSide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);    // without encoders, but can be
    }

    public void loop(){
        double constant = 0.5;
        double forwardPower = gamepad1.right_stick_y;
        double sidePower = gamepad1.right_stick_x;
        sidePower = Range.clip(sidePower, -1, 1);
        forwardPower = Range.clip(forwardPower, -1, 1);

        motorsSide.setPower(sidePower*constant);
        motorsForward.setPower(forwardPower*constant);

        telemetry.addData("sidePower", sidePower);
        telemetry.addData("forwardPower", forwardPower);

        telemetry.update();
    }
}
