/**
 * JComponentUtil
 * Author: Neil Balaskandarajah
 * Created on: 28/02/2020
 * Use pre-configured Swing components with ease
 */

package util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import graphics.Painter;
import main.AutoSim;

public class JComponentUtil {
	// PANELS//

	/**
	 * Creates a panel with a specified box layout
	 * @param horizontal whether the panel is horizontal or not
	 */
	public static JPanel boxPanel(boolean horizontal) {
		JPanel panel = new JPanel();
		BoxLayout box;

		// set to horizontal or vertical
		if (horizontal) {
			box = new BoxLayout(panel, BoxLayout.X_AXIS);
		} else {
			box = new BoxLayout(panel, BoxLayout.Y_AXIS);
		} // if

		panel.setLayout(box);

		return panel;
	} // end boxPanel

	/**
	 * Creates a panel with a border layout
	 */
	public static JPanel borderPanel() {
		JPanel panel = new JPanel();

		// set to border layout
		BorderLayout border = new BorderLayout();
		panel.setLayout(border);

		return panel;
	} // end borderPanel

	/**
	 * Create a panel with a grid layout 
	 * @param rows - number of rows in the grid 
	 * @param cols - number of columns in the grid
	 */
	public static JPanel gridPanel(int rows, int cols) {
		JPanel panel = new JPanel();
		GridLayout grid = new GridLayout(rows, cols, 5, 5);
		panel.setLayout(grid);

		return panel;
	} // end gridPanel
	
	/**
	 * Create a JPanel holding a JComponent
	 * @param component JComponent to be put in JPanel
	 * @return vanilla JPanel containing component
	 */
	public static JPanel panelFromComponent(JComponent component) {
		JPanel panel = new JPanel();
		panel.add(component);
		return panel;
	} //end panelFromComponent

	// WIDGETS//
	/**
	 * Create a checkbox of a standard size 
	 * @param label - component label
	 */
	public static JCheckBox checkBox(String label) {
		JCheckBox checkBox = new JCheckBox(label);
		checkBox.setPreferredSize(new Dimension(75, 25));

		return checkBox;
	} // end checkBox

	/**
	 * Create a slider of a standard size 
	 * @param min - left slider value 
	 * @param max - right slider value
	 */
	public static JSlider slider(int min, int max) {
		JSlider slider = new JSlider(min, max);
		slider.setPreferredSize(new Dimension(200, 25));
		slider.setValue(min);

		return slider;
	} // end slider

	/**
	 * Creates a radio button with a label and standard size 
	 * @param label - component label
	 */
	public static JRadioButton radioButton(String label) {
		JRadioButton radio = new JRadioButton(label);
		radio.setPreferredSize(new Dimension(110, 25));

		return radio;
	} // end radioButton

	/**
	 * Create a text area
	 */
	public static JTextArea textArea(int width, int height) {
		JTextArea text = new JTextArea();
		text.setPreferredSize(new Dimension(width, height));

		return text;
	} // end textArea
	
	/**
	 * Create a text pane
	 */
	public static JTextPane textPane(int width, int height) {
		JTextPane text = new JTextPane();
		text.setPreferredSize(new Dimension(width, height));

		return text;
	} // end textArea
	
	/**
	 * Create a text field
	 * @param text Text in the text field
	 * @param width Width in pixels
	 * @param height Height in pixels
	 * @param fontSize Size of font in pixels
	 */
	public static JTextField textField(String text, int width, int height, int fontSize) {
		JTextField textField = new JTextField(text);
		textField.setPreferredSize(new Dimension(width, height));
		textField.setFont(Painter.createFont(Painter.SF_UI_FONT, AutoSim.PPI * 10));
		textField.setHorizontalAlignment(SwingConstants.CENTER); //center text
		textField.setBorder(BorderFactory.createLineBorder(Color.black, (int) (AutoSim.PPI * 0.2)));

		return textField;
	} // end textArea

	/**
	 * Create a button with a title of standard size 
	 * @param label - component label
	 */
	public static JButton button(String label) {
		JButton button = new JButton(label);
//		button.setPreferredSize(new Dimension(width, height));

		return button;
	} // end button

	/**
	 * Return a partially configured frame 
	 * @param name - window title
	 */
	public static JFrame frame(String name) {
		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setFocusable(true);

		return frame;
	} // end frame
	
	/**
	 * Create a GridBagConstraints object based on parameters for the layout
	 * @param gridx - x position to contrain to
	 * @param gridy - y position to constrain to
	 * @param weightx - x weight to contrain to
	 * @param weighty - y weight to constrain to
	 * @return gbc - GridBagConstraints with parameters
	 */
	public static GridBagConstraints createGBC(int gridx, int gridy, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.fill = GridBagConstraints.BOTH; //default to filling entire cell
		
		return gbc;
	} //end createGBC
	
	/**
	 * Create a GridBagConstraints object based on parameters for the layout
	 * @param gridx - x position to contrain to
	 * @param gridy - y position to constrain to
	 * @return gbc - GridBagConstraints with parameters
	 */
	public static GridBagConstraints createGBC(int gridx, int gridy) {
		return createGBC(gridx, gridy, 0, 0);
	} //end createGBC
	
	/**
	 * Create an empty border with thickness pad
	 * @param pad Thickness of border edges in pixels
	 * @return Empty border with thickness pad
	 */
	public static Border paddedBorder(int pad) {
		return BorderFactory.createEmptyBorder(pad, pad, pad, pad);
	} //end paddedBorder
} // end class