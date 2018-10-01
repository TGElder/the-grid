package main.com.tgelder.grid;

import de.micromata.opengis.kml.v_2_2_0.*;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
public class GridWriter {

  private final Function<Cell, Integer> classifer;

  void write(Grid grid, String file) {

    final Kml kml = KmlFactory.createKml();
    final Document document = kml.createAndSetDocument().withName(file).withOpen(false);
    final Style labelStyle = document.createAndAddStyle();

    labelStyle.createAndSetIconStyle().withColor("00FFFFFF");
    labelStyle.createAndSetLabelStyle().withScale(0.7);

    final List<Folder> folders = IntStream
        .range(0, 12)
        .mapToObj(i -> document.createAndAddFolder().withName(String.valueOf(i)))
        .collect(Collectors.toList());

    document.createAndAddStyle().withId("style0").createAndSetPolyStyle().withColor("C06B69F8");
    document.createAndAddStyle().withId("style1").createAndSetPolyStyle().withColor("C06F80F9");
    document.createAndAddStyle().withId("style2").createAndSetPolyStyle().withColor("C07498FA");
    document.createAndAddStyle().withId("style3").createAndSetPolyStyle().withColor("C078AFFB");
    document.createAndAddStyle().withId("style4").createAndSetPolyStyle().withColor("C07DC7FD");
    document.createAndAddStyle().withId("style5").createAndSetPolyStyle().withColor("C081DFFE");
    document.createAndAddStyle().withId("style6").createAndSetPolyStyle().withColor("C084E7F1");
    document.createAndAddStyle().withId("style7").createAndSetPolyStyle().withColor("C082DFD5");
    document.createAndAddStyle().withId("style8").createAndSetPolyStyle().withColor("C080D7B9");
    document.createAndAddStyle().withId("style9").createAndSetPolyStyle().withColor("C07FCF9C");
    document.createAndAddStyle().withId("style10").createAndSetPolyStyle().withColor("C07DC780");
    document.createAndAddStyle().withId("style11").createAndSetPolyStyle().withColor("C07BBE63");


    Polygon KMLpolygon;
    LinearRing linearRing;

    for (int x = 0; x < grid.getWidth(); x++) {
      for (int y = 0; y < grid.getHeight(); y++) {
        Cell cell = grid.getCell(x, y);

        if (cell.getVisits() > 0) {

          Integer classification = classifer.apply(cell);

          Folder folder = folders.get(classification);
          String style = "style" + classification;

          KMLpolygon = folder
              .createAndAddPlacemark()
              .withName(x + "," + y)
              .withStyleUrl(style)
              .createAndSetPolygon()
              .withExtrude(true)
              .withAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);

          linearRing = KMLpolygon.createAndSetOuterBoundaryIs().createAndSetLinearRing();

          linearRing.addToCoordinates(cell.getTopLeft().x, cell.getTopLeft().y);
          linearRing.addToCoordinates(cell.getBottomRight().x, cell.getTopLeft().y);
          linearRing.addToCoordinates(cell.getBottomRight().x, cell.getBottomRight().y);
          linearRing.addToCoordinates(cell.getTopLeft().x, cell.getBottomRight().y);

        }
      }
    }

    try {
      kml.marshal(new File(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

}
