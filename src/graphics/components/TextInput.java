/**
 * TextInput
 * Author: Neil Balaskandarajah
 * Created on: 07/28/2020
 * Widget for getting user text input
 */
package graphics.components;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;

import graphics.Painter;
import main.AutoSim;
import util.JComponentUtil;
import util.TextFieldFilter;
import util.Util;

public class TextInput extends JPanel {
	//Attributes
	private int height; //height in pixels
	private int width; //width in pixels
	private String title; //title of the box
	private JLabel titleComp; //component for the title
	private JTextField textComp; //component for the text
	private String validKeys; //keys allowed to be in the field
	private static final String ALL_VALID = ""; //identifier that all keys are allowed
	
	/**
	 * Create a Text Input component
	 * @param title Title of the box
	 * @param height Height in pixels
	 * @param width Width in pixels
	 * @param validKeys Keys allowed in the text box
	 */
	public TextInput(String title, int height, int width, String validKeys) {
		super();
		
		this.height = height;
		this.width = width;
		this.title = title;
		this.validKeys = validKeys;
		
		layoutView();
	}
	
	/**
	 * Create a Text Input component with all keys available
	 * @param title Title of the input
	 * @param height Height in pixels
	 * @param width Width in pixels
	 */
	public TextInput(String title, int height, int width) {
		this(title, height, width, ALL_VALID);
	}
	
	/**
	 * Create the layout and all the components
	 */
	private void layoutView() {
		//Layout
		BoxLayout layout = new BoxLayout(this, BoxLayout.X_AXIS);
		this.setLayout(layout);
		int h = this.height / 15;
		
		//Title label
		titleComp = new JLabel(title, SwingConstants.CENTER);
		titleComp.setFont(Painter.createFont(Painter.SF_UI_FONT, AutoSim.PPI * 10));
		titleComp.setPreferredSize(new Dimension(width / 6, h));
		this.add(titleComp);

		//Textbox
		textComp = JComponentUtil.textField("0.0", (int) (this.width * 0.7), h, AutoSim.PPI * 3);
		if (!validKeys.equals(ALL_VALID)) {
			AbstractDocument d = (AbstractDocument) textComp.getDocument();
			d.setDocumentFilter(new TextFieldFilter(validKeys));
		}
		this.add(textComp);
	}
	
	/**
	 * Return the text typed by the user
	 * @return Text from the text field
	 */
	public String getText() {
		return textComp.getText();
	}
}
