package ece.cpen502;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {

    // Fixed
    static boolean IsBinary = false;
    static double LearningRate = 0.2;
    static double MomentumTerm = 0.0;
    static double Bias = 1;
    static double ErrorThreshold = 0.05;
    static int MAX_EPOCH = 20000;
    static double[] RNGBounds = new double[]{-0.5, 0.5}; //Random number between -0.5 to 0.5

    //DataSet
    double[][] DatasetInput = new double[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
    double[][] DatasetOutput = new double[][] {{0}, {1}, {1}, {0}};
    static int CurrentDatasetRow = 0;

    //Layers, weight, and error matrices.
    int[] LayerSizes = new int[] {3, 5, 1};
    int MaxSizeOfLayers = 5;
    double TotalErrorAfterFF  = 0.0;

    double[][] LayerOutputMatrix = new double[LayerSizes.length][MaxSizeOfLayers];;
    double[] OutputLayerError = new double[LayerSizes[LayerSizes.length - 1]];
    double[][][] WeightMatrix = new double[LayerSizes.length][LayerSizes.length][MaxSizeOfLayers];
    double[][][] DeltaWeightMatrix = new double[LayerSizes.length][LayerSizes.length][MaxSizeOfLayers];
    double[][] DeltaLayerOutputs = new double[LayerSizes.length][MaxSizeOfLayers];

    enum DebugOptions{
        NO_DEBUG,
        FULL_DEBUG,
        CORE_DEBUG
    };

    // Debug code
    static DebugOptions IsDebug = DebugOptions.NO_DEBUG;
    static long RNGSeed = 0;
    static long CurrentRNGSeed = 0;

    private void InitializeWeightMatrices()
    {
        // Initialize weight matrix by random  umber between -0.5 to 0.5.
        Random RNG = new Random();
        CurrentRNGSeed = RNG.nextLong();

        if (RNGSeed == 0)
        {
            RNG.setSeed(CurrentRNGSeed);
        }
        else
        {
            RNG.setSeed(RNGSeed);
        }

        for (int i = 0; i < LayerSizes.length; i++)
        {
            for (int j = 0; j < LayerSizes.length; j++)
            {
                for (int k = 0; k < MaxSizeOfLayers; k++)
                {
                    WeightMatrix[i][j][k] = RNG.nextDouble() - 0.5;
                }
            }
        }

        if (IsDebug == DebugOptions.FULL_DEBUG)
        {
            System.out.println("Weight matrix after initialization is:");
            System.out.println(WeightMatrix.toString());
        }
    }

    // To reset DeltaWeight, DeltaLayerOutput, LayerOutput matrices
    private void ResetTemporaryMatrices()
    {
        for (int i = 0; i < LayerSizes.length; i++)
        {
            for (int j = 0; j < LayerSizes.length; j++)
            {
                for (int k = 0; k < MaxSizeOfLayers; k++)
                {
                    DeltaWeightMatrix[i][j][k] = 0.0;
                }
            }
        }

        for (int i = 0; i < LayerSizes.length; i++)
        {
            for (int j = 0; j < MaxSizeOfLayers; j++)
            {
                    DeltaLayerOutputs[i][j] = 0.0;

            }
        }
    }
}
