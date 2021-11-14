package ece.cpen502.NN;

import java.io.File;
import java.io.IOException;

public interface CommonInterface {

    // Get output for the NN.
    public double outputFor (double[] x);

    // Function to train the NN.
    public double train (double[] x, double argVal);

    // Functions to save and load weights.
    public void save (File argFile);
    public void load (String argFile) throws IOException;
}
