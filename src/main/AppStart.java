package main;

import main.gui.MainFrame;

import javax.swing.SwingUtilities;


/**
* <h1>AppStart</h1>
*
* @author  Anton Byström
* @version 1.0
* @since   2022-09-12
*/
public class AppStart {

	public static void main(String[] args) {
		
		// Make sure GUI is created on the event dispatching thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}
}