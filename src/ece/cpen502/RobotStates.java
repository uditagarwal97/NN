package ece.cpen502;

public class RobotStates {

    // X and Y coordinates of our robot
    int x1 = 0, y1 = 0;

    // X and Y coordinates of our adversary
    int x2 = 0, y2 = 0;

    // To determine whether we have sufficient firepower or not.
    Boolean IsFirePowerLeft = Boolean.TRUE;

    void SetState(double _x1, double _y1, double _x2, double _y2, double FirePower)
    {
        x1 = QuantizeCoordinate(_x1);
        x2 = QuantizeCoordinate(_x2);
        y1 = QuantizeCoordinate(_y1);
        y2 = QuantizeCoordinate(_y2);

        IsFirePowerLeft = FirePower > 1.0;
    }

    int QuantizeCoordinate(double val)
    {
        return (int) (val / 80);
    }

    int IsFirePowerGreaterThanZero()
    {
        int retval = IsFirePowerLeft ? 1 : 0;
        return retval;
    }

    void UpdateMyState(double _x1, double _y1)
    {
        x1 = QuantizeCoordinate(_x1);
        y1 = QuantizeCoordinate(_y1);
    }
}
