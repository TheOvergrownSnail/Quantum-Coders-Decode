//Commenting is used by adding // to the start of a line.
/* For comments that you want to last longer than one line.
 *add aterisks to every line
 *
 *
 *
 * At the end use */

//This tells the code where it should be found on android studio
package org.firstinspires.ftc.teamcode;

/*This is importing everything that is used for ftc because android studio is
an app making studio
*/

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;


//This defines what this will be called on the Driver Hub

@Autonomous(name="QuantumCodersAutonomous", group="StarterBot")
public class QuantumCodersAutonomous extends LinearOpMode {


    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotorEx launcher = null;
    private CRServo leftFeeder = null;
    private CRServo rightFeeder = null;

    private ElapsedTime feederTimer = new ElapsedTime();

    //This is overriding the code that is normally here
    @Override
    public void runOpMode() {

        //These are connecting the idea of "motor" in the code to the motors on the bot
        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        launcher = hardwareMap.get(DcMotorEx.class,"launcher");
        leftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
        rightFeeder = hardwareMap.get(CRServo.class, "right_feeder");

        ElapsedTime feederTimer = new ElapsedTime();
        final double WAIT_1SEC = 1;
        final double FEED_TIME = 0.20;

        //These are being reversed because the drive motors start reversed
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);

        final double LAUNCHER_TARGET_VELOCITY = 1125;
        final double LAUNCHER_MIN_VELOCITY = 1075;
    //This tells the code to wait for you to start on the Driver hub
        waitForStart();

        launcher.setPower(1);
        launcher.setVelocity(500);
        feederTimer.reset();
        while (feederTimer.seconds() < 1) {
            if (feederTimer.seconds() > 1) {
                continue;
            }
        }
        leftFeeder.setPower(50);
        rightFeeder.setPower(-50);
        sleep(2000);

        //This is lowering the power so it doesn't get way too fast
        frontRightDrive.setPower(0.5);
        frontLeftDrive.setPower(0.5);
        backRightDrive.setPower(0.5);
        backLeftDrive.setPower(0.5);
        sleep(3000);

        frontRightDrive.setPower(0);
        frontLeftDrive.setPower(0);
        backRightDrive.setPower(0);
        launcher.setPower(0);
    }
}