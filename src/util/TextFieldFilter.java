/**
 * TextFieldFilter
 * Author: Neil Balaskandarajah
 * Created on: 07/28/2020
 * Filter for a text field
 */

package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class TextFieldFilter extends DocumentFilter {
	//Attributes
	private String validCharacters; //characters allowed to be typed
	
	/**
	 * Create a filter to be used in text fields
	 * @param valid String of characters to add
	 */
	public TextFieldFilter(String valid) {
		super();
		this.validCharacters = valid;
	}
	
	/**
	 * Insert a String into the field
	 * @param fb Contains document
	 * @param offs Start position offset
	 * @param str String to add
	 * @param a Set of attributes
	 */
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		if (str == null) return;
		
		//if valid key or does not already have a period
		if (validCharacters.contains(str)) {
//			if (str.equals(".") && !fb.getDocument().getText(0, fb.getDocument().getLength()).contains("."))
				super.insertString(fb, offs, str, a);
		}
	}
	
	/**
	 * Replace a String in the field
	 * @param fb Contains document
	 * @param offs Start position offset
	 * @param length Length to check
	 * @param str String to add
	 * @param a Set of attributes
	 */
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		if (str == null) return;
		
		//if valid key or does not already have a period
		if (validCharacters.contains(str)) {
//			if (str.equals(".") && !fb.getDocument().getText(0, fb.getDocument().getLength()).contains("."))
				super.replace(fb, offs, length, str, a);
		}
	}
}
