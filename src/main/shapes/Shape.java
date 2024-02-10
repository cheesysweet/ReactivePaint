package main.se.miun.dt176g.anby2001.reactive.shapes;


import main.se.miun.dt176g.anby2001.reactive.draw.Drawable;
import main.se.miun.dt176g.anby2001.reactive.draw.Point;

import java.awt.*;
import java.io.Serializable;

/**
 * <h1>Shape</h1> Abstract class which derived classes builds on.
 * This class consists of the attributes common to all geometric shapes.
 * Specific shapes are based on this class.
 *
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */

public abstract class Shape implements Drawable, Serializable {
    private static final long serialVersionUID = 1L;
    private final Point cords1; // first coordinate position
    private final Point cords2; // second coordinate position
    private final int lineThickness; // line-thickness
    private final Color color; // color

    /**
     * stores values of all params
     * @param cords1 first position
     * @param cords2 second position
     * @param thickness line-thickness
     * @param color color of line
     */
    protected Shape(Point cords1, Point cords2, int thickness, Color color) {
        this.cords1 = cords1;
        this.cords2 = cords2;
        this.lineThickness = thickness;
        this.color = color;
    }


    /**
     * @return first coordinate position
     */
    protected Point getCords1() {
        return cords1;
    }

    /**
     * @return second coordinate position
     */
    protected Point getCords2() { return cords2; }

    /**
     * @return line-thickness
     */
    protected int getLineThickness() { return lineThickness; }

    /**
     * @return color
     */
    protected Color getColor() { return color; }

    /**
     * sets the max / min position of the drawn positions
     * @return int list of coordinates
     */
    protected int[] getCords() {
        int[] cords = new int[4];
        int xMin = Math.min(this.getCords1().x(), this.getCords2().x());
        int yMin = Math.min(this.getCords1().y(), this.getCords2().y());
        int xMax = Math.max(this.getCords1().x(), this.getCords2().x());
        int yMax = Math.max(this.getCords1().y(), this.getCords2().y());

        cords[0] = xMin;
        cords[1] = yMin;
        cords[2] = xMax-xMin;
        cords[3] = yMax-yMin;

        return cords;
    }
}
