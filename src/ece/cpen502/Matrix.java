package ece.cpen502;

import java.util.Random;
import java.util.Scanner;

/*
 * A library for handling matrix operations. It's required for developing NN.
 * @ Author: Udit Kumar Agarwal
 * @ Email: uditagarwal1997@gmail.com
 */
public class Matrix {

    // Number of rows in the matrix.
    private int NumRows;
    // Number of coloumns of the matrix.
    private int NumCols;
    // Data stored within the matrix.
    public double data[][];

    public Matrix (int Rows, int Coloumns){
        this.NumCols = Coloumns;
        this.NumRows = Rows;
        this.data = new double[Rows][Coloumns];

        this.InitZero();
    }

    public Matrix (int Rows, int Coloumns, int init){
        this.NumCols = Coloumns;
        this.NumRows = Rows;
        this.data = new double[Rows][Coloumns];

        for (int i = 0; i < this.NumRows; i++){
            for (int j = 0; j < this.NumCols; j++){
                this.data[i][j] = init;
            }
        }
    }

    public Matrix (int NumCols, double data[])
    {
        assert data.length % NumCols == 0;
        this.NumCols = NumCols;
        this.NumRows = (int)(data.length / this.NumCols);

        this.data = new double[this.NumRows][this.NumCols];

        for (int i = 0; i < this.NumRows; i++){
            for (int j = 0; j < this.NumCols; j++){
                this.data[i][j] = data[i+j];
            }
        }
    }

    // Initialize the matrix by zeros.
    public void InitZero()
    {
        for (int i = 0; i < this.NumRows; i++){
            for (int j = 0; j < this.NumCols; j++){
                this.data[i][j] = 0;
            }
        }
    }

    // Initialize the matrix by random number.
    public void InitRand()
    {
        Random rand = new Random();
        for (int i = 0; i < this.NumRows; i++){
            for (int j = 0; j < this.NumCols; j++){
                // Initialize the matrix by values: [-0.5, +0.5]
                this.data[i][j] = rand.nextDouble() - (double)0.5;
            }
        }
    }

    // Return the dimensions of this matrix.
    public int GetRow() { return this.NumRows; }
    public int GetCol() { return this.NumCols; }

    // Add two matrices.
    public Matrix AddMatrix(Matrix b)
    {
        assert this.NumRows == b.GetRow();
        assert this.NumCols == b.GetCol();

        Matrix out = new Matrix(this.NumRows, this.NumCols);

        for (int i = 0; i < this.NumRows; i++){
            for (int j = 0; j < this.NumCols; j++){
                out.data[i][j] = b.data[i][j] + this.data[i][j];
            }
        }

        return out;
    }

    // Calculate dot product of two matrices.
    // out = this * b;
    public Matrix DotProduct(Matrix b)
    {
        // Make sure that these two matrices can be multiplied by each other.
        assert this.NumCols == b.GetRow();

        Matrix out = new Matrix(this.NumRows, b.GetCol());

        for (int i = 0; i < this.NumRows; i++){
            for (int j = 0; j < b.GetCol(); j++){
                for (int k = 0; k < b.GetRow(); k++){
                    out.data[i][j] = out.data[i][j] + this.data[i][k] * b.data[k][j];
                }
            }
        }

        return out;
    }

    static public boolean CompareMatrices(Matrix a, Matrix b)
    {
        if ((a.GetCol() == b.GetCol()) && (a.GetRow() == b.GetRow()))
        {
            for (int i = 0; i < a.GetRow(); i++)
            {
                for (int j = 0; j < a.GetCol(); j++)
                {
                    if (a.data[i][j] != b.data[i][j])
                    {
                        return false;
                    }
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public void PrintMatrix()
    {
        for (int i = 0; i < this.GetRow(); i++)
        {
            for (int j = 0; j < this.GetCol(); j++)
            {
                System.out.print(this.data[i][j]);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
    }
}
