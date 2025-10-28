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
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


//This defines what this will be called on the Driver Hub
@Autonomous(name="QuantumCodersAutonomous", group="StarterBot")
public class QuantumCodersAutonomous extends LinearOpMode {


    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    //This is overriding the code that is normally here
    @Override
    public void runOpMode() {

        //These are connecting the idea of "motor" in the code to the motors on the bot
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        //These are being reversed because the drive motors start reversed
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        leftDrive.setDirection(DcMotor.Direction.FORWARD);

//This tells the code to wait for you to start on the Driver hub
        waitForStart();

        //This is lowering the power so it doesn't get way to fast
        leftDrive.setPower(0.5);
        rightDrive.setPower(0.5);



    }
}