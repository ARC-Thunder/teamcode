package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;

/**
 * Created by citruseel on 2/22/2017.
 */
//When the phone is in landscape without potrait lock: x = [1], y = -1*[0], z = -1 * [2]
@Autonomous(name="Vuforia Combined With Encoder To Calculate The Difference In Translation Autonomous So That We Align Perfectly In Front Of The Beacon", group="Vuforia")
public class VuforiaAutoDeltaTrans extends LinearOpMode {
    private DcMotorController motorControllerL, motorControllerR, motorControllerA1, motorControllerA2;
    private DcMotor motorFrontL, motorFrontR, motorBackL, motorBackR;

    int ticksPerRev = 1120;             // This is the specific value for AndyMark motors
    double ticksPer360Turn = 4500;         // The amount of ticks for a 360 degree turn
    double tickTurnRatio = ticksPer360Turn / 360;

    double wheelDiameter = 101.6;         // Diameter of the current omniwheels in millimeters; 4 inches
    double phoneDisplacement = 210; //10 = 1cm; 1 = 1mm; from the center to the front (along the z axis when the robot is facing the target)
    double wheelCircumference =  wheelDiameter * Math.PI; //1 = 1mm

    double ticksPerInch = (ticksPerRev / (wheelCircumference));

    double inchToMm = 25.4;             // For conversion between the vectors

    double degreesToTurn;
    double degreesToParallel;

