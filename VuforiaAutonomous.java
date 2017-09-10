package org.firstinspires.ftc.teamcode;

/**
 * Created by Alex on 10/25/2016.
 */
import android.graphics.drawable.GradientDrawable;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
/** Few Important Notes About Matrices:
 *
 *  - The origin must be defined as someplace on the field. This can be at any location, but
 *    should be easy to find
 *  - x, y, and z represent the value for each's corresponding axis
 *  - u, v, and w correspond to rotational amounts.
 *  - Default x, y, z measurements are millimeters (i.e. moving an object 1/2 a meter along the
 *    y-axis would be 0, 500, 0
 *  - Default orientation of an object is facing up and top of the face facing in the y direction
 *  - Rotations follow the right-hand rule
 *  - Order of rotations does matter!
 *
 * */
@Autonomous(name = "Vuforia v1")
public class VuforiaAutonomous extends LinearOpMode
{
    /** -----VARIABLE SETUP----------------------------------------------------- */
    /* These are variables for later use */
    private VuforiaLocalizer vuforiaLocalizer;
    private VuforiaLocalizer.Parameters parameters;
    private VuforiaTrackables visionTargets;
    private VuforiaTrackable target;
    private VuforiaTrackableDefaultListener listener;

    private OpenGLMatrix lastKnownLocation;             // Creates a matrix called lastKnownLocation
    private OpenGLMatrix phoneLocation;                 // Creates a matrix called phoneLocation


    public static final String VUFORIA_KEY = "AfL9DST/////AAAAGcC5dR2bzE/YhAg3ZKntUZN6JDz2Eg0EbS08RJF9d+TNT/sGuL+ZdPv+y/iAkGeuUOyTRhE3X8cd/Z1mvp4RqiFyxNF4uE/zShcGZuMqXjiIsWAu/q76vLt4Qq/7V765NUtW+wb1dD6S8uSiSoDDU/wmhP5AiUw6/tofUpBuqgRr1WfuKTBO/SClC8ed2sWm8wO1ePhuGEG/roGN/CGZFlBqvdafPnKvV2iSeGR5uYfn1Y0NQ+J/fV152LMlF1LIYT8E3mIliK93cC37GrgyHx+BmGIWLOKdW78yJG8OWHgkBqFeKsVLl/Ki0jczkd60O0gl8vZjgW4bSZexrTzErsI5cMcSc6MiWwrCsXwdV+oY";
        /* The above is the place where the Vuforia license key is put, and this is mandatory
        /*  for the code to run. */

    private float robotX = 0;           // Tracks the robot's x location
    private float robotY = 0;           // Tracks the robot's y location
    private float robotAngle = 0;       // Tracks the robot's angle
    /** ------------------------------------------------------------------------- */

    public void runOpMode() throws InterruptedException
    {
        setupVuforia();                                         // Calls the method setupVuforia

        lastKnownLocation = createMatrix(0, 0, 0, 0, 0, 0);
            /* Since the lsat known location of the robot is unknown (as of starting), we will
             * set it to all 0's. If this is not set here, it will cause problems later.*/

        waitForStart();                                         // Waits for start to be pressed

        visionTargets.activate();                               // Start tracking vision targets

        while (opModeIsActive())
        {
            OpenGLMatrix latestLocation = listener.getUpdatedRobotLocation();
                /* Asks the listener for info on where the robot is. */


            if(latestLocation != null)
            {
                lastKnownLocation = latestLocation;
            }
                /* If the listener returns null, then the last known location will not update for
                 * reason of errors */

            float[] coordinates = lastKnownLocation.getTranslation().getData();

            robotX = coordinates[0];
            robotY = coordinates[1];
            robotAngle = Orientation.getOrientation(lastKnownLocation, AxesReference.EXTRINSIC,
                    AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

    /** -----ADD TELEMETRY DATA HERE--------------------------------------------- */
            telemetry.addData("Tracking " + target.getName(), listener.isVisible());
                // Sends info about whether the target is visible
            telemetry.addData("Last Known Location", formatMatrix(lastKnownLocation));
                // Sends info about the robot's last known location
    /** ------------------------------------------------------------------------- */

            telemetry.update();
                // Send telemetry info
            idle();
                // Wait for hardware to catch up
        }
    }

    public void setupVuforia()
    /** This method sets up Vuforia on the phone. */
    {
        /** -----VUFORIA SETUP------------------------------------------------------- */
        parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
            /* The above will activate the smartphone camera Vuforia display, which will drain
             * battery life a lot quicker. To disable this, remove the "R.id.cameraMonitorViewId"
             * above from the parameters. */
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
            /* Sets the required license key to the key listed above */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
            /* The last part of the code above allows the user to change which camera the
             * code and Vuforia use.*/
        vuforiaLocalizer = ClassFactory.createVuforiaLocalizer(parameters);
        /** ------------------------------------------------------------------------- */


        /** -----VISION TARGETS------------------------------------------------------ */
        visionTargets = vuforiaLocalizer.loadTrackablesFromAsset("FTC_2016-17");
            /* Indicates where the xml file containing locations of the trackable
             * pictures are. This xml file also contains the names of each for reference. */
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_OBJECT_TARGETS, 4);
            /* Changes the maximum amount of vision targets that the robot will be tracking
             * through the number at the end of the line. */
        /** ------------------------------------------------------------------------- */


        /** -----TARGET SETUP-------------------------------------------------------- */
        target = visionTargets.get(0);
            /* Tells which target to track. The "0" corresponds to the "Wheels" target. */
        target.setName("Wheels Target");
            /* This sets the name of the target in the class for the code. */
        target.setLocation(createMatrix(0, 0, 0, 0, 0, 0));
            /* This line sets the location of the target. It is currently set at all 0
             * because the real is not known yet. */
        /** ------------------------------------------------------------------------- */

        phoneLocation = createMatrix(0, 0, 0, 0, 0, 0);
            // Sets the phone location on the robot.

        /** -----LISTENER SETUP------------------------------------------------------ */
        listener = (VuforiaTrackableDefaultListener) target.getListener();
        listener.setPhoneInformation(phoneLocation, parameters.cameraDirection);
        /** ------------------------------------------------------------------------- */

    }

    public OpenGLMatrix createMatrix(float x, float y, float z, float u, float v, float w)
    /** This method creates a matrix for any object. */
    {
        return OpenGLMatrix.translation(x, y, z).
                multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, u, v, w));
    }

    public String formatMatrix(OpenGLMatrix matrix)
    /** This method formats a matrix into a string for display, use with telemetry. */
    {
        return matrix.formatAsTransform();
    }
}
