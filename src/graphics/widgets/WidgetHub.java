/*
 * WidgetHub
 * Author: Neil Balaskandarajah
 * Created on: 01/03/2020
 * Hub for all the widgets 
 */

package graphics.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import graphics.Painter;
import main.AutoSim;
import util.Util;
import util.Util.ROBOT_KEY;
import util.Util.WIDGET_ID;

public class WidgetHub extends JPanel {
	//Attributes
	private int height; //height of the hub
	private int width; //width of the hub
	private ArrayList<Widget> widgets; //widgets and their IDs
	private Font font; //font for the text
	
	/**
	 * Create a WidgetHub
	 * @param width Width in pixels
	 * @param height Height in pixels
	 */
	public WidgetHub(int width, int height) {
		//set attributes
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width, height));
		this.widgets = new ArrayList<Widget>();
		
		//layout the view
		layoutView();
	} //end constructor

	/**
	 * Layout the view of the widget hub
	 */
	private void layoutView() {
		//set the layout manager of the hub
		this.setAlignmentX(CENTER_ALIGNMENT);
		
		//add title bar
		JLabel title = new JLabel("Widget Hub");
		title.setForeground(Color.BLACK);
		title.setFont(Painter.createFont(Painter.SF_UI_FONT, AutoSim.PPI * 15));
		title.setAlignmentX(CENTER_ALIGNMENT);
		this.add(title);
		
		//add border to hub
		this.setBorder(BorderFactory.createEmptyBorder(AutoSim.PPI, AutoSim.PPI, AutoSim.PPI, AutoSim.PPI));
	} //end layoutView
	
	/**
	 * Add a widget to the hub and to the map
	 * @param w Widget to add to the hub
	 */
	public void addWidget(Widget w) {
		//add the component
		JPanel component = w.getComponent();
		component.setAlignmentX(CENTER_ALIGNMENT);
		this.add(w.getComponent());
		
		//add the widget to the list
		widgets.add(w);
	} //end addWidget
	
	/**
	 * Return the width of the widget hub
	 * @return width - width of component in pixels
	 */
	public int width() {
		return width;
	} //end width

	/**
	 * Return the height of the widget hub
	 * @return height - height of component in pixels
	 */
	public int height() {
		return height;		
	} //end height
	
	/**
	 * Update the widget
	 * @param data all data points from the robot
	 */
	public void update(HashMap<ROBOT_KEY, Object> data) {	
		//loop through each widget and update it
		for (int i = 0; i < widgets.size(); i++) {
			if (widgets.get(i).getKeyArray() != null) {
				ROBOT_KEY[] keys = widgets.get(i).getKeyArray(); //array of keys
				
				//fill the values using the widget's keys
				double[] values = new double[keys.length];
				
				for (int k = 0; k < keys.length; k++)
					values[k] = (double) data.get(keys[k]); 
				
				//send those values to the widget
				widgets.get(i).update(values);
			} //if
		} //loop
	} //end update

	/**
	 * Update a widget with given values
	 * @param widgetID identifier of the tag for the information to be given to
	 * @param values numerical values to be fed to the widget
	 */
	public void updateWidget(WIDGET_ID widgetID, double[] values) {
		
	} //end updateWidget
} //end class