    public void runOpMode()throws InterruptedException{

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId); //shows on phone screen what camera sees, remove the stuff in parentheses to remove this feature
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK; //sets the camera being used as the back
        params.vuforiaLicenseKey = "AQRacK7/////AAAAGea1bsBsYEJvq6S3KuXK4PYTz4IZmGA7SV88bdM7l26beSEWkZTUb8H352Bo/ZMC6krwmfEuXiK7d7qdFkeBt8BaD0TZAYBMwHoBkb7IBgMuDF4fnx2KiQPOvwBdsIYSIFjiJgGlSj8pKZI+M5qiLb3DG3Ty884EmsqWQY0gjd6RNhtSR+6oiXazLhezm9msyHWZtX5hQFd9XoG5npm4HoGaZNdB3g5YCAQNHipjTm3Vkf71rG/Fffif8UTCI1frmKYtb4RvqiixDSPrD6OG6YmbsPOYUt2RZ6sSTreMzVL76CNfBTzmpo2V0E6KKP2y9N19hAum3GZu3G/1GEB5D+ckL/CXk4JM66sJw3PGucCs";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES; //can be teapot, axes, buildings, or none; shows feedback on the camera monitor
        //blue = z, green = y, red = x


        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params); //creates vuforia
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4); //sets the maximum amoung of targets and makes it so if it sees nothing it won't throw an exception

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17"); //wher to get the trackable assets from,
        beacons.get(0).setName("Wheels");//seperates the trackables so each can be read seperately
        beacons.get(1).setName("Tools");//seperates the trackables so each can be read seperately
        beacons.get(2).setName("Legos");//seperates the trackables so each can be read seperately
        beacons.get(3).setName("Gears");//seperates the trackables so each can be read seperately https://firstinspiresst01.blob.core.windows.net/ftc/gears.pdf

        VuforiaTrackableDefaultListener wheels = (VuforiaTrackableDefaultListener) beacons.get(0).getListener();//the default listerner is set to track the wheels

        initElectronics();

        runWithoutEncoders();

        waitForStart();

        beacons.activate();

        addTelemetryData("Sees Target", "No");

        OpenGLMatrix pose = wheels.getPose();

        while (opModeIsActive() && pose == null){
            pose = wheels.getPose();
            motorFrontR.setPower(0.2);
            motorFrontL.setPower(0.2);
            motorBackR.setPower(0.2);
            motorBackL.setPower(0.2);
        }

        stopMotion();

        addTelemetryData("Sees Target", "Yes");

        VectorF translation = pose.getTranslation();

        degreesToTurn = Math.toDegrees(Math.atan2(translation.get(1), -1 * translation.get(2)));
        degreesToParallel = 90-Math.abs(degreesToTurn);

        while (opModeIsActive() && pose != null ){
            pose = wheels.getPose();
            motorFrontR.setPower(-0.2);
            motorFrontL.setPower(0.2);
            motorBackR.setPower(-0.2);
            motorBackL.setPower(0.2);
        }

        stopAndResetEncoders();
        rotateDegreesLeft(0.1, 10);

        stopMotion();
        addTelemetryData("Parallel", "Yes");

        stopAndResetEncoders();
        rotateDegreesRight(0.1, 90);

        stopMotion();
        addTelemetryData("Facing beacon", "Yes");

        //had to rotate first to parallel to beacon in order to get accurate results
        double xcoord = translation.get(1);
        double zcoord = translation.get(2) * -1;

        stopAndResetEncoders();
        rotateDegreesLeft(0.1, 90);

        stopMotion();
        addTelemetryData("Facing beacon", "No");

        stopAndResetEncoders();
        encoderMove(0.1, -1 * xcoord / inchToMm, -1 * xcoord / inchToMm );

        stopMotion();
        addTelemetryData("X coord is 0", "Yes");

        stopAndResetEncoders();
        encoderMove(0.1, -1 * phoneDisplacement / inchToMm, -1 * phoneDisplacement / inchToMm );

        stopMotion();
        addTelemetryData("X coord is 0", "Yes");

        stopAndResetEncoders();
        encoderMove(0.1, -1 * (zcoord - 50)/ inchToMm, -1 * (zcoord - 50)/ inchToMm);

        stopMotion();
        addTelemetryData("Aligned", "Yes");

    }
    public void initElectronics(){
        motorControllerL = hardwareMap.dcMotorController.get("MC_L");
        motorControllerR = hardwareMap.dcMotorController.get("MC_R");
        motorControllerA1 = hardwareMap.dcMotorController.get("MC_A1");
        motorControllerA2 = hardwareMap.dcMotorController.get("MC_A2");

        motorFrontL = hardwareMap.dcMotor.get("motorFrontL");
        motorFrontR = hardwareMap.dcMotor.get("motorFrontR");
        motorBackL = hardwareMap.dcMotor.get("motorBackL");
        motorBackR = hardwareMap.dcMotor.get("motorBackR");

        motorFrontL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackL.setDirection(DcMotorSimple.Direction.REVERSE);

        stopAndResetEncoders();

    }

    public void stopAndResetEncoders() {
        /** Resets the encoder values on each of the drive motors **/
        motorFrontL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runToPositionEncoders() {
        /** Sets the encoded motors to RUN_TO_POSITION **/
        motorFrontL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void runUsingEncoders() {
        /** Sets the encoders to RUN_USING_ENCODERS **/
        motorFrontL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void runWithoutEncoders() {
        /** Sets the encoders to RUN_USING_ENCODERS **/
        motorFrontL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void addTelemetryData(String string1, String string2) {
        telemetry.addData(string1, string2);
        telemetry.update();
    }

    public void stopMotion() {
        /** Stops all drive motor motion **/
        motorFrontL.setPower(0);
        motorFrontR.setPower(0);
        motorBackL.setPower(0);
        motorBackR.setPower(0);
    }

    public void waitTillPosition(){
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()) {

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }
        stopMotion();
    }

    public void setTargetPosition(int position){
        motorFrontL.setTargetPosition(position);
        motorFrontR.setTargetPosition(position);
        motorBackL.setTargetPosition(position);
        motorBackR.setTargetPosition(position);
    }
    public void encoderMove(double power,
                            double leftInches, double rightInches) {
        /** This method makes the motors move a certain distance **/

        // Sets the power range
        power = Range.clip(power, -1, 1);
        power = Math.abs(power);

        // Assigning variables
        int leftTarget = (int)(leftInches * -ticksPerInch);     // Value must be negative to go forward
        int rightTarget = (int)(rightInches * -ticksPerInch);


        // Setting the target positions
        motorFrontL.setTargetPosition(motorFrontL.getCurrentPosition() + leftTarget);
        motorFrontR.setTargetPosition(motorFrontR.getCurrentPosition() + rightTarget);
        motorBackL.setTargetPosition(motorBackL.getCurrentPosition() + leftTarget);
        motorBackR.setTargetPosition(motorBackR.getCurrentPosition() + rightTarget);

        runToPositionEncoders();

        // Sets the motors' position
        motorFrontL.setPower(power);
        motorFrontR.setPower(power);
        motorBackL.setPower(power);
        motorBackR.setPower(power);

        // While loop for updating telemetry
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()){

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }

        // Stops the motors
        stopMotion();

        // Resets to run using encoders mode
        runUsingEncoders();

    }

    public void rotateDegreesLeft(double power, double robotDegrees) {
        /** Robot requires values of...
         *  360 degrees =~ 4500 ticks
         *  180 degrees =~ 2250 ticks  **/

        /** This method, given an input amount of degrees, makes the robot turn
         *  the amount of degrees specified around ITS center of rotation **/

        // Sets the power range
        power = Range.clip(power, -1, 1);
        power = Math.abs(power);

        // Setting variables
        double robotTurn = robotDegrees * tickTurnRatio;

        // Setting the target positions
        motorFrontL.setTargetPosition((int)(motorFrontL.getCurrentPosition() + robotTurn));
        motorFrontR.setTargetPosition((int)(motorFrontR.getCurrentPosition() + -robotTurn));
        motorBackL.setTargetPosition((int)(motorBackL.getCurrentPosition() + robotTurn));
        motorBackR.setTargetPosition((int)(motorBackR.getCurrentPosition() + -robotTurn));

        runToPositionEncoders();

        // Sets the motors' positions
        motorFrontL.setPower(power);
        motorFrontR.setPower(power);
        motorBackL.setPower(power);
        motorBackR.setPower(power);

        // While loop for updating telemetry
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()){

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }

        // Stops the motors
        stopMotion();

        // Resets to run using encoders mode
        runUsingEncoders();
    }

    public void rotateDegreesRight(double power, double robotDegrees) {
        /** Robot requires values of...
         *  360 degrees =~ 4600 ticks
         *  180 degrees =~ 2300 ticks  **/

        /** This method, given an input amount of degrees, makes the robot turn
         *  the amount of degrees specified around ITS center of rotation **/

        // Sets the power range
        power = Range.clip(power, -1, 1);
        power = Math.abs(power);

        // Setting variables
        double robotTurn = robotDegrees * tickTurnRatio;

        // Setting the target positions
        motorFrontL.setTargetPosition((int)(motorFrontL.getCurrentPosition() + -robotTurn));
        motorFrontR.setTargetPosition((int)(motorFrontR.getCurrentPosition() + robotTurn));
        motorBackL.setTargetPosition((int)(motorBackL.getCurrentPosition() + -robotTurn));
        motorBackR.setTargetPosition((int)(motorBackR.getCurrentPosition() + robotTurn));

        runToPositionEncoders();

        // Sets the motors' positions
        motorFrontL.setPower(power);
        motorFrontR.setPower(power);
        motorBackL.setPower(power);
        motorBackR.setPower(power);

        // While loop for updating telemetry
        while(motorFrontL.isBusy() && motorFrontR.isBusy() &&
                motorBackL.isBusy() && motorBackR.isBusy() && opModeIsActive()){

            // Updates the position of the motors
            double frontLPos = motorFrontL.getCurrentPosition();
            double frontRPos = motorFrontR.getCurrentPosition();
            double backLPos = motorBackL.getCurrentPosition();
            double backRPos = motorBackR.getCurrentPosition();

            // Adds telemetry of the drive motors
            telemetry.addData("MotorFrontL Pos", frontLPos);
            telemetry.addData("MotorFrontR Pos", frontRPos);
            telemetry.addData("MotorBackL Pos", backLPos);
            telemetry.addData("MotorBackR Pos", backRPos);

            // Updates the telemetry
            telemetry.update();

        }

        // Stops the motors
        stopMotion();

        // Resets to run using encoders mode
        runUsingEncoders();
    }
}
