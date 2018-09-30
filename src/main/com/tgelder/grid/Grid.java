package main.com.tgelder.grid;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.*;

public class Grid
{
	
	private Point topLeft;
	private Point bottomRight;
	private double cellWidth;
	private double cellHeight;
	
	private int width;
	private int height;
	private Cell[][] cells;
	
	
	public Grid(Point topLeft, Point bottomRight, double cellWidth, double cellHeight)
	{
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		
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
		
		

		Polygon KMLpolygon;
		LinearRing linearRing; 
		
		int maxVisits = computeMaxVisits(); 
		
		for (int x=0; x<width; x++)
		{
			for (int y=0; y<height; y++)
			{
				Cell cell = cells[x][y];

				String style;
				
				if (cells[x][y].getVisits()>0)
				{
					int score = (int)(128*((Math.log10(cells[x][y].getVisits()))/(Math.log10(maxVisits))));
					
					String scoreString = Integer.toHexString(127 + score).toUpperCase(); 
					
					document.createAndAddStyle().withId("x"+x+"y"+y).createAndSetPolyStyle().withColor("C0"+scoreString+scoreString+scoreString);

					style = "#x"+x+"y"+y;
								
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
		Grid grid = new Grid(new Point(-0.510363135,51.2867019,null),new Point(0.33404439,51.69182417,null),0.007222097,0.004496363);
		
		grid.walk(GeoJSONReader.read("LocationHistory.json"));
		
		grid.write("test.kml");

	}
	

}
