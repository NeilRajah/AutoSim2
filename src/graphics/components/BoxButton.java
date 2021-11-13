package graphics.components;
/*
 * BoxButton
 * Author: Neil Balaskandarajah
 * Created on: 21/03/2020
 * A custom and more aesthetically pleasing button/text box
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import graphics.Painter;
import main.AutoSim;
import util.JComponentUtil;
import util.Util;

public class BoxButton extends JComponent {
	//Attributes
	private String text; //text for the box
	
	private Color defColor; //default color
	private Color hoverColor; //hovered over color
	private Color pressColor; //locked color
	private Color paintColor; //color to be painted with
	private Color textColor; //text color
	
	private Font f; //Font for text
	private float fontSize; //font size in pixels
	private int cornerRad; //radius of the corners in pixels
	private boolean border; //whether OBOX has border
	private BUTTON_STATE state; //state the button is in
	
	//Possible actions
	public static enum BUTTON_STATE {
		DEFAULT,
		HOVER,
		LOCK	
	} //end enum
	
	/*
	 * Create an OBox with a width, height and text inside
	 * int width - width of the box
	 * int height - height of the box
	 * String text - text in the box
	 */
	public BoxButton(int width, int height, String text) {
		super();
		
		//set attributes
		this.text = text;
		this.fontSize = Util.FONT_SIZE;	
		
		//set the corner radius and default colors colors
		this.cornerRad = AutoSim.PPI * 10;		
		this.defColor = Color.LIGHT_GRAY;
		this.hoverColor = Color.GRAY;
		this.pressColor = Color.BLACK;
		this.paintColor = defColor;
		this.textColor = Color.WHITE;
		
		//set the size of the box
		this.setPreferredSize(new Dimension(width, height));
		
		//regular Oxygen font
		this.f = Util.getFileFont(Painter.SF_UI_FONT);
		
		//add padding around component
		this.setBorder(JComponentUtil.paddedBorder(AutoSim.PPI * 2));
		
		//set the default state
		setState(BUTTON_STATE.DEFAULT);
	} //end constructor
	
	/*
	 * Create an OBox with a width, height and text inside
	 * int width - width of the box
	 * int height - height of the box
	 * String text - text in the box
	 * boolean straight - whether the box has straight corners or not
	 * boolean border - whether the box has a border or not
	 */
	public BoxButton(int width, int height, String text, boolean straight, boolean border) {
		this(width, height, text);
		
		cornerRad = straight ? 0 : 60;
	} //end constructor
	
	/*
	 * Draw the box to the screen
	 * Graphics g - object responsible for drawing 
	 */
	public void paintComponent(Graphics g) {
		//draw border
		if (border) {
			g.setColor(paintColor.darker());
			g.fillRoundRect(AutoSim.PPI, 0, this.getWidth(), this.getHeight(), cornerRad, cornerRad);
		} //if
		
		//draw the inside
		g.setColor(paintColor);
		g.fillRoundRect(AutoSim.PPI, 0, this.getWidth(), this.getHeight(), cornerRad, cornerRad);
		
		//set the text color
		g.setColor(textColor);

		//set the font
		g.setFont(f.deriveFont(fontSize));
		FontMetrics fm = g.getFontMetrics();
		
		//set the font to use antialiasing or not
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		//draw the text centered on the button
		g.drawString(text, (this.getWidth() / 2) - (fm.stringWidth(text) / 2), 
							(this.getHeight() / 2) + (fm.getAscent() / 2) - (int) (Util.FONT_SIZE * 0.12));
	} //end paintComponent
	
	/**
	 * Set the colors to be used by the button
	 * @param light Light color to paint background regularly
	 * @param dark Dark color to paint background on hover
	 */
	public void setColors(Color light, Color dark) {
		defColor = light;
		hoverColor = dark;
		pressColor = dark.darker();
		paintColor = defColor;
		repaint();
	} //end setColors
	
	/**
	 * Set the state of the button and change the color
	 * @param state New button state
	 */
	public void setState(BUTTON_STATE state) {
		this.state = state;
		
		//change color of the button
		switch (this.state) {
			case DEFAULT:
				paintColor = defColor;
				break;
			
			case HOVER:
				paintColor = hoverColor;
				break;
			
			case LOCK:
				paintColor = pressColor;
				break;
		} //switch
		
		repaint();
	} //end setState

	/**
	 * Check if the button is locked 
	 * @return locked Whether the button is locked to user input
	 */
	public boolean isLocked() {
		return state.equals(BUTTON_STATE.LOCK);
	} //end isLocked
	
	/**
	 * Toggle the button lock state
	 */
	public void toggleLock() {
		if (state.equals(BUTTON_STATE.LOCK))
			setState(BUTTON_STATE.HOVER);
		else
			setState(BUTTON_STATE.LOCK);
	} //end toggleLock
	
	/**
	 * Get the state of the button
	 * @return Button state
	 */
	public BUTTON_STATE getState() {
		return state;
	} //end getState
	
	/**
	 * Get the box text
	 * @return text Text within the box
	 */
	public String getText() {
		return text;
	} //end getText
	
	/*
	 * Set the text to the box
	 * String text - text to set the box to
	 */
	public void setText(String text) {
		this.text = text;
	} //end setText
	
	/*
	 * Set the font size of the box
	 * float fontSize - new font size
	 */
	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	} //end setFontSize
	
	/*
	 * Set the corner radius of the box
	 * int cornerRad - corner radius
	 */
	public void setCornerRadius(int cornerRad) {
		this.cornerRad = cornerRad;
	} //end setCornerRadius

	/*
	 * Set the text color of the box
	 * Color color - new text color
	 */
	public void setTextColor(Color color) {
		this.textColor = color;
	} //end setTextColor
} //end class