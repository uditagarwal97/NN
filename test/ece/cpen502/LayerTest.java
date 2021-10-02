package ece.cpen502;

import org.junit.Test;

public class LayerTest {

    @Test
    public void TestLayerConstructor()
    {
        double inputArray[] = {1, 2, 3};
        Layer inpLayer = new Layer(1, Layer.Type.Input, 3, null, false, true);

        assert inpLayer.GetLayerType() == Layer.Type.Input;
        assert inpLayer.GetLayerNumber() == 1;
        assert inpLayer.GetOutputMatrixSize() == 3;
        assert inpLayer.GetNumberOfNeurons() == 3;
        assert Matrix.CompareMatrices(inpLayer.GetWeightMatrix(), new Matrix(3, 1, 1)) == true;

        // First hidden layer
        Layer hiddenLayer = new Layer(2, Layer.Type.Hidden, 5, inpLayer, false, true);
        assert Matrix.CompareMatrices(hiddenLayer.GetWeightMatrix(), new Matrix(5, 3, 1)) == true;
        assert hiddenLayer.GetOutputMatrixSize() == 5;

        // Second hidden layer
        Layer hiddenLayer2 = new Layer(3, Layer.Type.Hidden, 5, hiddenLayer, true, true);
        assert Matrix.CompareMatrices(hiddenLayer2.GetWeightMatrix(), new Matrix(5, 5, 1)) == true;
        assert hiddenLayer2.GetOutputMatrixSize() == 6;
    }

    @Test
    public void TestForwardPropogationInputLayer()
    {
        Matrix input = new Matrix(1, new double[] {1, 2});
        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, true);

        Matrix OpOfLayer1 = InputLayer.FeedPropogate(input);
        assert InputLayer.GetOutputMatrixSize() == OpOfLayer1.GetRow();
        assert OpOfLayer1.GetCol() == 1;
        assert Matrix.CompareMatrices(OpOfLayer1, new Matrix(1, new double[]{1, 1, 2}));
    }

    @Test
    public void TestForwardPropogationHiddenLayer()
    {
        Matrix input = new Matrix(1, new double[] {1, 2});
        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, true);
        Layer HiddenLayer = new Layer(2, Layer.Type.Hidden, 4, InputLayer, true, true);

        Matrix OpOfLayer1 = InputLayer.FeedPropogate(input); // Output should be {1, 1, 2}

        Matrix OpOfLayer2 = HiddenLayer.FeedPropogate(OpOfLayer1); // Output should be {1, 4, 4, 4, 4}
        assert HiddenLayer.GetOutputMatrixSize() == 5;
        assert OpOfLayer2.GetCol() == 1;
        assert OpOfLayer2.GetRow() == 5;
        assert Matrix.CompareMatrices(OpOfLayer2, new Matrix(1, new double[] {1, 4, 4, 4, 4})) == true;
    }

    @Test
    public void TestForwardPropogationOutputLayer()
    {
        Matrix input = new Matrix(1, new double[] {1, 2});
        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, true);
        Layer HiddenLayer = new Layer(2, Layer.Type.Hidden, 4, InputLayer, true, true);
        Layer OutputLayer = new Layer(3, Layer.Type.Output, 1, HiddenLayer, false, true);

        Matrix OpOfLayer1 = InputLayer.FeedPropogate(input); // Output should be {1, 1, 2}

        Matrix OpOfLayer2 = HiddenLayer.FeedPropogate(OpOfLayer1); // Output should be {1, 4, 4, 4, 4}

        Matrix OpOfLayer3 = OutputLayer.FeedPropogate(OpOfLayer2); // Output should be 17
        assert OutputLayer.GetOutputMatrixSize() == 1;
        assert OpOfLayer3.GetCol() == 1;
        assert OpOfLayer3.GetRow() == 1;
        assert Matrix.CompareMatrices(OpOfLayer3, new Matrix(1, new double[] {17})) == true;
    }

    /* Expected output:
    Layer weights of layer 3 is:
            1.0 1.0 1.0 1.0 1.0
    Error signal of layer 3 is:
            -3808.0
            ---
    Layer weights of layer 2 is:
            1.0 1.0 1.0
            1.0 1.0 1.0
            1.0 1.0 1.0
            1.0 1.0 1.0
    Error signal of layer 2 is:
            -0.0
            45696.0
            45696.0
            45696.0
            45696.0
    */
    @Test
    public void TestErrorSignalGeneration()
    {
        Matrix input = new Matrix(1, new double[] {1, 2});
        Matrix output = new Matrix(1, new double[] {3});

        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, true);
        Layer HiddenLayer = new Layer(2, Layer.Type.Hidden, 4, InputLayer, true, true);
        Layer OutputLayer = new Layer(3, Layer.Type.Output, 1, HiddenLayer, false, true);

        Matrix OpOfLayer1 = InputLayer.FeedPropogate(input); // Output should be {1, 1, 2}

        Matrix OpOfLayer2 = HiddenLayer.FeedPropogate(OpOfLayer1); // Output should be {1, 4, 4, 4, 4}

        Matrix OpOfLayer3 = OutputLayer.FeedPropogate(OpOfLayer2); // Output should be 17

        OutputLayer.BackPropogation(output, null);
        assert Matrix.CompareMatrices(OutputLayer.GetWeightMatrix(), new Matrix(1, 5, 1)) == true;
        assert Matrix.CompareMatrices(OutputLayer.ErrorSignal, new Matrix(1, new double[] {-3808.0})) == true;

        System.out.println("---");

        HiddenLayer.BackPropogation(null, OutputLayer);
        assert Matrix.CompareMatrices(HiddenLayer.GetWeightMatrix(), new Matrix(4, 3, 1)) == true;
        assert Matrix.CompareMatrices(HiddenLayer.ErrorSignal, new Matrix(1, new double[] {-0.0, 45696.0, 45696.0, 45696.0, 45696.0})) == true;
    }

    @Test
    public void TestSigmoidActivationFunction()
    {
        Matrix input = new Matrix(1, new double[] {1, 2});
        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, true);

        Matrix OpOfLayer1 = InputLayer.FeedPropogate(input); // Output should be {1, 1, 2}
        assert InputLayer.GetSigmoid(0) == (double)0.5;
    }
}
