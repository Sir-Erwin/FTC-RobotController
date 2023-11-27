package org.firstinspires.ftc.teamcode;
import static org.firstinspires.ftc.teamcode.utils.BTController.Axis.LEFT_X;

import com.arcrobotics.ftclib.command.Command;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.subsystems.*;
import org.firstinspires.ftc.teamcode.utils.BTController;


public class RobotContainer extends com.arcrobotics.ftclib.command.Robot {
    private Gamepad gamepad;
     Chassis m_chassis;
     BTController m_controller;
     gripper m_gripper;
    public RobotContainer(HardwareMap map, Telemetry telemetry){
        m_controller = new BTController(gamepad);
        m_chassis= new Chassis(map, telemetry);
        m_gripper = new gripper(map,telemetry);
    }
    //bind commands to trigger
    public void bindCommands(){
        m_controller.assignCommand(m_chassis.drive(()->gamepad.left_stick_x, ()->gamepad.left_trigger+gamepad.right_trigger, ()->gamepad.left_stick_y),
                true, LEFT_X, BTController.Axis.LEFT_Y, BTController.Axis.LEFT_TRIGGER, BTController.Axis.RIGHT_TRIGGER);
    }
    public Command AutonomousCommand(){
        return null;
    }
}
