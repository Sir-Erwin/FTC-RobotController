package org.firstinspires.ftc.teamcode.opmodes.createmechanism;

import com.arcrobotics.ftclib.command.ConditionalCommand;
import com.arcrobotics.ftclib.command.PerpetualCommand;
import com.arcrobotics.ftclib.command.button.Button;
import com.arcrobotics.ftclib.command.button.GamepadButton;
import com.arcrobotics.ftclib.command.button.Trigger;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.commands.arm.NudgeArm;
import org.firstinspires.ftc.teamcode.commands.arm.NudgeArmWithStick;
import org.firstinspires.ftc.teamcode.commands.arm.NudgeArmWithSupplier;
import org.firstinspires.ftc.teamcode.commands.arm.ResetArmCount;
import org.firstinspires.ftc.teamcode.commands.arm.SetArmLevel;
import org.firstinspires.ftc.teamcode.opmodes.triggers.CreateMagneticLimitSwitchTrigger;
import org.firstinspires.ftc.teamcode.subsystems.magnetic.limitswitch.MagneticLimitSwitchSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.arm.ArmSubsystem;

import java.util.HashMap;
import java.util.Map;

public class CreateArm {

    private ArmSubsystem arm;
    private MagneticLimitSwitchSubsystem magneticLimitSwitch;
    private final HardwareMap hwMap;
    private final String deviceName;
    private final Telemetry telemetry;
    private GamepadEx op;
    private Trigger mlsTrigger;

    private Map<Integer, Integer> armLevels = new HashMap<>();

    private SetArmLevel moveToLevel0;
    private SetArmLevel moveToLevel1;
    private SetArmLevel moveToLevel2;
    private SetArmLevel moveToLevel3;

    private ResetArmCount resetArmCount;


    private static final int NUDGE = 5;

    public CreateArm(final HardwareMap hwMap, final String deviceName, final GamepadEx op, Telemetry telemetry){
        this.deviceName = deviceName;
        this.hwMap = hwMap;
        this.op = op;
        this.telemetry = telemetry;

    }

    public CreateArm(final HardwareMap hwMap, final String deviceName, final GamepadEx op, Telemetry telemetry, boolean autoCreate){
        this.deviceName = deviceName;
        this.hwMap = hwMap;
        this.op = op;
        this.telemetry = telemetry;

        if (autoCreate) create();

    }

    public CreateArm(final HardwareMap hwMap, final String deviceName, Telemetry telemetry){
        this.deviceName = deviceName;
        this.hwMap = hwMap;

        this.telemetry = telemetry;



    }

    public void create(){


        armLevels.put(0,0);
        armLevels.put(1,250);
        armLevels.put(2,600);
        armLevels.put(3,900);

        CreateMagneticLimitSwitch createMagneticLimitSwitch = new CreateMagneticLimitSwitch(hwMap, "limitSwitch", telemetry,true);
        magneticLimitSwitch = createMagneticLimitSwitch.getMagneticLimitSwitchTrigger();

        arm = new ArmSubsystem(hwMap,deviceName, magneticLimitSwitch, DcMotorEx.RunMode.STOP_AND_RESET_ENCODER, (HashMap) armLevels, telemetry);


        arm.setArmTargetPosition(arm.getLevel(0));
        arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        arm.setDirection(DcMotorEx.Direction.REVERSE);


        NudgeArmWithStick nudgeArmUp = new NudgeArmWithStick(arm,NUDGE,telemetry);
        NudgeArmWithStick nudgeArmDown = new NudgeArmWithStick(arm, -NUDGE, telemetry);

        moveToLevel0 = createSetArmLevel(0);
        moveToLevel1 = createSetArmLevel(1);
        moveToLevel2 = createSetArmLevel(2);
        moveToLevel3 = createSetArmLevel(3);

        Trigger armNudgerUpTrigger = new Trigger(() -> op.getRightY() <= -0.5);
        Trigger armNudgerDownTrigger = new Trigger(() -> op.getRightY() >= 0.5);

        armNudgerUpTrigger.whileActiveContinuous(nudgeArmUp);
        armNudgerDownTrigger.whileActiveContinuous(nudgeArmDown);


        //A Level 0
        Button armLevel0 = new GamepadButton(op, GamepadKeys.Button.A);
        //X Level 1
        Button armLevel1 = new GamepadButton(op, GamepadKeys.Button.X);
        //Y Level 2
        Button armLevel2 = new GamepadButton(op, GamepadKeys.Button.Y);
        //B Level 3
        Button armLevel3 = new GamepadButton(op, GamepadKeys.Button.B);

        armLevel0.whenPressed(moveToLevel0);
        armLevel1.whenPressed(moveToLevel1);
        armLevel2.whenPressed(moveToLevel2);
        armLevel3.whenPressed(moveToLevel3);

        resetArmCount = createResetArmCount();
        arm.setDefaultCommand(new PerpetualCommand(resetArmCount));


    }

    public void createAuto(){


        armLevels.put(0,0);
        armLevels.put(1,250);
        armLevels.put(2,600);
        armLevels.put(3,900);

        CreateMagneticLimitSwitch createMagneticLimitSwitch = new CreateMagneticLimitSwitch(hwMap, "limitSwitch", telemetry,true);
        magneticLimitSwitch = createMagneticLimitSwitch.getMagneticLimitSwitchTrigger();

        arm = new ArmSubsystem(hwMap,deviceName, magneticLimitSwitch, DcMotorEx.RunMode.STOP_AND_RESET_ENCODER, (HashMap) armLevels, telemetry);


        arm.setArmTargetPosition(arm.getLevel(0));
        arm.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
        arm.setDirection(DcMotorEx.Direction.REVERSE);


        moveToLevel0 = createSetArmLevel(0);
        moveToLevel1 = createSetArmLevel(1);
        moveToLevel2 = createSetArmLevel(2);
        moveToLevel3 = createSetArmLevel(3);


        resetArmCount = createResetArmCount();
        arm.setDefaultCommand(new PerpetualCommand(resetArmCount));


    }

    private SetArmLevel createSetArmLevel(int levelIndicator){
        return new SetArmLevel(arm,levelIndicator, telemetry);
    }

    private ResetArmCount createResetArmCount(){
        return new ResetArmCount(arm,telemetry);
    }

    public ArmSubsystem getArm(){
        return arm;
    }
}
