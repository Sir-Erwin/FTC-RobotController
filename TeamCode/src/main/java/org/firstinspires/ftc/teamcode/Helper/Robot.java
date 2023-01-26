package org.firstinspires.ftc.teamcode.Helper;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

//Related to IMU

import java.util.*;

// Related to vision
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

public class Robot {
    /*
    Properties that describe hardware.
     */
    private ElapsedTime runtime = new ElapsedTime();
    double timeout_ms = 0;

    public Claw claw = new Claw();
    public Arm arm = new Arm();
    public Chassis chassis = new Chassis();
    public VSlider vSlider = new VSlider();

    public double armHoldingPower = 1;

    private int robotInUse = 2022;

    private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    public static final String[] LABELS = {
            "1 Bolt",
            "2 Bulb",
            "3 Panel"
    };

    private static final String VUFORIA_KEY =
            "AWtcstb/////AAABmfYaB2Q4dURcmKS8qV2asrhnGIuQxM/ioq6TnYqZseP/c52ZaYTjs4/2xhW/91XEaX7c3aw74P3kGZybIaXued3nGShb7oNQyRkVePnFYbabnU/G8em37JQrH309U1zOYtM3bEhRej91Sq6cf6yLjiSXJ+DxxLtSgWvO5f+wM3Wny8MbGUpVSiogYnI7UxEz8OY88d+hgal9u3GhhISdnNucsL+fRAE8mKwT1jGDgUVE1uAJoZFvo95AJWS2Yhdq/N/HpxEH3sBXEm99ci+mdQsl0m96PMCDfV5RgWBjhLbBEIJyQ/xKAbw5Yfr/AKCeB86WDPhR3+Mr8BUvsrycZA6FDJnN5sZZwTg0ZE22+gFL";
    public VuforiaLocalizer vuforia; //vuforia object stored in vision class.
    public TFObjectDetector tfod; //tfod object stored in vision class.

    /* local OpMode members. */
    //Init hardware map
    HardwareMap hwMap = null;


    public ElapsedTime period = new ElapsedTime();
    //tells you how long the robot has run for



    public void init(HardwareMap ahwMap) throws InterruptedException {
        hwMap = ahwMap;
        claw.init(hwMap);
        chassis.init(hwMap);
        arm.init(hwMap);
        vSlider.init(hwMap);

        if(robotInUse == 2021) {
            arm.motor.setDirection(DcMotorSimple.Direction.REVERSE);
            vSlider.motor.setDirection(DcMotorSimple.Direction.REVERSE);
            claw.servo.setDirection(Servo.Direction.FORWARD);
        } else if (robotInUse == 2022) {
            arm.motor.setDirection(DcMotorSimple.Direction.REVERSE);
            vSlider.motor.setDirection(DcMotorSimple.Direction.FORWARD);
            claw.servo.setDirection(Servo.Direction.REVERSE);
        }


    }

