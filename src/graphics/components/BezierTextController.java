/**
 * TextBoxController
 * Author: Neil Balaskandarajah
 * Created on: 21/03/2020
 * Controller for a text box
 */

package graphics.components;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.stream.IntStream;

import javax.swing.text.JTextComponent;

import graphics.widgets.BezierPathCreator;
import util.Util;

public class BezierTextController implements FocusListener {
	//Attributes
	private JTextComponent textArea; //component being controlled
	private BezierPathCreator bpc; //main widget
	private String key; //key from the textArea
	
	/**
	 * Create a controller for a text field
	 * @param textArea JTextField to be controlled
	 */
	public BezierTextController(JTextComponent textArea, BezierPathCreator bpc) {
		//set attributes
		this.textArea = textArea;
		this.bpc = bpc;
		this.key = this.textArea.getText();
	} 

	/**
	 * Change the color of the text in the box to the primary color when focused
	 * @param f Event created when component gains focus
	 */
	public void focusGained(FocusEvent f) {
		if (textArea.getText().equals(key)) {
			textArea.setText("");
		}

		textArea.setForeground(Color.BLACK);
	}

	/**
	 * Set the ghost text if nothing has been typed
	 */
	public void focusLost(FocusEvent f) {
		if (textArea.getText().equals("")) {
			textArea.setForeground(Color.LIGHT_GRAY);
			textArea.setText(key);
		}
	}
}