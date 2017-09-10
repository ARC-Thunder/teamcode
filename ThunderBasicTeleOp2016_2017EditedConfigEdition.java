package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by citruseel on 10/14/2016.
 */

@TeleOp(name="Thunder 2016-2017 TeleOp", group="TeleOp")
public class ThunderBasicTeleOp2016_2017EditedConfigEdition extends OpMode {

   /* private DcMotorController motorControllerL;    // left motor controllers
    private DcMotorController motorControllerR;    // right motor controllers
    private DcMotorController motorControllerA1;   // Scoring motor controller
    private DcMotorController motorControllerA2;   // Scoring motor controller
    private ServoController servoController; */

    private DcMotor motorFrontL;
    private DcMotor motorFrontR;
    private DcMotor motorBackL;
    private DcMotor motorBackR;
    private DcMotor sweeperMotor;
    private DcMotor motorLauncher;
    //private DcMotor motorStrafe;

    private Servo servo;

    boolean scoring = false;


    @Override
    public void init() {
        /* Initializing and mapping electronics*/
       /* motorControllerL = hardwareMap.dcMotorController.get("MC_L");
        motorControllerR = hardwareMap.dcMotorController.get("MC_R");
        motorControllerA1 = hardwareMap.dcMotorController.get("MC_A1");
        motorControllerA2 = hardwareMap.dcMotorController.get("MC_A2");
        servoController = hardwareMap.servoController.get("SC"); */


        motorFrontL = hardwareMap.dcMotor.get("motorFrontL");        //P0 is actually the right
        motorFrontR = hardwareMap.dcMotor.get("motorFrontR");        //P1 is actually the left
        motorBackL = hardwareMap.dcMotor.get("motorBackL");         //P0
        motorBackR = hardwareMap.dcMotor.get("motorBackR");         //P1

        servo = hardwareMap.servo.get("servo");

        motorLauncher = hardwareMap.dcMotor.get("motorLauncher"); //P0
        sweeperMotor = hardwareMap.dcMotor.get("motorSweeper"); //P1

//        motorStrafe = hardwareMap.dcMotor.get("motorStrafe");//P0 A2

        /*Setting channel modes*/
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sweeperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

//        motorStrafe.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motorFrontL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorLauncher.setDirection(DcMotorSimple.Direction.REVERSE);




    }

    @Override
    public void loop() {                                          //constant loop that rechecks about every 20ms

        motorFrontL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double acceleration = gamepad1.right_trigger > 0 ? 0.25: gamepad1.right_bumper ? 1 : 0.6;

        double leftpower = gamepad1.left_stick_y * acceleration;     //set's a value for power equal to the value of the joysticks for the left stick of the 1st controller
        double rightpower = gamepad1.right_stick_y * acceleration;   //set's a value for power equal to the value of the joysticks for the right stick of the 2nd controller

        // value for the triggers is either 0.0 or 1.0
        double sweeperPower = gamepad1.left_bumper ? 1: gamepad1.left_trigger > 0 ? -1: gamepad2.left_stick_y; //sets the sweeper power equal to the value of the joysticks for the left stick of the 2nd controller
        double launcherPower = gamepad2.y ? 1 : 0;;

        double strafePower = gamepad1.x ? 1 : gamepad1.b ? -1 : 0;

        double servoposition = gamepad2.right_trigger > 0 ? 0.6: 1;

        leftpower = Range.clip(leftpower, -1, 1);        //gamepad controllers have a value of 1 when you push it to its maximum foward
        rightpower = Range.clip(rightpower, -1, 1);      //range of power, min first then max

        sweeperPower = Range.clip(sweeperPower, -1, 1);//range of sweeper motor values is between 0 and 1
        launcherPower = Range.clip(launcherPower, -1, 1);//range of launcher motor values is between 0 and 1

        strafePower = Range.clip(strafePower, -1, 1);//range of launcher motor values is between 0 and 1

        servoposition = Range.clip(servoposition, 0, 1);

        motorFrontL.setPower(leftpower);                    //connects the value for power to the actual power of the motors
        motorFrontR.setPower(rightpower);
        motorBackL.setPower(leftpower);
        motorBackR.setPower(rightpower);

        sweeperMotor.setPower(sweeperPower);
        motorLauncher.setPower(launcherPower);

//        motorStrafe.setPower(strafePower);

        servo.setPosition(servoposition);

        if (gamepad1.a == true){
            motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        /*
        if (gamepad2.a == true && scoring == false){
            motorLauncher.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); //hopefully sets current position as 0
            scoring = true;
            motorLauncher.setTargetPosition(1440); // 1440 ticks = one full rotation
            motorLauncher.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motorLauncher.setPower(1);
            while (motorLauncher.isBusy() && motorLauncher.getCurrentPosition() < 1440) {
                Thread.yield(); //basically idle();
            }
            motorLauncher.setPower(0);
            motorLauncher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//makes it so that we can use it as a normal launching mechanism after one shot
            scoring = false;
        }*/
        // Telemetry for the motor encoders
        telemetry.addData("Front L Motor ", (double) motorFrontL.getCurrentPosition()/1440 + " rotations");
        telemetry.addData("Front R Motor ", (double) motorFrontR.getCurrentPosition()/1440 + " rotations");
        telemetry.addData("Back L Motor ", (double) motorBackL.getCurrentPosition()/1440 + " rotations");
        telemetry.addData("Back R Motor ", (double) motorBackR.getCurrentPosition()/1440 + " rotations");
        telemetry.addData("Acceleration ", acceleration);
        //telemetry.addData("Left Motor Power: ", leftpower);           //shows the data or text stated onto phone telemetry
        //telemetry.addData("Right Motor Power:", rightpower);
        telemetry.addData("Sweeper Power ", sweeperPower);
        telemetry.addData("Launcher Power ", launcherPower);
//        telemetry.addData("Strafe Power ", strafePower);
        telemetry.addData("Servo Position ", servoposition);
        telemetry.addData("Launcher Position ", motorLauncher.getCurrentPosition());
    }
}
