package main.shapes;

import main.draw.Point;

import java.awt.*;

/**
 * Creates a rectangular shape
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */
public class Rectangle extends Shape{

    /**
     * stores values in super class
     * @param cords1 first position
     * @param cords2 second position
     * @param thickness line-thickness
     * @param color color of line
     */
    public Rectangle(Point cords1, Point cords2, int thickness, Color color) {
        super(cords1, cords2, thickness, color);
    }

    /**
     * draws a rectangle with given cords.
     * @param g graphics
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        g2.setColor(this.getColor());
        g2.setStroke(new BasicStroke(this.getLineThickness()));
        g2.drawRect(this.getCords()[0], this.getCords()[1], this.getCords()[2], this.getCords()[3]);
    }
}
