package org.firstinspires.ftc.teamcode;

/**
 * Created by Alex on 10/12/2016.
 */

/**
 *
 * NOTE: THIS IS NOT INTENDED TO BE USED AS A FULLY FUNCTIONING AUTONOMOUS. THIS CLASS IS INTENDED
 *       TO BE USED FOR DEMONSTRATIVE / EDUCATIONAL PURPOSES.
 *
 * Some things that I have noticed that are different / better:
 *
 * - Hardware classes can be created so that multiple other classes can use the same hardware
 *   without needing to retype it everytime.
 * - Different alternate way to register op modes.
 * -
 *
 *
 * */
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

//package org.firstinspires.ftc.robotcontroller.external.samples;

@Disabled
@Autonomous(name="Basic Autonomous", group="Autonomous")
/**The line above demonstrates the new way to register opmodes inside the code. This method cannot
 * be used in FTC apps prior to version 2. (Update your phones to have matching version numbers
 * and version 2 of the FTC apps.)
 */
//@Disabled
/**The "@Disabled" in the line above this comment indicates whether or not the opmode will be
 * shown in the opmode list inside the Driver Station app.
 * */

public class ThunderBasicAuto2016_2017 extends OpMode {
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

    @Override
    public void init() {
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

    }
    @Override
    public void loop() {

    }

}
