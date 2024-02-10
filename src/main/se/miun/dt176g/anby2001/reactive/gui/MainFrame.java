package main.se.miun.dt176g.anby2001.reactive.gui;

import main.se.miun.dt176g.anby2001.reactive.draw.DrawingPanel;

import java.awt.*;
import javax.swing.*;

/**
 * <h1>MainFrame</h1> 
 * JFrame to contain the rest
 *
 * @author 	Anton Byström
 * @version 1.0
 * @since 	2022-09-12
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private final DrawingPanel drawingPanel;

	/**
	 * Main frame that adds the menu bar and drawing panel
	 */
	public MainFrame() {

		// default window-size.
		this.setSize(1200, 900);
		// application closes when the "x" in the upper-right corner is clicked.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		String header = "Reactive Paint";
		this.setTitle(header);

		// Changes layout from default to BorderLayout
		this.setLayout(new BorderLayout());

		// Creates a new drawing panel
		drawingPanel = new DrawingPanel();

		// Menu bar
		Menu menu = new Menu(drawingPanel);
		this.setJMenuBar(menu);

		// Stores selected shape, line thickness, color
		drawingPanel.setDrawingPanel(
				menu.getShape(),
				menu.getLineThickness(),
				menu
		);
		drawingPanel.setBounds(0, 0, getWidth(), getHeight());
		this.getContentPane().add(drawingPanel, BorderLayout.CENTER);

	}


}
