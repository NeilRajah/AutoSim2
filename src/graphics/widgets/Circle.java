/**
 * Circle
 * Author: Neil Balaskandarajah
 * Created on: 30/03/2020
 * Circle that acts as a point with additional UI features
 */
package graphics.widgets;

import java.awt.Color;

import graphics.Environment;
import graphics.components.BoxButton.BUTTON_STATE;
import model.Point;

public class Circle extends Point {
	//Attributes
	private Color color; //color of the circle
	private Color hoverColor; //color of the circle when hovered over
	private Color lockedColor; //color of the circle when locked
	private boolean hovered; //whether the circle is being hovered over or not
	private boolean locked; //whether the circle is locked to user input
	
	/**
	 * Create a circle with (x,y) coordinates
	 * @param x X value of the circle
	 * @param y Y value of the circle
	 * @param color Color of the circle
	 */
	public Circle(double x, double y, Color color) {
		super(x, y);
		
		this.color = color;
		this.hoverColor = this.color.darker();
		this.lockedColor = hoverColor.darker();
	} 
	
	/**
	 * Create a circle with a point
	 * @param p Point containing the coordinates of the circle
	 * @param color Color of the circle
	 */
	public Circle(Point p, Color color) {
		super(p.getX(), p.getY());
		
		this.color = color;
		this.hoverColor = this.color.darker();
		this.lockedColor = this.color.darker();
	} 
	
	/**
	 * Get the color of the circle
	 * @return Circle's color based on the state of the button
	 */
	public Color getColor() {
		if (locked) 
			return lockedColor;
		else if (hovered)
			return hoverColor;
		return color;
	} 
	
	/**
	 * Set the circle to be hovered over and update the Environment
	 */
	public void setHovered() {
		this.hovered = true;
		this.locked = false;
		Environment.getInstance().update();
	} 
	
	/**
	 * Set the circle to be locked and update the Environment
	 */
	public void setLocked() {
		this.locked = true;
		this.hovered = false;
		Environment.getInstance().update();
	}
	
	/**
	 * Get whether or not the circle is locked
	 * @return Locked state of the button
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Set the circle to its default state
	 */
	public void setDefault() {
		this.locked = false;
		this.hovered = false;
		Environment.getInstance().update();
	} 
	
	/**
	 * Get the state of the Circle
	 * @return whether Circle is default, hovered or locked
	 */
	public BUTTON_STATE getState() {
		if (hovered) 
			return BUTTON_STATE.HOVER;
		
		else if (locked) 
			return BUTTON_STATE.LOCK;
		
		return BUTTON_STATE.DEFAULT;
	} 
	
	/**
	 * Set the state of the circle and update the Environment
	 * @param state State of the circle
	 */
	public void setState(BUTTON_STATE state) {
		switch (state) {
			case DEFAULT:
				hovered = false;
				locked = false;
				break;
			
			case HOVER:
				hovered = true;
				locked = false;
				break;
				
			case LOCK:
				hovered = false;
				locked = true;
				break;
		}
		
		Environment.getInstance().update();
	} 
}