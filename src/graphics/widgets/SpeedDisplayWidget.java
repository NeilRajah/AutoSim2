package graphics.widgets;

import java.awt.Color;

import util.JComponentUtil;
import util.Util.ROBOT_KEY;

public class SpeedDisplayWidget extends Widget {
	//Attributes
	private SpeedDisplay sd; //speed display to control
	private String title; //title of the widget
	
	/**
	 * Create a SpeedDisplayWidget using a pre-existing component
	 * @param sd SpeedDisplay JComponent
	 * @param key value to get from data map and update with
	 */
	public SpeedDisplayWidget(SpeedDisplay sd, ROBOT_KEY key) {
		//create widget
		super(JComponentUtil.panelFromComponent(sd));
		
		//set attributes
		this.sd = sd;
		
		//set the keys to be used when updating
		this.keyArray = new ROBOT_KEY[] {key}; 
	} 
	
	/**
	 * Create a SpeedDisplayWidget
	 * @param width width of widget in pixels
	 * @param height height of widget in pixels
	 * @param maxVal maximum value to scale to
	 * @param key value to get from data map and update with
	 */
	public SpeedDisplayWidget(int width, int height, double maxVal, ROBOT_KEY key) {
		this(new SpeedDisplay(width, height, maxVal), key);
	} 
	
	/**
	 * Update the component
	 * @param values array of values used to update
	 */
	public void update(double[] values) {
		sd.update(values[0]);
	} 
	
	/**
	 * Set the color of the bar
	 * @param color color for the bar
	 */
	public void setColor(Color color) {
		sd.setColor(color);
	} 
	
	/**
	 * Set two colors for the display
	 * @param fwdClr Color if speed is positive
	 * @param revClr Color if speed is negative
	 */
	public void setColors(Color fwdClr, Color revClr) {
		sd.setColors(fwdClr, revClr);
	} 
}