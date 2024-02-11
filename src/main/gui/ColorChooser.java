package main.gui;

import javax.swing.*;
import java.awt.*;

/**
 * <h1>ColorChooser</h1>
 * Color chooser panel
 *
 * @author 	Anton Byström
 * @version 1.0
 * @since 	2022-10-30
 */
public class ColorChooser extends JColorChooser {


    public ColorChooser() {
        //remove chooser panels
        this.removeChooserPanel(this.getChooserPanels()[4]);
        this.removeChooserPanel(this.getChooserPanels()[3]);
        this.removeChooserPanel(this.getChooserPanels()[2]);
        this.removeChooserPanel(this.getChooserPanels()[1]);

        //remove preview panel
        this.setPreviewPanel(new JPanel());

        this.setColor(Color.BLACK);

    }

}
