/**
 * UIBar
 * Author: Neil Balaskandarajah
 * Created on: 02/01/2020
 * Bar on bottom of window to help user interact with program 
 */
package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import main.AutoSim;
import util.Util;

public class UIBar extends JComponent {
	//Attributes
	//Configured
	private int width; //component width
	private int height; //component height
	
	//Updated
	private String cursorLoc; //x,y location of cursor in inches
	private String time; //time from start in seconds
	private String currentCmd; //name of current command
	
	//Constants
	private final int TEXT_HEIGHT; //height of text
	
	/**
	 * Create a bar for user interaction
	 * @param width - width of the component
	 * @param height - height of the component
	 */
	public UIBar(int width, int height) {
		super();
		
		//set attributes
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));
		
		//update constants=
		cursorLoc = "(x,y): 0 0";
		time = "0.0000";
		currentCmd = "";
		TEXT_HEIGHT = height / 10;
		
		//draw default information
		repaint();
	} //end constructor

	//Graphics

	/**
	 * Return the width of the environment
	 * @return width - width of component in pixels
	 */
	public int width() {
		return width;
	} //end width

	/**
	 * Return the height of the environment
	 * @return height - height of component in pixels
	 */
	public int height() {
		return height;		
	} //end height
	
	/**
	 * Update the environment
	 */
	public void update() {	
		repaint();
	} //end update
	
	/**
	 * Update the time since the animation started
	 * @param index - number of frames elapsed in animation
	 */
	public void setTime(int index) {
		//each frame is one update period long
		time = String.format("Time: %.3f", index * Util.UPDATE_PERIOD); 	
		repaint();
	} //end setTime
	
	/**
	 * Update the cursor location
	 * @param x - x position of cursor
	 * @param y - y position of cursor
	 */
	public void setCursorLocation(int x, int y) {
		//x and y values in inches
		double xVal = (double) y / AutoSim.PPI;
		double yVal = (double) x / AutoSim.PPI;
		
		cursorLoc = String.format("(x,y): %.1f %.1f", xVal, yVal);
		repaint();
	} //end setCursorLocation
	
	/**
	 * Set the name of the command to output to the screen
	 * @param name name of the current command running
	 */
	public void setCommandName(String name) {
		this.currentCmd = name;
		repaint();
	} //end setCommandName
	
	/**
	 * Draw the information to the bar
	 * @param g - used for drawing
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; //g2 for better drawing
		
		//set font and make text smoother
		g2.setFont(Painter.createFont(Painter.SF_UI_FONT, AutoSim.PPI * 10));
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		//move origin to bottom left
		g2.scale(1.0, -1.0);
		g2.translate(0, -height);
		
		//set the background
		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, height);
		
		//draw text
		g2.setColor(Color.black);
		Painter.drawFlippedString(g2, cursorLoc, 0, TEXT_HEIGHT); //cursor location
		Painter.drawFlippedString(g2, time + "s", (int) (width * 0.7), (int) (TEXT_HEIGHT * 0.8)); //simulation time
		Painter.drawFlippedString(g2, currentCmd, (int) (width * 0.32), TEXT_HEIGHT); //name of command being run
	} //end paintComponent
} //end class