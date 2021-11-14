package ece.cpen502.Robot;

// Lookup table class
public class LookUpTable {

    private final int NumberOfDirections = 6;
    private final int BattlefieldSizeQuantizer = 5;
    private final int NumberOfFirePowerValues = 2;
    private final int PossibleDistanceValues = 2;
    private final int PossibleFireDecision = 2;

    public int StateSpaceSize = NumberOfDirections * NumberOfFirePowerValues * BattlefieldSizeQuantizer *
            BattlefieldSizeQuantizer * BattlefieldSizeQuantizer * BattlefieldSizeQuantizer * PossibleDistanceValues *
            PossibleFireDecision;

    // Look-up table
    public double[][][][][][][][] table = new double[BattlefieldSizeQuantizer][BattlefieldSizeQuantizer][BattlefieldSizeQuantizer][BattlefieldSizeQuantizer]
                                                    [NumberOfFirePowerValues][NumberOfDirections][PossibleDistanceValues][PossibleFireDecision];

    // Initialize the LUT with 0.0
    public void InitLUT()
    {
        for (int i = 0; i < BattlefieldSizeQuantizer; i++){
            for (int j = 0; j < BattlefieldSizeQuantizer; j++){
                for (int k = 0; k < BattlefieldSizeQuantizer; k++){
                    for (int l = 0; l < BattlefieldSizeQuantizer; l++){
                        for (int a = 0; a < NumberOfFirePowerValues; a++){
                            for (int b = 0; b < NumberOfDirections; b++){
                                for (int c = 0; c < PossibleDistanceValues; c++){
                                    for (int d = 0; d < PossibleFireDecision; d++){
                                        table[i][j][k][l][a][b][c][d] = 0.0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public LookUpTable()
    {
        InitLUT();
    }

    public void SetValue(RobotStates state, RobotActions action, double QValue){

        table[state.x1][state.y1][state.x2][state.y2][state.IsFirePowerGreaterThanZero()]
                [action.GetActionAsInteger()][action.GetDistanceAsInteger()][action.GetFireDecisionAsInteger()] = QValue;
    }

    public double GetValue(RobotStates state, RobotActions action){
        return table[state.x1][state.y1][state.x2][state.y2][state.IsFirePowerGreaterThanZero()]
                [action.GetActionAsInteger()][action.GetDistanceAsInteger()][action.GetFireDecisionAsInteger()];
    }

    // Get max QValue of a state, after considering all possible actions
    public double GetMaxValueOfState(RobotStates state)
    {
        double retval = Double.NEGATIVE_INFINITY;

        for (int b = 0; b < NumberOfDirections; b++){
            for (int c = 0; c < PossibleDistanceValues; c++){
                for (int d = 0; d < PossibleFireDecision; d++){

                    double currValue = table[state.x1][state.y1][state.x2][state.y2][state.IsFirePowerGreaterThanZero()][b][c][d];

                    if (currValue > retval)
                        retval = currValue;
                }
            }
        }

        return retval;
    }

    // Get best set of actions for a given state.
    public RobotActions GetBestAction(RobotStates state)
    {
        double temp = Double.NEGATIVE_INFINITY;
        RobotActions retval = new RobotActions();

        for (int b = 0; b < NumberOfDirections; b++){
            for (int c = 0; c < PossibleDistanceValues; c++){
                for (int d = 0; d < PossibleFireDecision; d++){

                    double currValue = table[state.x1][state.y1][state.x2][state.y2][state.IsFirePowerGreaterThanZero()][b][c][d];

                    if (currValue > temp)
                    {
                        temp = currValue;
                        retval.SetActions(b, c, d);
                    }
                }
            }
        }

        return retval;
    }
}
