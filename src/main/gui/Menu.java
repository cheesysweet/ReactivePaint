package main.se.miun.dt176g.anby2001.reactive.gui;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import main.se.miun.dt176g.anby2001.reactive.Constants;
import main.se.miun.dt176g.anby2001.reactive.draw.Drawing;
import main.se.miun.dt176g.anby2001.reactive.draw.DrawingPanel;
import main.se.miun.dt176g.anby2001.reactive.multiplayer.Client;
import main.se.miun.dt176g.anby2001.reactive.multiplayer.Server;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;


/**
 * <h1>Menu</h1> 
 *
 * @author  Anton Byström
 * @version 1.0
 * @since   2022-09-12
 */
public class Menu extends JMenuBar {

	private boolean server = false;
	private boolean client = false;

	private static final long serialVersionUID = 1L;
	private final DrawingPanel drawingPanel;
	private List<AbstractButton> shapeButtons;
	private Observable<Integer> lineThickness;
	private Boolean singlePlayer = true;
	private ColorChooser colorChooser;

	/**
	 * menu bar on gui
	 *
	 * @param drawingPanel drawing panel
	 */
	public Menu(DrawingPanel drawingPanel) {
		init();
		this.drawingPanel = drawingPanel;
	}

	/**
	 * sets all buttons in the menu frame
	 */
	private void init() {

		JMenu menu;

		menu = new JMenu("Some Menu category");
		this.add(menu);

		// Clear panel
		JButton clear = new JButton("Clear"); // clears drawing
		clear.addActionListener(e -> drawingPanel.setDrawing(new Drawing()));
		this.add(clear);

		// Choose shape
		JToggleButton freehand = new JToggleButton(Constants.Freehand);
		JToggleButton line = new JToggleButton(Constants.Line);
		JToggleButton rectangle = new JToggleButton(Constants.Rectangle);
		JToggleButton oval = new JToggleButton(Constants.Oval);
		freehand.doClick();

		ButtonGroup shapes = new ButtonGroup();
		shapes.add(freehand);
		shapes.add(line);
		shapes.add(rectangle);
		shapes.add(oval);

		this.add(freehand);
		this.add(line);
		this.add(rectangle);
		this.add(oval);

		shapeButtons = Collections.list(shapes.getElements());

		// Gets line thickness
		JSlider lineThick = new JSlider(JSlider.HORIZONTAL, 1,30,5);
		lineThickness = getLineThicknessObservable(lineThick);
		this.add(lineThick);
		lineThick.setMaximumSize(new Dimension(150,20));

		// Connection labels for hosting & connecting
		JLabel hosting = new JLabel("hosting");
		hosting.setForeground(Color.red);
		JLabel connected = new JLabel("connected");
		connected.setForeground(Color.red);
		this.add(hosting);
		this.add(connected);

		// Color chooser
		colorChooser = new ColorChooser();
		this.add(colorChooser);

		// Host server
		JMenuItem host = new JMenuItem("Host");
		if (!server) {
			host.addActionListener(e -> {
			new Thread(Server::new).start();
					multiplayer();
					hosting.setForeground(Color.green);
			});
		}
		menu.add(host);

		// Connect to server
		JMenuItem connect = new JMenuItem("Connect");
		if (!client) { //
			connect.addActionListener(e -> {
			new Thread(() -> {
				try {
					connected.setForeground(Color.green);
					multiplayer();
					new Client(drawingPanel);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}).start();
			});
		}
		menu.add(connect);
	}

	/**
	 * clears drawingPanel and only stores shapes through server
	 */
	private void multiplayer() {
		drawingPanel.setDrawing(new Drawing()); // clears drawing panel
		singlePlayer = false;
	}


	/**
	 * observes chosen selected shape
	 * @param buttonGroup buttons
	 * @return string observables
	 */
	private Observable<String> getShapeObservable(List<AbstractButton> buttonGroup) {

		return Observable.create(emitter -> {
			for (AbstractButton button: buttonGroup) {
				if (button.isSelected()) { // fetches the default shape
					emitter.onNext(button.getText());
				}
				button.addActionListener(e -> { // adds actionListener to change shape on button press
					emitter.onNext(e.getActionCommand());
				});
			}
		});
	}

	/**
	 * observes line-thickness
	 * @param lineThickness slider
	 * @return int observable
	 */
	private Observable<Integer> getLineThicknessObservable(JSlider lineThickness) {
		return Observable.create(emitter -> {
			emitter.onNext(lineThickness.getValue()); // fetches the default value
			lineThickness.addChangeListener(e -> { // add changeListener to update value
				emitter.onNext(lineThickness.getValue());
			});
		});
	}

	/**
	 * @return chosen shape
	 */
	public Observable<String> getShape() {
		return getShapeObservable(shapeButtons).map(String::valueOf);
	}

	/**
	 * @return line-thickness
	 */
	public Observable<Integer> getLineThickness() {
		return lineThickness;
	}

	/**
	 * @return true if single player
	 */
	public Boolean getSinglePlayer() {
		return singlePlayer;
	}

	/**
	 * @return colorChooser
	 */
	public ColorChooser getColorChooser() {
		return colorChooser;
	}
}
