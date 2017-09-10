package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by citruseel on 12/31/2016.
 */

@Autonomous(name="Vuforia Full?", group="Vuforia")

public class VuforiaOp extends LinearOpMode {

    public String phase = "Start";

    private DcMotorController motorControllerL;    // left motor controllers
    private DcMotorController motorControllerR;    // right motor controllers
    private DcMotorController motorControllerA1;   // Scoring motor controller

    private DcMotor motor1;
    private DcMotor motor2;
    private DcMotor motor3;
    private DcMotor motor4;
    private DcMotor sweeperMotor;
    private DcMotor launcherMotor;

    private DeviceInterfaceModule interfaceModule; //stated interface module

    ColorSensor colorSensor; //stated colorsensor

    //Each color sensor has it's own I2cAddress, they need to have unique addresses so the systeme doesn't get confused.
    public static final I2cAddr COLOR_SENSOR_ORIGINAL_ADDRESS = I2cAddr.create8bit(0x3c);//this is to create our own i2c address for some reason

    public void runOpMode() throws InterruptedException{
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId); //shows on phone screen what camera sees, remove the stuff in parentheses to remove this feature
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK; //sets the camera being used as the back
        params.vuforiaLicenseKey = "AQRacK7/////AAAAGea1bsBsYEJvq6S3KuXK4PYTz4IZmGA7SV88bdM7l26beSEWkZTUb8H352Bo/ZMC6krwmfEuXiK7d7qdFkeBt8BaD0TZAYBMwHoBkb7IBgMuDF4fnx2KiQPOvwBdsIYSIFjiJgGlSj8pKZI+M5qiLb3DG3Ty884EmsqWQY0gjd6RNhtSR+6oiXazLhezm9msyHWZtX5hQFd9XoG5npm4HoGaZNdB3g5YCAQNHipjTm3Vkf71rG/Fffif8UTCI1frmKYtb4RvqiixDSPrD6OG6YmbsPOYUt2RZ6sSTreMzVL76CNfBTzmpo2V0E6KKP2y9N19hAum3GZu3G/1GEB5D+ckL/CXk4JM66sJw3PGucCs";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES; //can be teapot, axes, buildings, or none; shows feedback on the camera monitor
        //blue = z, green = y, red = x


        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params); //creates vuforia
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4); //sets the maximum amoung of targets and makes it so if it sees nothing it won't throw an exception

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17"); //where to get the trackable assets from,
        beacons.get(0).setName("Wheels");//seperates the trackables so each can be read seperately
        beacons.get(1).setName("Tools");//seperates the trackables so each can be read seperately
        beacons.get(2).setName("Legos");//seperates the trackables so each can be read seperately
        beacons.get(3).setName("Gears");//seperates the trackables so each can be read seperately https://firstinspiresst01.blob.core.windows.net/ftc/gears.pdf

        //the default listerner is set to track the wheels
        VuforiaTrackableDefaultListener gears = (VuforiaTrackableDefaultListener) beacons.get(3).getListener();

        /*Initializing and mapping electronics*/
        motorControllerL = hardwareMap.dcMotorController.get("MC_L");
        motorControllerR = hardwareMap.dcMotorController.get("MC_R");
        motorControllerA1 = hardwareMap.dcMotorController.get("MC_A1");


        motor1 = hardwareMap.dcMotor.get("motorFrontL");        //P0
        motor2 = hardwareMap.dcMotor.get("motorFrontR");        //P1
        motor3 = hardwareMap.dcMotor.get("motorBackL");         //P0
        motor4 = hardwareMap.dcMotor.get("motorBackR");         //P1

        launcherMotor = hardwareMap.dcMotor.get("motorLauncher"); //P0
        sweeperMotor = hardwareMap.dcMotor.get("motorSweeper"); //P1

        /*Setting channel modes*/

        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        launcherMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sweeperMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motor2.setDirection(DcMotorSimple.Direction.REVERSE);
        motor4.setDirection(DcMotorSimple.Direction.REVERSE);

        interfaceModule = hardwareMap.deviceInterfaceModule.get("DIM"); //hardware map the device interface module which controls the color sensor
        colorSensor = hardwareMap.colorSensor.get("Color sensor"); //hardware map the color sensor

        waitForStart();
        //start robot facing the picture

        telemetry.addData("Phase: ", phase);

        colorSensor.enableLed(false);

        beacons.activate(); //begins tracking

        phase = "Finding image";

        motor1.setPower(0.1);
        motor2.setPower(0.1);
        motor3.setPower(0.1);
        motor4.setPower(0.1);

        //if the robot doesn't see wheels
        while(opModeIsActive() && gears.getRawPose() == null){
                idle(); //just keep going forward
        }

        //once you see the picture, stop
        motor1.setPower(0);
        motor2.setPower(0);
        motor3.setPower(0);
        motor4.setPower(0);

        Thread.sleep(1000); //stop for a very short time

        //possible way to approach beacons?
        while(opModeIsActive() && (gears.getPose() == null || Math.abs(gears.getPose().getTranslation().get(0)) > 10)){
            if (gears.getPose() != null){
                //if the robot is very far off the z axis
                if (gears.getPose().getTranslation().get(0) > 0 && gears.getPose().getTranslation().get(2) >= 150){ //if you're facing the right of the picture, then rotate left
                    phase = "Far left rotation";
                    motor1.setPower(0.3);
                    motor2.setPower(0.2);
                    motor3.setPower(0.3);
                    motor4.setPower(0.2);
                    telemetry.update();
                }
                if (gears.getPose().getTranslation().get(0) < 0 && gears.getPose().getTranslation().get(2) >= 150) {
                    phase = "Far right rotation";
                    motor1.setPower(0.2);
                    motor2.setPower(0.3);
                    motor3.setPower(0.2);
                    motor4.setPower(0.3);
                    telemetry.update();
                }

                if (gears.getPose().getTranslation().get(0) > 0 && gears.getPose().getTranslation().get(2) >= 80 && gears.getPose().getTranslation().get(2) < 150){
                    phase = "Left rotation";
                    motor1.setPower(0.3);
                    motor2.setPower(0.1);
                    motor3.setPower(0.3);
                    motor4.setPower(0.1);
                    telemetry.update();
                }
                if (gears.getPose().getTranslation().get(0) < 0 && gears.getPose().getTranslation().get(2) >= 80 && gears.getPose().getTranslation().get(2) < 150) {
                    phase = "Right rotation";
                    motor1.setPower(0.1);
                    motor2.setPower(0.3);
                    motor3.setPower(0.1);
                    motor4.setPower(0.3);
                    telemetry.update();
                }

                //if the robot is very close on the z axis
                if (gears.getPose().getTranslation().get(0) > 0 && gears.getPose().getTranslation().get(2) < 80 && gears.getPose().getTranslation().get(2) >= 10){ //if the phone is facing right of the picture
                    phase = "Close left rotation";
                    motor1.setPower(0.3);
                    motor2.setPower(-0.3);
                    motor3.setPower(0.3);
                    motor4.setPower(-0.3);
                    telemetry.update();
                }
                if (gears.getPose().getTranslation().get(0) < 0 && gears.getPose().getTranslation().get(2) < 80 && gears.getPose().getTranslation().get(2) >= 10) { // if the phone is facing left of the picture
                    phase = "Close right rotation";
                    motor1.setPower(-0.3);
                    motor2.setPower(0.3);
                    motor3.setPower(-0.3);
                    motor4.setPower(0.3);
                    telemetry.update();
                }

                //if the robot is too close on the z axis
                if (gears.getPose().getTranslation().get(2) < 10) { // if the phone is facing left of the picture
                    phase = "Too Close";
                    motor1.setPower(-0.3);
                    motor2.setPower(-0.3);
                    motor3.setPower(-0.3);
                    motor4.setPower(-0.3);
                    telemetry.update();
                }

            }
            else{ //if you lose the picture during the process, rotate to the right
                phase = "Can't See";
                motor1.setPower(-0.3);
                motor2.setPower(0.3);
                motor3.setPower(-0.3);
                motor4.setPower(0.3);
                telemetry.update();
            }
        }

        phase = "Facing Picture";

        motor1.setPower(0);
        motor2.setPower(0);
        motor3.setPower(0);
        motor4.setPower(0);

        telemetry.update();

        Thread.sleep(1000);

        while (opModeIsActive() && colorSensor.blue() == colorSensor.red()){
            phase = "Can't find color";

            motor1.setPower(0.3);
            motor2.setPower(0.3);
            motor3.setPower(0.3);
            motor4.setPower(0.3);

            telemetry.update();
        }

        //if team is red
        if (colorSensor.blue() > colorSensor.red()){
            rotateLeft(0.5,1000);

            phase = "Done";

            motor1.setPower(0);
            motor2.setPower(0);
            motor3.setPower(0);
            motor4.setPower(0);

            telemetry.update();
        }
        else if (colorSensor.blue() > colorSensor.red()){
            rotateRight(0.5,1000);

            phase = "Done";

            motor1.setPower(0);
            motor2.setPower(0);
            motor3.setPower(0);
            motor4.setPower(0);

            telemetry.update();
        }


        phase = "Can't Find Beacons";
        telemetry.update();

    }

    public void rotateRight(double power, long time) throws InterruptedException{
        motor1.setPower(power);
        motor2.setPower(-power);
        motor3.setPower(power);
        motor4.setPower(-power);
        Thread.sleep(time);
    }
    public void rotateLeft(double power, long time) throws InterruptedException{
        motor1.setPower(-power);
        motor2.setPower(power);
        motor3.setPower(-power);
        motor4.setPower(power);
        Thread.sleep(time);
    }

}
