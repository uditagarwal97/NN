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
        assert Matrix.CompareMatrices(inpLayer.GetWeightMatrix(), new Matrix(3, 1, 1));

        // First hidden layer
        Layer hiddenLayer = new Layer(2, Layer.Type.Hidden, 5, inpLayer, false, true);
        assert Matrix.CompareMatrices(hiddenLayer.GetWeightMatrix(), new Matrix(5, 3, 1));
        assert hiddenLayer.GetOutputMatrixSize() == 5;

        // Second hidden layer
        Layer hiddenLayer2 = new Layer(3, Layer.Type.Hidden, 5, hiddenLayer, true, true);
        assert Matrix.CompareMatrices(hiddenLayer2.GetWeightMatrix(), new Matrix(5, 5, 1));
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
        assert Matrix.CompareMatrices(OpOfLayer2, new Matrix(1, new double[]{1, 4, 4, 4, 4}));
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
        assert Matrix.CompareMatrices(OpOfLayer3, new Matrix(1, new double[]{17}));
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
        assert Matrix.CompareMatrices(OutputLayer.GetWeightMatrix(), new Matrix(1, 5, 1));
        assert Matrix.CompareMatrices(OutputLayer.ErrorSignal, new Matrix(1, new double[]{-3808.0}));

        System.out.println("---");

        HiddenLayer.BackPropogation(null, OutputLayer);
        assert Matrix.CompareMatrices(HiddenLayer.GetWeightMatrix(), new Matrix(4, 3, 1));
        assert Matrix.CompareMatrices(HiddenLayer.ErrorSignal, new Matrix(1, new double[]{-0.0, 45696.0, 45696.0, 45696.0, 45696.0}));
    }

    @Test
    public void TestUpdateWeights()
    {
        Matrix input = new Matrix(1, new double[] {1, 0});
        Matrix output = new Matrix(1, new double[] {1});

        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, false);
        Layer HiddenLayer = new Layer(2, Layer.Type.Hidden, 4, InputLayer, true, false);
        Layer OutputLayer = new Layer(3, Layer.Type.Output, 1, HiddenLayer, false, false);

        for (int k =0; k < 5000; k++) {
            Matrix OpOfLayer1 = InputLayer.FeedPropogate(input); // Output should be {1, 1, 2}

            Matrix OpOfLayer2 = HiddenLayer.FeedPropogate(OpOfLayer1); // Output should be {1, 4, 4, 4, 4}

            Matrix OpOfLayer3 = OutputLayer.FeedPropogate(OpOfLayer2); // Output should be 17

            System.out.println(OpOfLayer3.data[0][0]);

            if (Math.abs(OpOfLayer3.data[0][0] - output.data[0][0]) < 0.05)
            {
                System.out.println("Current iteration is: " + k);
                break;
            }

            OutputLayer.BackPropogation(output, null);
            HiddenLayer.BackPropogation(null, OutputLayer);

            HiddenLayer.UpdateWeights(InputLayer);
            OutputLayer.UpdateWeights(HiddenLayer);
        }
    }

    @Test
    public void TestCompleteNN()
    {
        Matrix input = new Matrix(2, new double[] {0, 0, 0, 1, 1, 1, 1, 0});
        Matrix output = new Matrix(1, new double[] {0, 1, 0, 1});

        assert input.GetRow() == output.GetRow();

        Layer InputLayer = new Layer(1, Layer.Type.Input, 2, null, true, false);
        Layer HiddenLayer = new Layer(2, Layer.Type.Hidden, 4, InputLayer, true, false);
        Layer OutputLayer = new Layer(3, Layer.Type.Output, 1, HiddenLayer, false, false);

        for (int k =0; k < 50000; k++) {
            for (int j = 0; j < input.GetRow(); j++) {

                Matrix currentInput = input.GetCurrentRow(j, true);
                Matrix currentOutput = output.GetCurrentRow(j);

                Matrix OpOfLayer1 = InputLayer.FeedPropogate(currentInput); // Output should be {1, 1, 2}

                Matrix OpOfLayer2 = HiddenLayer.FeedPropogate(OpOfLayer1); // Output should be {1, 4, 4, 4, 4}

                Matrix OpOfLayer3 = OutputLayer.FeedPropogate(OpOfLayer2); // Output should be 17

                System.out.print("Input is: ");
                input.GetCurrentRow(j).PrintMatrix();
                System.out.println(OpOfLayer3.data[0][0]);

                if (Math.abs(OpOfLayer3.data[0][0] - output.data[0][0]) < 0.05) {
                    System.out.println("Current iteration is: " + k);
                    break;
                }

                OutputLayer.BackPropogation(currentOutput, null);
                HiddenLayer.BackPropogation(null, OutputLayer);

                HiddenLayer.UpdateWeights(InputLayer);
                OutputLayer.UpdateWeights(HiddenLayer);
            }
        }

        /*
        System.out.println("Weights after updating");
        HiddenLayer.PrintLayerWeights();
        OutputLayer.PrintLayerWeights();

        System.out.println("Error matrix is");
        HiddenLayer.PrintErrorSignal();
        OutputLayer.PrintErrorSignal();
         */
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
