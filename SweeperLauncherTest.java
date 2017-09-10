package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 *Created by Lake Yin on 11/16/2016.
 */

@TeleOp(name="SweeperLauncherTest", group="TeleOp")

public class SweeperLauncherTest extends OpMode {

    private DcMotorController motorControllerP4;
    private DcMotor sweeperMotor;
    private DcMotor launcherMotor;

    @Override
    public void init() {
        motorControllerP4 = hardwareMap.dcMotorController.get("MCP4"); //hardwaremapping the motor controller
        launcherMotor = hardwareMap.dcMotor.get("motorLauncher"); //hardwaremapping the motor for the launcher
        sweeperMotor = hardwareMap.dcMotor.get("motorSweeper"); //hardwaremapping the motor for the sweeper

        launcherMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sweeperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void loop(){ //50 loops = 1 second

        // value for the triggers is either 0.0 or 1.0
        double sweeperPower = gamepad2.left_trigger; //sets the sweeper power equal to the state of the left trigger on the second controller
        double launcherPower = gamepad2.right_trigger; //sets the launcher power equal to the state of the right trigger on the second controller

        sweeperPower = Range.clip(sweeperPower, -1, 1);//range of sweeper motor values is between 0 and 1
        launcherPower = Range.clip(launcherPower, -1, 1);//range of launcher motor values is between 0 and 1

        sweeperMotor.setPower(sweeperPower);
        launcherMotor.setPower(launcherPower);

        telemetry.addData("SweeperLauncherTest", "Sweeper Power " + sweeperPower); //shows sweeper data after comma in phone
        telemetry.addData("SweeperLauncherTest", "Launcher Power " + launcherPower); //shows launcher data after comma in phone

    }
}
