package ece.cpen502.Robot;

import java.util.Random;

// This class represents the set of actions our robot can make.
public class RobotActions {

    enum ActionTypes {
        Forward,
        Backward,
        LeftForward,
        RightForward,
        LeftBackward,
        RightBackward
    };

    enum QuantizedDistance {
        Near,
        Far
    };

    QuantizedDistance QDistance = QuantizedDistance.Near;
    int Distance = 500;
    ActionTypes ActionType = ActionTypes.Forward;
    Boolean FireDecision = Boolean.TRUE;
    Random rand = new Random();

    void SelectRandomAction()
    {
        int randInt = Math.abs(rand.nextInt(6) % 6);

        assert randInt < 6 && randInt > -1;
        ActionType = ActionTypes.values()[randInt];
    }

    void SelectRandomDistance()
    {
        int randInt = Math.abs(rand.nextInt(2) % 2);
        QDistance = QuantizedDistance.values()[randInt];

        switch (QDistance){
            case Near:
                Distance = 80;
                break;
            case Far:
                Distance = 160;
                break;
        }
    }

    void SelectRandomFireDecision()
    {
        FireDecision = rand.nextBoolean();
    }

    static RobotActions GetRandomRobotActions()
    {
        RobotActions retval = new RobotActions();
        retval.SelectRandomFireDecision();
        retval.SelectRandomAction();
        retval.SelectRandomDistance();

        return retval;
    }

    int GetActionAsInteger()
    {
        return ActionType.ordinal();
    }

    int GetDistanceAsInteger()
    {
        return QDistance.ordinal();
    }

    int GetFireDecisionAsInteger()
    {
        return FireDecision ? 1 : 0;
    }

    public void SetActions(int actionType, int distance, int fireDecision){

        ActionType = ActionTypes.values()[actionType];
        QDistance = QuantizedDistance.values()[distance];

        switch (QDistance){
            case Near:
                Distance = 80;
                break;
            case Far:
                Distance = 160;
                break;
        }

        FireDecision = (fireDecision == 1);
    }
}
