/**
 * EnvironmentUIController
 * Author: Neil Balaskandarajah
 * Created on: 02/01/2020
 * Controller for the Environment to update the UI bar
 */
package graphics;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class EnvironmentUIController implements MouseMotionListener, FocusListener {
	//Attributes
	private Environment env; //Environment instance to update
	
	/*
	 * Add points for bezier
	 * pressing on point locks it, no other can update (don't check which one mouse is over until release)
	 * BezierPathCreator (BPC) button locks point, click updates point buttons to show which is locked
	 * Update BPC when moving points to change text box values (0.1, 0.01 tolerance)
	 * calls repaint() everytime it updates, draw path with only 100 points (faster)
	 */
	
	/**
	 * Create an environment controller with an environment
	 * Environment env - environment to get information from 
	 */
	public EnvironmentUIController(Environment env) {
		super();
		
		//set attributes
		this.env = env;
	} //end constructor

	/**
	 * Send the coordinates of the mouse to the UI bar
	 */
	public void mouseMoved(MouseEvent m) {
		env.setBarCursorLocation(m.getX(), m.getY());
	} //end mouseMoved

	/**
	 * Set focused to true for the Environment
	 */
	public void focusGained(FocusEvent f) {
		env.setFocused(true);	
	} //end focusGained

	/**
	 * Set focused to false for the Environment
	 */
	public void focusLost(FocusEvent f) {
		env.setFocused(false);
	} //end focusLost
	
	/*
	 * Unimplemented
	 */
	public void mouseDragged(MouseEvent m) {}
} //end class