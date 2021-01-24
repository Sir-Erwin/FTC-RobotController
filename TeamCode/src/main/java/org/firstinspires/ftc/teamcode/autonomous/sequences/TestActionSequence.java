package org.firstinspires.ftc.teamcode.autonomous.sequences;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.action.DetectRingsAction;
import org.firstinspires.ftc.teamcode.action.ExecuteSequenceAction;
import org.firstinspires.ftc.teamcode.action.IfActionResult;
import org.firstinspires.ftc.teamcode.action.MoveAndOrientAction;
import org.firstinspires.ftc.teamcode.action.WaitForeverAction;
import org.firstinspires.ftc.teamcode.playmaker.ActionSequence;

public class TestActionSequence extends ActionSequence {

    DetectRingsAction detectRingsAction = new DetectRingsAction(2000);

    public TestActionSequence() {
        //addAction(new MoveAndOrientAction(DistanceUnit.INCH, 0, -40, 90, 0.75f));
        addAction(new ExecuteSequenceAction(new ReleaseWobbleGoalSequence()));
//        addAction(new MoveAndOrientAction(DistanceUnit.INCH, 0, -36, 80, 0.75f));
//        addAction(new ExecuteSequenceAction(new ShootActionSequence(3)));
//        addAction(new WaitForeverAction());
    }
}