    public void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }

    public  void initTfod() {
        int tfodMonitorViewId = hwMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hwMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.7f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        // chassis.tfod.loadMo  delFromFile(tfodPath, LABELS);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);


        if (tfod != null) {
            tfod.activate();

            tfod.setZoom(1.1, 16.0 / 9.0);
        }
    }





    public float modAngle(float angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    //Turns the robot
    public void turnRobotToAngle(float endAngle) {
        org.firstinspires.ftc.robotcore.external.navigation.Orientation angle;
        angle = chassis.imu.getAngularOrientation();

        float angleStart = modAngle(angle.firstAngle);
        float angleEnd = modAngle(endAngle);
        float angleCurrent = angleStart;
        float direction = 0;

        if(modAngle((angleEnd - angleCurrent)) >= 180) {
            //Go Clockwise
            direction = -1;
        } else if (modAngle((angleEnd - angleCurrent)) <= 180) {
            //Go Counter Clockwise
            direction = 1;
        }

        double pwr = -0.5;


        while (Math.abs(angleCurrent - angleEnd) > 5) {
            chassis.FLMotor.setPower(-pwr * direction);
            chassis.FRMotor.setPower(pwr * direction);
            chassis.BLMotor.setPower(-pwr * direction);
            chassis.BRMotor.setPower(pwr * direction);
            angle = chassis.imu.getAngularOrientation();
            angleCurrent = modAngle(angle.firstAngle);

        }
        chassis.stopDriveMotors();
    }




    public void Park(int location) {
        if (location == 1) {
            claw.servo.setPosition(0);
            chassis.DriveToPosition(0.3, -75, 75, true);
        }

        if (location == 2) {
            claw.servo.setPosition(0);
            chassis.DriveToPosition(0.3, 0, 75, true);
        }

        if (location == 3) {
            claw.servo.setPosition(0);
            chassis.DriveToPosition(0.3, 75, 75, true);

        }
    }

    public void initArmClaw(){
        claw.close();
        arm.swingUp();
        claw.open();

    }

    public void deliverPreLoad(boolean LR) {
        /** First swing the arm up and go to the pole. **/
        //Close claw, the arm is already up.
        claw.servo.setPosition(1);

        timeout_ms = 500;
        runtime.reset();
        while(runtime.milliseconds() < timeout_ms){

        }

        chassis.DriveToPosition(0.8, 0, 70, true);


        //Drive to the pole
        if(LR) { // True = Left
            turnRobotToAngle(312);
        }
        else{
            turnRobotToAngle(45);
        }

        chassis.DriveToPosition(0.8, 0, 10, true);

        /** Next, move the slider to the right height, swing the arm down, drop the cone, swing the arm back up, and lower the slider. **/
        //Moves the slider to the correct height
       vSlider.MoveSlider(1, 1000, 2000);


//        //Swings the arm down
         arm.swingDown();
         //Arm(false);

        // Lower the slider a little bit to catch the cone in pole.
       vSlider.MoveSlider(1, -500, 100);

        //Open and close the claw to drop the cone
        claw.open();
        timeout_ms = 1500;
        runtime.reset();
        while ((runtime.milliseconds() < timeout_ms)) {
        }
        claw.close();

        chassis.DriveToPosition(0.8,0,-5, true);

        //Raises slider a little bit to not get caught on the pole
       vSlider.MoveSlider(1, 1000, 500);
        //lower the slider
       vSlider.MoveSlider(-1, -1000, 1200);

    }

    public void ParkFromMedium(int location, boolean fromFront){
        turnRobotToAngle(175);
        if(fromFront) {
            switch(location){
                case 1:
                    chassis.DriveToPosition(0.5, -70, -5, true);
                    break;
                case 2:
                    chassis.DriveToPosition(0.5, 0, -5, true);
                    break;
                case 3:
                    chassis.DriveToPosition(0.5, 70, -5, true);
                    break;
            }
        }
        else {
            switch(location){
                case 1:
                    chassis.DriveToPosition(0.5, -80, -5, true);
                    break;
                case 2:
                    chassis.DriveToPosition(0.5, -60, -5, true);
                    break;
                case 3:
                    chassis.DriveToPosition(0.5, 50, -5, true);
                    break;
            }
        }
    }



    public void CycleCone(boolean LR){
        if(LR){
            /** First go to the stack of cones **/
            turnRobotToAngle(360);
            chassis.DriveToPosition(0.8, 0, 50, true);
            /** Now cycle the cones**/
            turnRobotToAngle(90);
            //Open the claw
            claw.open();
            //Drive forward slightly
            chassis.DriveToPosition(0.8, 0, 55, true);
            //close the claw and grab onto the cone
            claw.close();
            /** Now drive to the medium pole **/
            //Drive to the pole and face it
            chassis.DriveToPosition(0.8,0,-50, true);
            turnRobotToAngle(205);
            chassis.stopDriveMotors();
            /** Now deliver the cone **/
            //Move the slider to the right height
            vSlider.MoveSlider(1, 1000,2400);
            //Open and close claw
            claw.open();
            timeout_ms = 500;
            runtime.reset();
            while ((runtime.milliseconds() < timeout_ms)) {
            }
            claw.close();
            //lower slider
            vSlider.MoveSlider(1, -1000,1200);
        }
        else{
            /** First go to the stack of cones **/
            turnRobotToAngle(360);
            chassis.DriveToPosition(0.8, 0, 50, true);
            /** Now cycle the cones**/
            turnRobotToAngle(270);
            //Open the claw
            claw.open();
            //Drive forward slightly
            chassis.DriveToPosition(0.8, 0, 55, true);
            //close the claw and grab onto the cone
            claw.close();
            /** Now drive to the medium pole **/
            //Drive to the pole and face it
            chassis.DriveToPosition(0.8,0,-50, true);
            turnRobotToAngle(135);
            chassis.stopDriveMotors();
            /** Now deliver the cone **/
            //Move the slider to the right height
            vSlider.MoveSlider(1, 1000,2400);
            //Open and close claw
            claw.open();
            timeout_ms = 500;
            runtime.reset();
            while ((runtime.milliseconds() < timeout_ms)) {
            }
            claw.close();
            //lower slider
            vSlider.MoveSlider(1, -1000,1200);
        }
    }

}