package main.shapes;

import main.draw.Point;

import java.awt.*;

/**
 * Creates a line
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */
public class Line extends Shape{

    /**
     * stores values in super class
     * @param cords1 first position
     * @param cords2 second position
     * @param thickness line-thickness
     * @param color color of line
     */
    public Line(main.draw.Point cords1, main.draw.Point cords2, int thickness, Color color) {
        super(cords1, cords2, thickness, color);
    }

    /**
     * draws the line with given cords.
     * @param g graphics
     */
    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Type-cast the parameter to Graphics2D.

        g2.setColor(this.getColor());
        g2.setStroke(new BasicStroke(this.getLineThickness()));
        g2.drawLine(this.getCords1().x(), this.getCords1().y(), this.getCords2().x(), this.getCords2().y());
    }
}
