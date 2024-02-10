package main.se.miun.dt176g.anby2001.reactive.draw;

import io.reactivex.rxjava3.core.Observable;
import main.se.miun.dt176g.anby2001.reactive.Constants;
import main.se.miun.dt176g.anby2001.reactive.gui.Menu;
import main.se.miun.dt176g.anby2001.reactive.shapes.Oval;
import main.se.miun.dt176g.anby2001.reactive.shapes.Freehand;
import main.se.miun.dt176g.anby2001.reactive.shapes.Line;
import main.se.miun.dt176g.anby2001.reactive.shapes.Rectangle;
import main.se.miun.dt176g.anby2001.reactive.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;

/**
 * <h1>DrawingPanel</h1> Creates a Canvas-object for displaying all graphics
 * already drawn.
 *
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */

@SuppressWarnings("serial")
public class DrawingPanel extends JPanel {

	private Drawing drawing; // drawing panel
	private Menu menu; // menu frame
	private Boolean save; // if drawing should be saved

	// defines shape and attributes
	private String shape;
	private Integer lineThickness;
	private Color color;

	/**
	 * observes when new drawings is created and stores them
	 */
	public DrawingPanel() {
		drawing = new Drawing();


		observeDraw()
				.subscribe(shape -> {
					Shape storeShape = drawShape(shape.get(0),shape.get(1)); // draws the shape
					if (menu.getSinglePlayer() && save) {
						this.drawing.addShape(storeShape);
					}
					if (save) {
						this.drawing.saveShape(storeShape);
					} else {
						this.drawing.tempShape(storeShape);
					}
					redraw();
				});

	}


	/**
	 * stores selected variables
	 * @param shape selected shape
	 * @param lineThickness selected line thickness
	 */
	public void setDrawingPanel(Observable<String> shape, Observable<Integer> lineThickness, Menu menu) {
		shape.subscribe(s -> {
			this.shape = s;
		});

		lineThickness.subscribe(i -> {
			this.lineThickness = i;
		});

		this.menu = menu;
	}

	/**
	 * stores a shape and redraws frame
	 * @param s Shape to save
	 */
	public void addShape(Shape s) {
		this.drawing.addShape(s);
		redraw();
	}

	/**
	 * repaints frame
	 */
	public void redraw() {
		repaint();
	}

	/**
	 * clears drawing panel
	 * @param d drawing
	 */
	public void setDrawing(Drawing d) {
		drawing = d;
		repaint();
	}

	public Drawing getDrawing() {
		return drawing;
	}

	/**
	 * paints the drawing panel
	 * @param g the <code>Graphics</code> object to protect
	 */
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		drawing.draw(g);
	}

	/**
	 * checks for mouse events and performs actions
	 * @return observable of current drawing
	 */
	private Observable<List<Point>> observeDraw() {

		return Observable.create(subscribe -> {
			MouseAdapter mouseAdapter = new MouseAdapter() {
				Point fPoint;
				@Override
				public void mousePressed(MouseEvent e) { // starts drawing
					fPoint = new Point(e.getX(), e.getY());
				}

				@Override
				public void mouseReleased(MouseEvent e) { // end point of drawing
					save = true;
					subscribe.onNext(Arrays.asList(fPoint, new Point(e.getX(), e.getY())));
				}

				@Override
				public void mouseDragged(MouseEvent e) { // displays current mouse events
					save = false;
					if (shape.equals(Constants.Freehand)) { // only stores each event if freehand is chosen
						save = true;
						subscribe.onNext(Arrays.asList(fPoint, new Point(e.getX(),e.getY())));
						fPoint = new Point(e.getX(), e.getY());
					}else {
						subscribe.onNext(Arrays.asList(fPoint, new Point(e.getX(), e.getY())));
					}
				}
			};
			this.addMouseListener(mouseAdapter);
			this.addMouseMotionListener(mouseAdapter);
		});
	}

	/**
	 * send the latest shape to server
	 * @return shape
	 */
	public Observable<Shape> shapeObservable() {
		return Observable.create(subscribe -> {
			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					subscribe.onNext(drawing.getShape());
				}
				@Override
				public void mouseDragged(MouseEvent e) { // displays current mouse events
					if (save) {
						subscribe.onNext(drawing.getShape());
					}
				}
			};

			this.addMouseListener(mouseAdapter);
			this.addMouseMotionListener(mouseAdapter);
		});
	}

	/**
	 * creates a new shape depending on chosen shape with attributes
	 * @param cords1 first set of coordinates of drawing
	 * @param cords2 second set of coordinates of drawing
	 * @return shape that will be drawn on the frame
	 */
	private Shape drawShape(Point cords1, Point cords2) {
		if (shape.equals(Constants.Oval)){
			return new Oval(cords1, cords2, lineThickness, getCurrentColor());
		}
		if (shape.equals(Constants.Line)){
			return new Line(cords1, cords2, lineThickness, getCurrentColor());
		}
		if (shape.equals(Constants.Rectangle)){
			return new Rectangle(cords1, cords2, lineThickness, getCurrentColor());
		} else {
			return new Freehand(cords1, cords2, lineThickness, getCurrentColor());
		}
	}

	private Color getCurrentColor() {
		return menu.getColorChooser().getColor();
	}
}
