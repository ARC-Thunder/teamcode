package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by citruseel on 11/2/2016.
 */

@TeleOp(name="ServoTest", group="TeleOp")

public class ServoTest extends OpMode {

    private ServoController servoController;
    private Servo testing;
    private double servoposition = 0;//start position

    @Override
    public void init() {
        servoController = hardwareMap.servoController.get("SCP2"); //hardwaremapping the servo controller
        testing = hardwareMap.servo.get("servo"); //hardwaremapping the servo
    }

    public void loop(){ //50 loops = 1 second

        servoposition=Range.clip(servoposition, -1, 1);//range of servo values is between 0 and 1

        if(gamepad2.left_stick_x > 0) {
           servoposition = servoposition + 0.008; //1 second adds 2/5 to the servo position...... 2.5 seconds to do a full movement from left max to right max
        }
        if(gamepad2.left_stick_x < 0) {
            servoposition = servoposition - 0.008;
        }
        
        /**
        *   Resetting Servo Code
        *     - Servo will go back to the middle if neither button is pressed
        *
        if(gamepad2.x == true && gamepad2.b == false) {
            servoposition = 0.05;
        }
        if(gamepad2.b == true && gamepad2.x == false) {
            servoposition = 0.95;
        }
        if (gamepad2.b == false && gamepad2.x == false) {
            servoposition = 0.5;
        }
        
        */

        testing.setPosition(servoposition); //constantly updates the servo's position every 20ms

        telemetry.addData("ServoTest", "Servo Postition " + servoposition); //shows data after comma in phone

    }
}
