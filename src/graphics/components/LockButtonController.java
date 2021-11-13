/**
 * LockButtonController
 * Author: Neil Balaskandarajah
 * Created on: 01/04/2020
 * Versatile controller for a button that locks
 */

package graphics.components;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

import graphics.components.BoxButton.BUTTON_STATE;

public class LockButtonController implements MouseListener {
	//Attributes
	private BoxButton button; //button to control
	private Consumer<Integer> toggle; //lock toggle method
	private int key; //index of this button in buttons array
	
	/**
	 * Create a controller for the button
	 * @param button Button to control
	 */
	public LockButtonController(BoxButton button, Consumer<Integer> toggle) {
		//set attributes
		this.button = button;
		this.toggle = toggle;
		
		this.key = Integer.parseInt(button.getText().substring(1)); //P0 -> 0, P1 -> 1, ...
	} //end constructor
	
	/**
	 * Create a controller for the button
	 * @param button Button to control
	 */
	public LockButtonController(BoxButton button) {
		//set attributes
		this.button = button;
	} //end constructor
	
	/**
	 * Change the color to dark when the mouse enters if not locked
	 */
	public void mouseEntered(MouseEvent m) {
		if (!button.isLocked())
			button.setState(BUTTON_STATE.HOVER);
		
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
	} //end mouseEntered

	/**
	 * Change the color to light when the mouse leaves if not locked
	 */
	public void mouseExited(MouseEvent m) {
		if (!button.isLocked())
			button.setState(BUTTON_STATE.DEFAULT);

		button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	} //end mouseExited

	/**
	 * Change the color to a darker dark when the mouse is pressed
	 */
	public void mousePressed(MouseEvent m) {
		if (toggle != null)
			toggle.accept(key);
		else
			button.toggleLock();
	} //end mousePressed

	/**
	 * Change the color back to light when the mouse is released if not locked
	 */
	public void mouseReleased(MouseEvent m) {
		if (!button.isLocked())
			button.setState(BUTTON_STATE.HOVER);		
	} //end mouseReleased
	
	/*
	 * Unimplemented
	 */
	public void mouseClicked(MouseEvent m) {}
} //end class
