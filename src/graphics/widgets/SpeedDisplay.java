/*
 * SpeedDisplay
 * Created by: Neil Balaskandarajah
 * Created on: 03/02/2020
 * A simple display of an object's speed
 */
package graphics.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

import util.Util;

public class SpeedDisplay extends JComponent {
	//Attributes
	private double speed; //speed of object
	private double maxSpeed; //speed cap
	private Color color; //color of the slider
	private Color fwdClr; //color if forward
	private Color revClr; //color if reverse
	
	/**
	 * Create a new speed display with a width, height and max speed
	 * @param width Width of the component in pixels
	 * @param height Height of the component in pixels
	 * @param maxSpeed Max speed of the object in units
	 */
	public SpeedDisplay(int width, int height, double maxSpeed) {
		this.setPreferredSize(new Dimension(width, height));
		
		this.maxSpeed = maxSpeed;
		
		this.color = Color.GREEN;
	}
	
	/**
	 * Set the speed of the object and redraw the component
	 * @param newSpeed New speed of the object
	 */
	public void update(double newSpeed) {
		speed = Math.abs(newSpeed);
		this.color = newSpeed > 0 ? fwdClr : revClr;
		repaint();
	}
	
	/**
	 * Set the color of the display
	 * @param clr Color to set the velocity slider of the display
	 */
	public void setColor(Color clr) {
		this.color = clr;
	}
	
	/**
	 * Set two colors for the display
	 * @param fwdClr Color if speed is positive
	 * @param revClr Color if speed is negative
	 */
	public void setColors(Color fwdClr, Color revClr) {
		this.fwdClr = fwdClr;
		this.revClr = revClr;
	}
	
	/**
	 * Visually represent the speed of the object
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		int drawHeight = this.getHeight() - (int) ((speed/maxSpeed) * this.getHeight());
		
		//background
		g2.setBackground(color);
		g2.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		//clear
		g2.setColor(Color.GRAY);
		g2.fillRect(0, 0, this.getWidth(), drawHeight);
	} 
}