package sudosolve;

import java.util.List;
import java.util.ArrayList;

public class SudokuCell
{
	private List m_vals;
	private boolean m_known;
	
	public SudokuCell(int val)
	{
		m_vals = new ArrayList();
		m_vals.add(new Integer(val));
		m_known = true;
	}
	
	public SudokuCell()
	{
		m_vals = new ArrayList();
		for (int i=1; i<=9; ++i)
		{
			m_vals.add(new Integer(i));
		}
		m_known = false;
	}
	
	public SudokuCell cloneCell()
	{	
		final SudokuCell cell = new SudokuCell();
		cell.m_known = m_known;
		cell.m_vals = (List)((ArrayList)m_vals).clone();
		
		return cell;
	}
	
	public boolean isKnown()
	{
		return m_known;
	}
	
	public void makeKnown()
	{
		assert(m_vals.size() == 1);
		m_known = true;
	}
	
	public Integer getKnownVal()
	{
		assert(isKnown());
		return (Integer)m_vals.get(0);
	}
	
	public int numVals()
	{
		return m_vals.size();
	}
	
	public List explode()
	{
		final List versions = new ArrayList();
		for (int i=0; i<m_vals.size(); ++i)
		{
			versions.add(new SudokuCell(((Integer)m_vals.get(i)).intValue()));
		}
		return versions;
	}
	
	public void prune(List nonVals)
	{
		m_vals.removeAll(nonVals);
		if (m_vals.isEmpty())
		{
			throw new RuntimeException("Invalid Path");
		}
	}
	
	public String toString()
	{
		if (m_known)
		{
			return getKnownVal().toString();
		}
		else
		{
			return "*";
		}
	}
}
