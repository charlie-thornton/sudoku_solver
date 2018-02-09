package sudosolve;

public class Print
{
	public static void printInput(SudokuCell[][] cells)
	{
		System.out.println("SUDOKU PUZZLE");
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				System.out.print(cells[row][col]);
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printConstraints(SudokuCell[][] cells)
	{
		System.out.println("OPTIONS");
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				if (cells[row][col].isKnown())
				{
					System.out.print("X");
				}
				else
				{
					System.out.print(cells[row][col].numVals());
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void printSolution(SudokuCell[][] cells)
	{
		System.out.println("SOLUTION");
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				System.out.print(cells[row][col].getKnownVal());
			}
			System.out.println();
		}
		System.out.println();
	}
}
