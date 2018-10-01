package main.com.tgelder.grid;

import lombok.Getter;

import java.util.List;

public class Grid {

  private final Point topLeft;
  private final Point bottomRight;
  private final double cellWidth;
  private final double cellHeight;

  @Getter
  private final int width;
  @Getter
  private final int height;
  private final Cell[][] cells;


  public Grid(Point topLeft, Point bottomRight, double cellWidth, double cellHeight) {
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
    this.cellWidth = cellWidth;
    this.cellHeight = cellHeight;

    width = (int) (Math.ceil((bottomRight.x - topLeft.x) / cellWidth));
    height = (int) (Math.ceil((bottomRight.y - topLeft.y) / cellHeight));

    cells = new Cell[width][height];

    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        cells[x][y] = new Cell(new Point(topLeft.x + cellWidth * x,
                                         topLeft.y + cellHeight * y,
                                         null),
                               new Point(topLeft.x + cellWidth * (x + 1),
                                         topLeft.y + cellHeight * (y + 1),
                                         null));
      }
    }
  }


  public void visitCells(List<Point> points) {
    for (Point point : points) {
      int x = (int) ((point.x - topLeft.x) / cellWidth);
      int y = (int) ((point.y - topLeft.y) / cellHeight);

      if (x > 0 && x < width && y > 0 && y < height) {
        cells[x][y].visit(point.time);
      }
    }
  }

  public Cell getCell(int x, int y) {
    return cells[x][y];
  }


}
