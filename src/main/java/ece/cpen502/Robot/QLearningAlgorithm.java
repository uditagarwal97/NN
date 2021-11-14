package ece.cpen502.Robot;

public class QLearningAlgorithm {

    private double Epsilon = 0.9;
    private double DiscountFactor = 0.1; // TODO: Check this?
    private double LearningRate = 0.2;

    private RobotStates previousState = new RobotStates();
    private RobotActions previousAction = new RobotActions();


    void Qlearn(LookUpTable table, RobotStates state, RobotActions action, double reward)
    {

        double oldQVal = table.GetValue(previousState, previousAction);
        double learnedValue = reward + DiscountFactor * table.GetMaxValueOfState(state);
        double newQVal = (1 - LearningRate) * oldQVal + LearningRate * learnedValue;

        table.SetValue(state, action, newQVal);

        //update state and action
        previousAction = action;
        previousState = state;
    }

    void ChangeToExplorationStrategy()
    {
        Epsilon = 0.1;
    }

    RobotActions SelectActionGreedy(LookUpTable table, RobotStates state)
    {
        RobotActions action;
        double rand = Math.random();

        // Pick a random action
        if (Epsilon < rand) {
            action = RobotActions.GetRandomRobotActions();
        }
        else {
            action = table.GetBestAction(state);
        }

        return action;
    }

    /*
    RobotActions selectActionSoftMax(LookUpTable table, RobotStates state)
    {
        int action = 0;
        double Qsum = 0;
        double[] Qprob = new double[table.numActions];

        for (int i = 0; i < table.numActions; i ++){
            Qprob[i] = Math.exp(table.getValue(state, i) / tau);
            Qsum += Qprob[i];
        }
        if (Qsum != 0){
            for (int i = 0; i < table.numActions; i ++) {
                Qprob[i] /= Qsum;
            }

        }else{
            action = table.getBestAction(state);
            return action;
        }

        //Look into this
        double cumulativeProb = 0.0;
        double randomNum = Math.random();
        while (randomNum > cumulativeProb && action < table.numActions)
        {
            cumulativeProb += Qprob[action];
            action ++;
        }
        return action - 1;
    }
    */
}