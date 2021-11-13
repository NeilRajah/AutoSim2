/**
 * PathKeyController
 * Author: Neil Balaskandarajah
 * Created on: 28/03/2020
 * Controller for the Environment to update a BezierPathCreator widget
 */

package graphics.widgets;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.BiConsumer;

import graphics.Environment;

public class PathKeyController implements KeyListener, MouseListener {	
	//Constants
	private final double DELTA = 3.0; //change in inches
	private final double FINE_SCALE = 0.1; //fine control scalar
	
	//Attributes
	private double dx = 0; //change in x in inches
	private double dy = 0; //change in y in inches
	private boolean fineControl; //whether fine control is enabled
	private BezierPathCreator bpc; //component containing path
	
	/**
	 * Create a path controller
	 * @param bpc BezierPathCreator component to update
	 */
	public PathKeyController(BezierPathCreator bpc) {
		//set attributes
		this.bpc = bpc;
		this.fineControl = false;
	} //end constructor
	
	/**
	 * Move the locked circle in the direction of the key press
	 */
	public void keyPressed(KeyEvent k) {		
		//left and right arrows
		if (k.getKeyCode() == KeyEvent.VK_LEFT) {
			dy = -DELTA;
			
		} else if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
			dy = DELTA;
		} //if
			
		//up and down arrows
		if (k.getKeyCode() == KeyEvent.VK_UP) {
			dx = -DELTA;
			
		} else if (k.getKeyCode() == KeyEvent.VK_DOWN) {
			dx = DELTA;
		} //if
		
		//turn fine control on
		if (k.getKeyCode() == KeyEvent.VK_SHIFT) {
			fineControl = true;
		} //if
		
		//scale down values if using fine control
		if (fineControl) {
			dx *= FINE_SCALE;
			dy *= FINE_SCALE;
		} //if
		
		bpc.moveCircle(dx, dy);
	} //end keyPressed

	/**
	 * Reset the deltas
	 */
	public void keyReleased(KeyEvent k) {
		//reset left and right keys
		if (k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_RIGHT) {
			dy = 0;
		} //if
			
		//reset up and down keys
		if (k.getKeyCode() == KeyEvent.VK_UP || k.getKeyCode() == KeyEvent.VK_DOWN) {
			dx = 0;
		} //if
		
		//turn fine control off
		if (k.getKeyCode() == KeyEvent.VK_SHIFT) {
			fineControl = false;
		} //if
	} //end keyReleased

	/**
	 * Set the focus of the Environment when this is clicked
	 */
	public void mousePressed(MouseEvent m) {
		Environment.getInstance().setFocused(true);
	} //end mouseClicked
	
	/*
	 * Unimplemented
	 */
	public void keyTyped(KeyEvent k) {}

	/*
	 * Unimplemented
	 */
	public void mouseEntered(MouseEvent m) {}

	/*
	 * Unimplemented
	 */
	public void mouseExited(MouseEvent m) {}

	/*
	 * Unimplemented
	 */
	public void mouseClicked(MouseEvent m) {}

	/*
	 * Unimplemented
	 */
	public void mouseReleased(MouseEvent m) {}
} //end class
