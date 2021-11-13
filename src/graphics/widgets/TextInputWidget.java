/**
 * TextInputWidget
 * Author: Neil Balaskandarajah
 * Created on: 28/07/2020
 * Text input widget
 */

package graphics.widgets;

import graphics.components.TextInput;

public class TextInputWidget extends Widget {
	//Attributes
	private TextInput textInput;
	
	/**
	 * Create a text input widget
	 * @param textInput Text input with label and field
	 */
	public TextInputWidget(TextInput textInput) {
		super(textInput);
		this.textInput = textInput;
	}
	
	/**
	 * Update the widget
	 */
	public void update(double[] values) {}
	
	/**
	 * Get the text from the field
	 * @return Text from the text field
	 */
	public String getText() {
		return textInput.getText();
	}
}
