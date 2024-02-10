package main.se.miun.dt176g.anby2001.reactive.draw;


import main.se.miun.dt176g.anby2001.reactive.shapes.Shape;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <h1>Drawing</h1> 
 * Let this class store an arbitrary number of AbstractShape-objects in
 * some kind of container. 
 *
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */


public class Drawing implements Drawable, Serializable {


	// private SomeContainer shapes;
	private final List<Shape> shapeList = new ArrayList<>();
	private Shape tempShape; // temporary shape that is displayed when starting to draw.
	private Shape saveShape;

	/**
	 * add a shape to the shapeList
	 * @param s a {@link Shape} object.
	 */
	public void addShape(Shape s) {
		synchronized (this.shapeList) {
			shapeList.add(s);
		}
	}

	public void tempShape(Shape s) {
		this.tempShape = s;
	}

	public void saveShape(Shape s) {
		this.saveShape = s;
	}

	public Shape getShape() {
		return saveShape;
	}
	public List<Shape> getShapeList() {return shapeList;}


	@Override
	public void draw(Graphics g) {
		// iterate over all shapes and draw them using the draw-method found in
		// each concrete subclass.
		synchronized (this.shapeList) {
			for (Shape shape: shapeList) {
				shape.draw(g);
			}
		}
		if (tempShape != null) {
			tempShape.draw(g);
		}
		if (saveShape != null) {
			saveShape.draw(g);
		}
	}

}
