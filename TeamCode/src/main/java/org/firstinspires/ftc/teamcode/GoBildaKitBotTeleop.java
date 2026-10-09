/*
 * Copyright (c) 2025 FIRST
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "GoBildaKitBotTeleop", group = "StarterBot")
public class GoBildaKitBotTeleop extends OpMode {
    final double FEED_TIME_SECONDS = 0.20;
    final double STOP_SPEED = 0.0;
    final double FULL_SPEED = 1.0;

    // 1. PLACE VARIABLE CHANGES HERE: We removed 'final' so these variables can change
    double launcherTargetVelocity = 1600;
    double launcherMinVelocity = 1400;

    // 2. PLACE RISING EDGE BUTTON STATES HERE: Prevents the speed from jumping infinitely
    boolean lastDpadUp = false;
    boolean lastDpadDown = false;

    // Declare OpMode members
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;
    private DcMotorEx launcher = null;
    private CRServo leftFeeder = null;
    private CRServo rightFeeder = null;
    ElapsedTime feederTimer = new ElapsedTime();

    private enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    private LaunchState launchState;
    double leftPower;
    double rightPower;
    double update = 0;

    @Override
    public void init() {
        launchState = LaunchState.IDLE;

        frontLeftDrive = hardwareMap.get(DcMotor.class, "front_left_drive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "back_left_drive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "front_right_drive");
        backRightDrive = hardwareMap.get(DcMotor.class, "back_right_drive");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        leftFeeder = hardwareMap.get(CRServo.class, "left_feeder");
        rightFeeder = hardwareMap.get(CRServo.class, "right_feeder");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 5, 0, 0));

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // Tank Drive controls
        leftPower  = -gamepad1.left_stick_y;
        rightPower = -gamepad1.right_stick_y;

        frontLeftDrive.setPower(leftPower);
        backLeftDrive.setPower(leftPower);
        frontRightDrive.setPower(rightPower);

        backRightDrive.setPower(rightPower);

        // 3. PLACE ADJUSTMENT CONTROLS HERE: Increments/decrements thresholds when pressed
        if (gamepad1.dpad_up && !lastDpadUp) {
            launcherTargetVelocity += 100; // Increase speed setting by 100
            launcherMinVelocity += 100;
        }
        if (gamepad1.dpad_down && !lastDpadDown) {
            launcherTargetVelocity -= 100; // Decrease speed setting by 100
            launcherMinVelocity -= 100;
        }
        // Store current states for edge checking
        lastDpadUp = gamepad1.dpad_up;
        lastDpadDown = gamepad1.dpad_down;

        // Firing buttons
        boolean shotRequested = gamepad1.right_bumper;
        boolean shotCancel = gamepad1.left_bumper;

        launch(shotRequested, shotCancel);

        // 4. PLACE TELEMETRY UPDATES HERE: View settings live on your Driver Station
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("SETTING - Target Vel", launcherTargetVelocity);
        telemetry.addData("REAL TIME - Motor Vel", launcher.getVelocity());
        telemetry.addData("Current Launch State", launchState);
    }

    /*
     * Launcher State Machine Logic
     */
    void launch(boolean shotRequested, boolean shotCancel) {
        switch (launchState) {
            case IDLE:
                launcher.setVelocity(0);
                if (shotRequested) {
                    launchState = LaunchState.SPIN_UP;
                }
                break;

            case SPIN_UP:
                // 5. VARIABLES USED HERE: State machine calls dynamic values rather than static targets
                launcher.setVelocity(launcherTargetVelocity);
                update = 1 + update;

                if (launcher.getVelocity() > launcherMinVelocity) {
                    launchState = LaunchState.LAUNCH;
                }
                if (shotCancel) {
                    launchState = LaunchState.IDLE;
                }
                break;

            case LAUNCH:
                leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                feederTimer.reset();
                launchState = LaunchState.LAUNCHING;
                break;

            case LAUNCHING:
                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    leftFeeder.setPower(STOP_SPEED);
                    rightFeeder.setPower(STOP_SPEED);
                    launchState = LaunchState.IDLE;
                }
                if (shotCancel) {
                    leftFeeder.setPower(STOP_SPEED);
                    rightFeeder.setPower(STOP_SPEED);
                    launchState = LaunchState.IDLE;
                }
                break;
        }
    }
}