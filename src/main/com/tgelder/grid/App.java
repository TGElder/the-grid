package main.com.tgelder.grid;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class App {

  public static void main(String[] argv) {
    Grid grid = new Grid(new Point(-0.510363135, 51.2867019, null),
                         new Point(0.33404439, 51.69182417, null),
                         0.007222097,
                         0.004496363);

//    Grid grid = new Grid(new Point(-6.216667,49.75, null),
//                         new Point(1.766667, 58.666667, null),
//                         0.159666667,
//                         0.08716667);

    List<Point> points = GeoJSONReader.read("LocationHistory.json");

    grid.visitCells(points);

    long referenceTime = new Date().getTime();

    GridWriter frequency = new GridWriter(
        cell -> 10 - (int)(Math.log(cell.getVisits())/Math.log(2))
    );

    frequency.write(grid, "frequency.kml");
    //frequency.write(grid, "frequencyRight.kml", 50, grid.getWidth(), 0, grid.getHeight());

    GridWriter firstVisit = new GridWriter(
        cell -> {
          int yearsBetween = (int)(TimeUnit.DAYS.convert(referenceTime - cell.getFirstVist().getTime(), TimeUnit.MILLISECONDS)/365.25);
          return 10 - (yearsBetween * 2);
        }
    );

    firstVisit.write(grid, "firstVisit.kml");

    GridWriter lastVisit = new GridWriter(
        cell -> {
          int yearsBetween = (int)(TimeUnit.DAYS.convert(referenceTime - cell.getLastVisit().getTime(), TimeUnit.MILLISECONDS)/365.25);
          return 10 - (yearsBetween * 2);
        }
    );

    lastVisit.write(grid, "lastVisit.kml");

  }

}
