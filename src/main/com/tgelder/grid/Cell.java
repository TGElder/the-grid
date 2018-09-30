package main.com.tgelder.grid;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Cell
{
	
	private Point topLeft;
	private Point bottomRight;

	
	private Collection<Date> visits = new HashSet<Date> ();
	
	private Date firstVisit = null;
	private Date lastVisit = null;
	
	Cell(Point topLeft, Point bottomRight)
	{
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	public Point getTopLeft()
	{
		return topLeft;
	}

	public Point getBottomRight()
	{
		return bottomRight;
	}
	
	public void visit(Date date)
	{
		visits.add(date);
		if (firstVisit==null)
		{
			firstVisit = date;
			lastVisit = date;
		}
		else
		{
			if (date.before(firstVisit))
			{
				firstVisit = date;
			}
			if (date.after(lastVisit))
			{
				lastVisit = date;
			}
		}
	
		
	}
	
	public int getVisits()
	{
		return visits.size();
		
	}
	
	public Date getFirstVist()
	{
		return firstVisit;
	}
	
	public Date getLastVisit()
	{
		return lastVisit;
	}


}
