package org.firstinspires.ftc.teamcode;

/**
 * Created by Alex on 2/14/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@Autonomous(name = "Vuforia Telemetry", group = "Vuforia")
public class VuforiaTelemetry extends LinearOpMode{

    @Override
    public void runOpMode() throws InterruptedException {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters((R.id.cameraMonitorViewId));
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AfL9DST/////AAAAGcC5dR2bzE/YhAg3ZKntUZN6JDz2Eg0EbS08RJF9d+TNT/sGuL+ZdPv+y/iAkGeuUOyTRhE3X8cd/Z1mvp4RqiFyxNF4uE/zShcGZuMqXjiIsWAu/q76vLt4Qq/7V765NUtW+wb1dD6S8uSiSoDDU/wmhP5AiUw6/tofUpBuqgRr1WfuKTBO/SClC8ed2sWm8wO1ePhuGEG/roGN/CGZFlBqvdafPnKvV2iSeGR5uYfn1Y0NQ+J/fV152LMlF1LIYT8E3mIliK93cC37GrgyHx+BmGIWLOKdW78yJG8OWHgkBqFeKsVLl/Ki0jczkd60O0gl8vZjgW4bSZexrTzErsI5cMcSc6MiWwrCsXwdV+oY";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint((HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS), 4);

        /** Importing all of the vision targets **/
        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Lego");
        beacons.get(3).setName("Gears");

        waitForStart();

        beacons.activate();

        while ((opModeIsActive())) {
            for(VuforiaTrackable beac : beacons) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beac.getListener()).getPose();

                if(pose != null) {
                    VectorF translation = pose.getTranslation();

                    telemetry.addData(beac.getName() + "-Translation", translation);

                    //atan2 rounds the arc tangent (y/x) to the closets double
                    //multiply the translation of the z axis by -1 if the phone is in the front of the autonomous, leave it with no multiplication if the phone is in the back
                    double degreesToTurn = Math.toDegrees(Math.atan2(translation.get(1), -1* translation.get(2))); //if phone is vertical to represent the x axis and z axis respectively. landscape mode if in screen rotation lock will want 0 instead of 1 for the first translation or x; vertical mode will be the 1 instead of 0
                    double degreesToParallel = 90-Math.abs(degreesToTurn);
                    
                    telemetry.addData(beac.getName() + "-Degrees", degreesToTurn);
                    telemetry.addData(beac.getName() + "-Degrees to Parallel", degreesToParallel);
                    telemetry.addData("X Coord", translation.get(1));
                    telemetry.addData("Y Coord", -1*translation.get(0));
                    telemetry.addData("Z Coord", -1 * translation.get(2));
                }
            }
            telemetry.update();
        }
    }
}
