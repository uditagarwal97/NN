package ece.cpen502;

public class Layer {

    public enum Type{
        Input,
        Hidden,
        Output
    };

    public enum ActivationFunctionType{
        Sigmoid,
        Relu,
        TanH
    };

    // That's the default settings.
    ActivationFunctionType ACTFun = ActivationFunctionType.Sigmoid;
    private double SigmoidUpperBound = 1;
    private double SigmoidLowerBound = 0;

    private Type LayerType;
    private int LayerNumber;
    private int NumberOfNeurons;
    private int HasBias;
    private boolean isTest;

    // The size of this matrix should be equal to No. of Neurons in this layer X No. of neurons in the previous layer.
    private Matrix Weights;
    // Matrix to temporarily store the output of a layer.
    private Matrix Output;
    // Size of this matrix is same as that of the Weight matrix.
    public Matrix OutputDerivative;
    // Error signal
    public Matrix ErrorSignal;

    public Layer(int layerNo, Type layerType, int numberOfNeurons, Layer previousLayer, boolean hasBias, boolean isTest)
    {
        this.LayerNumber = layerNo;
        this.LayerType = layerType;
        this.HasBias = (hasBias) ? 1 : 0;
        this.isTest = isTest;
        this.Output = null;
        this.OutputDerivative = null;
        this.NumberOfNeurons = numberOfNeurons;
        this.ErrorSignal = null;

        // If it's an input layer, just have a matrix of [No. of inputs, 1], initialized by one.
        if (this.LayerType == Type.Input)
        {
            assert previousLayer == null;
            this.Weights = new Matrix(this.NumberOfNeurons, 1, 1);
        }
        else
        {
            assert previousLayer != null;

            // During test mode, do not use random weights so that test cases are deterministic.
            if (isTest)
            {
                // Initialize weights by 1 during test mode.
                this.Weights = new Matrix(this.NumberOfNeurons, previousLayer.GetOutputMatrixSize(), 1);
            }
            else
            {
                // Initialize the weights by random value: [-0.5, 0.5]
                this.Weights = new Matrix(this.NumberOfNeurons, previousLayer.GetOutputMatrixSize());
                this.Weights.InitRand();
            }
        }
    }

    /**
     * This function calculates the output of the current layer.
     * @param outputOfPreviousLayer: Give the output of the previous layer as input to this layer.
     * @return It will return and store the output of the current layer.
     */
    public Matrix FeedPropogate(Matrix outputOfPreviousLayer)
    {
        assert outputOfPreviousLayer != null;

        // TODO: This code can be simplified.
        if (this.LayerType == Type.Input)
        {
            assert outputOfPreviousLayer.GetRow() == this.NumberOfNeurons;

            Matrix out = new Matrix(this.NumberOfNeurons + this.HasBias, 1, 0);

            for (int i = 0; i < this.NumberOfNeurons; i++)
            {
                out.data[i + this.HasBias][0] = this.Weights.data[i][0] * outputOfPreviousLayer.data[i][0];
            }
            // First output is always 1 (bias)
            if (this.HasBias == 1)
            {
                out.data[0][0] = 1;
            }

            this.Output = out;
            return out;
        }
        else
        {
            assert this.Weights.GetCol() == outputOfPreviousLayer.GetRow();

            Matrix out = new Matrix(this.NumberOfNeurons + this.HasBias, 1, 0);

            for (int i = 0; i < this.NumberOfNeurons; i++)
            {
                for (int j = 0; j < outputOfPreviousLayer.GetRow(); j++)
                {
                    out.data[i + this.HasBias][0] = out.data[i + this.HasBias][0] + (this.Weights.data[i][j] * outputOfPreviousLayer.data[j][0]);
                }

                // Add Activation function only during production so it is otherwise easier to test the code.
                if (!this.isTest)
                {
                    out.data[i + this.HasBias][0] = this.ComputeActivationFunction(out.data[i + this.HasBias][0]);
                }
            }

            // First output is always 1 (bias)
            if (this.HasBias == 1)
            {
                out.data[0][0] = 1;
            }

            // Calculate output derivative matrix for Hidden and output layers
            this.OutputDerivative = new Matrix(this.NumberOfNeurons + this.HasBias, 1, 0);
            for (int i = 0; i < this.NumberOfNeurons; i++)
            {
                this.OutputDerivative.data[i + this.HasBias][0] = out.data[i + this.HasBias][0] * ((double)1
                        - out.data[i + this.HasBias][0]);
            }

            this.Output = out;
            return out;
        }
    }

    /**
     * This function calculates the error for the current layer.
     * @param target Either give target of the NN as parameter (for output layer) or give previous layer as input (for hidden layers).
     */
    public void BackPropogation(Matrix target, Layer nextLayer)
    {
        // These checks ensure that feedforward propogation step is complete.
        assert this.Output != null;
        assert this.OutputDerivative != null;

        // assert this.LayerType != Type.Input : "Don't call backpropogation function on input layer";

        if (this.LayerType == Type.Output)
        {
            assert target.GetCol() == 1;
            assert target.GetRow() == this.NumberOfNeurons + this.HasBias;

            this.ErrorSignal = new Matrix(this.NumberOfNeurons + this.HasBias, 1, 0);
            for (int i = 0; i < this.NumberOfNeurons; i++)
            {
                this.ErrorSignal.data[i + this.HasBias][0] = (this.Output.data[i + this.HasBias][0] - target.data[i][0])
                        * this.OutputDerivative.data[i + this.HasBias][0];
            }
        }
        else
        {
            assert nextLayer != null : "Give the n+1 layer as input";

            this.ErrorSignal = new Matrix(this.NumberOfNeurons + this.HasBias, 1, 0);
            for (int i = 0; i < this.NumberOfNeurons + this.HasBias; i++)
            {
                double sum = 0;
                for (int j = 0; j < nextLayer.NumberOfNeurons; j++)
                {
                    sum += nextLayer.Weights.data[j][i] * nextLayer.ErrorSignal.data[j][0];
                }
                this.ErrorSignal.data[i][0] = sum * this.OutputDerivative.data[i][0];
            }
        }
    }

    // TODO: Complete this.
    public void UpdateWeights()
    {
        
    }

    public double ComputeActivationFunction(double x)
    {
        if (this.ACTFun == ActivationFunctionType.Sigmoid)
        {
            return GetSigmoid(x);
        }
        else
        {
            assert false : "Implement other activation functions";
            return 0;
        }
    }

    public double GetSigmoid(double x)
    {
        // TODO: Customize the Sigmoid function.
        double ans = 0.0;
        ans = 1 / (1 + Math.exp(-x));
        return ans;
    }

    public int GetNumberOfNeurons() { return this.NumberOfNeurons; }
    public Type GetLayerType() { return this.LayerType; }
    public int GetLayerNumber() { return this.LayerNumber; }
    public int GetOutputMatrixSize() { return (this.HasBias + this.NumberOfNeurons); }
    public Matrix GetWeightMatrix() { return this.Weights; }

    public void PrintErrorSignal()
    {
        System.out.println("Error signal of layer " + this.LayerNumber + " is:");
        if (this.ErrorSignal == null || this.ErrorSignal.data.length == 0)
            return;

        this.ErrorSignal.PrintMatrix();
    }

    public void PrintLayerWeights()
    {
        System.out.println("Layer weights of layer " + this.LayerNumber + " is:");
        if (this.ErrorSignal == null || this.ErrorSignal.data.length == 0)
            return;

        this.Weights.PrintMatrix();
    }
}
