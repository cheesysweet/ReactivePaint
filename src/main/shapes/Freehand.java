package main.se.miun.dt176g.anby2001.reactive.shapes;

import main.se.miun.dt176g.anby2001.reactive.draw.Point;

import java.awt.*;

/**
 * Used for drawing freehand
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */
public class Freehand extends Shape{


    /**
     * stores values in super class
     * @param cords1 first position
     * @param cords2 second position
     * @param thickness line-thickness
     * @param color color of line
     */
    public Freehand(Point cords1, Point cords2, int thickness, Color color) {
        super(cords1, cords2, thickness, color);
    }

    /**
     * draws the line with given cords.
     * @param g graphics
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        g2.setColor(this.getColor()); // sets color
        g2.setStroke(new BasicStroke(this.getLineThickness(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));  // sets line-thickness with rounded corners and sets connection point
        g2.drawLine(this.getCords1().x(), this.getCords1().y(), this.getCords2().x(), this.getCords2().y());
    }
}
