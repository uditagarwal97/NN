package ece.cpen502.NN;

import ece.cpen502.NN.CommonInterface;

public interface NeuralNetworkInterface extends CommonInterface {

    final double bias = 1.0;

    public double sigmoid (double x);
    public double customSigmoid (double x);
    public void initializeWeights();
    public void zeroWeights();
}
