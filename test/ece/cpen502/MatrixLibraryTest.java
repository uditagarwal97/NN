package ece.cpen502;

import org.junit.Test;

public class MatrixLibraryTest {

    @Test
    public void TestBasicMatrixFunc()
    {
        Matrix test = new Matrix(3, 4);
        test.InitZero();

        assert test.GetRow() == 3;
        assert test.GetCol() == 4;

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                assert test.data[i][j] == 0;
            }
        }
    }

    @Test
    public void TestMatrixAdd()
    {
        Matrix a = new Matrix(3, 4, 1);
        Matrix b = new Matrix(3, 4, 2);
        Matrix c = a.AddMatrix(b);

        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                assert c.data[i][j] == 3;
            }
        }
    }

    @Test
    public void TestMatrixDotProduct()
    {
        Matrix a = new Matrix(3, 4, 1);
        Matrix b = new Matrix(4, 4, 1);
        Matrix c = a.DotProduct(b);

        assert c.GetRow() == a.GetRow();
        assert c.GetCol() == b.GetCol();

        for (int i = 0; i < c.GetRow(); i++)
        {
            for (int j = 0; j < c.GetCol(); j++)
            {
                assert c.data[i][j] == 4;
            }
        }
    }

    @Test
    public void TestMatrixConstructorAndCompare()
    {
        // Create a 3 X 2 matrix
        Matrix a = new Matrix(2, new double[] {1, 1, 1, 1, 1, 1});
        assert a.GetRow() == 3;
        assert a.GetCol() == 2;

        Matrix b = new Matrix(3, 2, 1);
        assert Matrix.CompareMatrices(a, b) == true;
    }
}
