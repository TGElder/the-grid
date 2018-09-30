package main.com.tgelder.grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import de.micromata.opengis.kml.v_2_2_0.*;

public class Grid2
{
	
	private Point topLeft;
	private Point bottomRight;
	private double cellWidth;
	private double cellHeight;
	private Date referenceDate;
	private Function<Cell, Date> dateExtractor;
	
	private int width;
	private int height;
	private Cell[][] cells;
	
	
	public Grid2(Point topLeft, Point bottomRight, double cellWidth, double cellHeight, Date referenceDate,
				 Function<Cell, Date> dateExtractor)
	{
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		this.referenceDate = referenceDate;
		this.dateExtractor = dateExtractor;
		
		width = (int)(Math.ceil((bottomRight.x - topLeft.x)/cellWidth));
		height = (int)(Math.ceil((bottomRight.y - topLeft.y)/cellHeight));
		
		cells =  new Cell[width][height];
		
		for (int x=0; x<width; x++)
		{
			for (int y=0; y<height; y++)
			{
				cells[x][y] = new Cell(new Point(topLeft.x + cellWidth*x,topLeft.y + cellHeight*y,null), new Point(topLeft.x + cellWidth*(x+1),topLeft.y + cellHeight*(y+1),null));
			}
		}
	}
	
	public int computeMaxVisits()
	{
		int out = 0;
		
		for (int x=0; x<width; x++)
		{
			for (int y=0; y<height; y++)
			{
				out = Math.max(out, cells[x][y].getVisits());
			}
		}
		
		return out;
	}
	
	
	public void write(String file)
	{
	
		final Kml kml = KmlFactory.createKml();
				
		final Document document = kml.createAndSetDocument().withName(file).withOpen(false);
						
		final Style labelStyle = document.createAndAddStyle();
		
		final Folder polygonFolder = document.createAndAddFolder().withName("Polygons");
		
		labelStyle.createAndSetIconStyle().withColor("00FFFFFF");
		labelStyle.createAndSetLabelStyle().withScale(0.7);
		
		document.createAndAddStyle().withId("style1").createAndSetPolyStyle().withColor("C0B69F8");
		document.createAndAddStyle().withId("style2").createAndSetPolyStyle().withColor("C06F80F9");
		document.createAndAddStyle().withId("style3").createAndSetPolyStyle().withColor("C07498FA");
		document.createAndAddStyle().withId("style4").createAndSetPolyStyle().withColor("C078AFFB");
		document.createAndAddStyle().withId("style5").createAndSetPolyStyle().withColor("C07DC7FD");
		document.createAndAddStyle().withId("style6").createAndSetPolyStyle().withColor("C081DFFE");
		document.createAndAddStyle().withId("style7").createAndSetPolyStyle().withColor("C084E7F1");
		document.createAndAddStyle().withId("style8").createAndSetPolyStyle().withColor("C082DFD5");
		document.createAndAddStyle().withId("style9").createAndSetPolyStyle().withColor("C080D7B9");
		document.createAndAddStyle().withId("style10").createAndSetPolyStyle().withColor("C07FCF9C");
		document.createAndAddStyle().withId("style11").createAndSetPolyStyle().withColor("C07DC780");
		document.createAndAddStyle().withId("style12").createAndSetPolyStyle().withColor("C07BBE63");


		Polygon KMLpolygon;
		LinearRing linearRing; 
		
		int maxVisits = computeMaxVisits(); 
		
		for (int x=50; x<width; x++)
		{
			for (int y=0; y<height; y++)
			{
				Cell cell = cells[x][y];

				String style;
				
				if (cells[x][y].getVisits()>0)
				{

					long ref = referenceDate.getTime();
										
					double yearsBetween = TimeUnit.DAYS.convert(ref - dateExtractor.apply(cell).getTime(),TimeUnit.MILLISECONDS)/365.25;

					System.out.println(x + ", " + y + " = " + yearsBetween);

					if (yearsBetween<=1)
					{
						style = "style10";
					}
					else if (yearsBetween<=2)
					{
						style = "style9";
					}
					else if (yearsBetween<=3)
					{
						style = "style8";
					}
					else if (yearsBetween<=4)
					{
						style = "style7";
					}
					else if (yearsBetween<=5)
					{
						style = "style6";
					}
					else if (yearsBetween<=6)
					{
						style = "style5";
					}
					else if (yearsBetween<=7)
					{
						style = "style4";
					}
					else if (yearsBetween<=8)
					{
						style = "style3";
					}
					else if (yearsBetween<=9)
					{
						style = "style2";
					}
					else
					{
						style = "style1";
					}
					
					
								
					KMLpolygon = polygonFolder.createAndAddPlacemark().withName(x+","+y).withStyleUrl(style).createAndSetPolygon().withExtrude(true).withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
				
					linearRing = KMLpolygon.createAndSetOuterBoundaryIs().createAndSetLinearRing();
					
					
					linearRing.addToCoordinates(cell.getTopLeft().x, cell.getTopLeft().y);
					linearRing.addToCoordinates(cell.getBottomRight().x, cell.getTopLeft().y);
					linearRing.addToCoordinates(cell.getBottomRight().x, cell.getBottomRight().y);
					linearRing.addToCoordinates(cell.getTopLeft().x, cell.getBottomRight().y);
					
				}
				
			}
			
		}
		
		

		
		try 
		{
			kml.marshal(new File(file));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public void walk(List<Point> points)
	{
		for (Point point : points)
		{
			int x = (int)((point.x - topLeft.x)/cellWidth);
			int y = (int)((point.y - topLeft.y)/cellHeight);
			
			if (x>0&&x<width&&y>0&&y<height)
			{
				cells[x][y].visit(point.time);
			}
		}
	}
	
	public static void main(String[] argv)
	{
		Grid2 grid = new Grid2(new Point(-0.510363135,51.2867019,null),
							   new Point(0.33404439,51.69182417,null),
							   0.007222097,
							   0.004496363,
							   new Date(1458432000000L),
							   cell -> cell.getFirstVist());
		
		grid.walk(GeoJSONReader.read("LocationHistory.json"));
		
		grid.write("lastVisit2.kml");

	}
	

}
