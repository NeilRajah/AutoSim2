/**
 * Widget
 * Author: Neil Balaskandarajah
 * Created on: 29/02/2020
 * Template for all widgets to be added to the UI
 */

package graphics.widgets;

import javax.swing.JPanel;

import util.Util.ROBOT_KEY;

public abstract class Widget {
	//Attributes
	private boolean toggled; //whether widget is collapsed or not
	protected ROBOT_KEY[] keyArray; //values to get from the HashMap when updated
	private JPanel panel; //graphical component in widget
	
	/**
	 * Create widget with a component
	 * @param panel graphical component for widget
	 */
	public Widget(JPanel panel) {
		//set attributes
		this.panel = panel;
		
		//layout the view of the widget
		layoutWidg();
	}
	
	public abstract void update(double[] values);
	
	/**
	 * Get the height of the widget 
	 * @return height in pixels
	 */
	public double getHeight() {
		return panel.getHeight();
	}
	
	/**
	 * Get the width of the widget
	 * @return width in pixels
	 */
	public double getWidth() {
		return panel.getWidth();
	}
	
	/**
	 * Get the key values in array form
	 * @return array of IDs used when updating the widget
	 */
	public ROBOT_KEY[] getKeyArray() {
		return keyArray;
	}
	
	/**
	 * Get the JPanel holding the widget
	 * @return
	 */
	public JPanel getComponent() {
		return panel;
	}
	
	/**
	 * Return whether or not the widget is toggled
	 * @return toggled
	 */
	public boolean getToggled() {
		return toggled;
	}
	
	/**
	 * Toggle the state of the widget and update it
	 */
	public void toggle() {
		toggled = !toggled;
	}
	
	/**
	 * Layout the widget, including its component panel
	 */
	private void layoutWidg() {		
		
	}
}