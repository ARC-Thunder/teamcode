package org.firstinspires.ftc.teamcode;

/**
 * Created by Alex on 3/10/2017.
 *
 * This is a test to demonstrate Java's inheritance.
 *
 * Implementing inheritance will make coding autonomous easier because inheritance allows
 * one file to store all of the functions, and separate files inherit those functions, making
 * code much easier to read and understand while also de-cluttering the java file.
 *
 */

public class EncoderInheritanceTest extends EncoderAutoBasicMethods{

    @Override
    public void runOpMode() throws InterruptedException {
        super.runOpMode();

        encoderMove(0.2, 6, 6);
    }

}
