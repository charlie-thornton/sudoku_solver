package sudosolve;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Run
{
	public static void main(String[] args)
		throws IOException
	{
		if (args.length < 1)
		{
			System.err.println("Usage is: java sudosolve.Run <input file>");
			return;
		}
		
		final String fn = args[0];
		final FileReader fr = new FileReader(fn);
		final BufferedReader reader = new BufferedReader(fr);
		final SudokuCell[][] cells = new SudokuCell[9][9];
		for (int row=0; row<9; ++row)
		{
			final String line = reader.readLine();
			for (int col=0; col<9; ++col)
			{
				final String val = line.substring(col, col+1);
				if (val.equals("*"))
				{
					cells[row][col] = new SudokuCell();
				}
				else
				{
					cells[row][col] = new SudokuCell(Integer.parseInt(val));
				}
			}
		}
		reader.close();
		
		Print.printInput(cells);
		
		solve(cells);
		
		
		
	}
	
	private static void solve(SudokuCell[][] cells)
	{
		pruneRows(cells);
		pruneCols(cells);
		pruneBoxes(cells);
		
		boolean needSplit = true;
		int solved = 0;
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown())
				{
					++solved;
					continue;
				}
				else if (c.numVals() == 1)
				{
					cells[row][col].makeKnown();
					++solved;
					needSplit = false;
				}
			}
		}
		
		if (solved == 81 && validate(cells))
		{
			Print.printSolution(cells);
			throw new RuntimeException("Solved!");
		}
		
		if (needSplit)
		{
			split(cells);
		}
		else
		{
			solve(cells);
		}
	}
	
	private static void split(SudokuCell[][] cells)
	{
		int min = getMin(cells);
		assert(min > 1);
		int sRow = -1;
		int sCol = -1;
		List versions = null;
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (c.numVals() == min)
				{
					sRow = row;
					sCol = col;
					versions = c.explode();
				}
			}
		}
		
		for (int i=0; i<versions.size(); ++i)
		{
			final SudokuCell[][] attempt = cloneArr(cells);
			attempt[sRow][sCol] = (SudokuCell)versions.get(i);
			try
			{
				solve(attempt);
			}
			catch (RuntimeException e) { }
		}
	}
	
	private static SudokuCell[][] cloneArr(SudokuCell[][] cells)
	{
		final SudokuCell[][] clone = new SudokuCell[9][9];
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				clone[row][col] = cells[row][col].cloneCell();
			}
		}
		return clone;
	}
	
	private static int getMin(SudokuCell[][] cells)
	{
		int min = Integer.MAX_VALUE;
		for (int row=0; row<9; ++row)
		{
			for (int col=0; col<9; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (!c.isKnown())
				{
					min = Math.min(min, c.numVals());
				}
			}
		}
		return min;
	}
	
	private static boolean validate(SudokuCell[][] cells)
	{
		return validateRows(cells) &&
			validateCols(cells) &&
			validateBox(cells, 0,0) &&
			validateBox(cells, 0,3) &&
			validateBox(cells, 0,6) &&
			validateBox(cells, 3,0) &&
			validateBox(cells, 3,3) &&
			validateBox(cells, 3,6) &&
			validateBox(cells, 6,0) &&
			validateBox(cells, 6,3) &&
			validateBox(cells, 6,6);
	}
	
	private static List getNumList()
	{
		final List nums = new ArrayList();
		for (int i=1; i<=9; ++i)
		{
			nums.add(new Integer(i));
		}
		return nums;
	}
	
	private static boolean validateRows(SudokuCell[][] cells)
	{
		for (int row=0; row<9; ++row)
		{
			final List nums = getNumList();
			for (int col=0; col<9; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown() && nums.contains(c.getKnownVal()))
				{
					nums.remove(c.getKnownVal());
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean validateCols(SudokuCell[][] cells)
	{
		for (int col=0; col<9; ++col)
		{
			final List nums = getNumList();
			for (int row=0; row<9; ++row)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown() && nums.contains(c.getKnownVal()))
				{
					nums.remove(c.getKnownVal());
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean validateBox(SudokuCell[][] cells, int row0, int col0)
	{
		final List nums = getNumList();
		for (int row=row0; row-row0 < 3; ++row)
		{
			for (int col=col0; col-col0 < 3; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown() && nums.contains(c.getKnownVal()))
				{
					nums.remove(c.getKnownVal());
				}
				else
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private static void pruneRows(SudokuCell[][] cells)
	{
		final List knowns = new ArrayList();
		for (int row=0; row<9; ++row)
		{
			knowns.clear();
			for (int col=0; col<9; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown())
				{
					knowns.add(c.getKnownVal());
				}
			}
			
			for (int col=0; col<9; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (!c.isKnown())
				{
					c.prune(knowns);
				}
			}
		}
	}
	
	private static void pruneCols(SudokuCell[][] cells)
	{
		final List knowns = new ArrayList();
		for (int col=0; col<9; ++col)
		{
			knowns.clear();
			
			for (int row=0; row<9; ++row)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown())
				{
					knowns.add(c.getKnownVal());
				}
			}
			
			for (int row=0; row<9; ++row)
			{
				final SudokuCell c = cells[row][col];
				if (!c.isKnown())
				{
					c.prune(knowns);
				}
			}
		}
	}
	
	private static void pruneBox(SudokuCell[][] cells, int row0, int col0)
	{
		final List knowns = new ArrayList();
		for (int row=row0; row-row0 < 3; ++row)
		{
			for (int col=col0; col-col0 < 3; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (c.isKnown())
				{
					knowns.add(c.getKnownVal());
				}
			}
		}
		
		for (int row=row0; row-row0 < 3; ++row)
		{
			for (int col=col0; col-col0 < 3; ++col)
			{
				final SudokuCell c = cells[row][col];
				if (!c.isKnown())
				{
					c.prune(knowns);
				}
			}
		}
	}
	
	private static void pruneBoxes(SudokuCell[][] cells)
	{
		pruneBox(cells, 0,0);
		pruneBox(cells, 0,3);
		pruneBox(cells, 0,6);
		pruneBox(cells, 3,0);
		pruneBox(cells, 3,3);
		pruneBox(cells, 3,6);
		pruneBox(cells, 6,0);
		pruneBox(cells, 6,3);
		pruneBox(cells, 6,6);
	}
}
