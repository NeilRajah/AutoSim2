/**
 * CircleButtonController
 * Author: Neil Balaskandarajah
 * Created on: 31/03/2020
 * Controller for changing the control circle state based on user mouse actions
 */

package graphics.widgets;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.BiConsumer;

import graphics.components.BoxButton;
import graphics.components.BoxButton.BUTTON_STATE;

public class CircleButtonController implements MouseListener {
	//Attributes
	private BoxButton button; //button being controlled
	private int key; //index of the control circle being updated
	private BiConsumer<Integer, BoxButton.BUTTON_STATE> updateCircle; //method that updates circle
	
	/**
	 * Create a circle controller to update a button
	 * @param button Point button
	 * @param method Method to run that updates the circle
	 */
	public CircleButtonController(BoxButton button, BiConsumer<Integer, BoxButton.BUTTON_STATE> method) {
		//set attributes
		this.button = button;
		this.updateCircle = method;
		
		//get key from button text
		this.key = Integer.parseInt(button.getText().substring(1)); //P0 -> 0, P1 -> 1, ...
	} //end constructor

	/**
	 * Update circle state to button state
	 */
	public void mouseEntered(MouseEvent m) {
		updateCircle.accept(key, BUTTON_STATE.HOVER);
	} //end mouseEntered

	/**
	 * Update circle state to button state
	 */
	public void mouseExited(MouseEvent m) {
		updateCircle.accept(key, BUTTON_STATE.DEFAULT);
	} //end mouseExited

	/**
	 * Update circle state to button state
	 */
	public void mousePressed(MouseEvent m) {
		updateCircle.accept(key, BUTTON_STATE.LOCK);
	} //end mousePressed

	/**
	 * Update circle state to button state
	 */
	public void mouseReleased(MouseEvent m) {
		updateCircle.accept(key, BUTTON_STATE.HOVER);
	} //end mouseReleased
	
	/*
	 * Unimplemented
	 */
	public void mouseClicked(MouseEvent m) {} 
} //end class
