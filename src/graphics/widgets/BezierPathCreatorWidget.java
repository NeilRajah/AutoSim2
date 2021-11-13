/**
 * BezierPathCreatorWidget
 * Author: Neil Balaskandarajah
 * Created on: 17/03/2020
 * Widget for bezier path creation
 */

package graphics.widgets;

import graphics.Environment;
import graphics.GraphicBezierPath;
import model.Point;
import util.Util;

public class BezierPathCreatorWidget extends Widget {
	//Attributes
	private BezierPathCreator bpc; //path creator JComponent
	private boolean hideMode; //whether to hide the widget on the Environment when running or not
	
	/**
	 * Create a widget containing a BezierPathCreator
	 * @param panel Panel containing the BezierPathCreator
	 */
	public BezierPathCreatorWidget(BezierPathCreator bpc) {
		super(bpc);
		
		//set attributes
		this.bpc = bpc;
		this.hideMode = false;
		
		//add the controllers
		registerControllers();
	} 

	/**
	 * Add the key and mouse controllers
	 */
	public void registerControllers() {
		//key controller
		PathKeyController pkc = new PathKeyController(bpc);
		Environment.getInstance().addKeyListener(pkc);
		Environment.getInstance().addMouseListener(pkc);
		
		//mouse controller
		PathMouseController pmc = new PathMouseController(bpc);
		Environment.getInstance().addMouseListener(pmc);
		Environment.getInstance().addMouseMotionListener(pmc);
	} 
	
	/**
	 * Set the control points of the path 
	 * @param points Control points of path 
	 */
	public void setControlPoints(Point[] points) {
		bpc.setCircles(GraphicBezierPath.circlesFromPoints(points));
	} 
	
	/**
	 * Set the control points of the path 
	 * @param points Control points of path 
	 */
	public void setControlPoints(double[][] points) {
		bpc.setCircles(GraphicBezierPath.circlesFromCoordinates(points));
	}
	
	/**
	 * Set the control points of the path
	 * @param filename File containing control points
	 */
	public void setControlPoints(String filename) {
		bpc.setCircles(GraphicBezierPath.circlesFromFile(filename));
	} 
	
	/**
	 * Update the widget given update values
	 * @param values Values given from the update
	 */
	public void update(double[] values) {
		Util.println("I do exist!");
		Environment.getInstance().setDrawCurve(!hideMode);
	}
	
	/**
	 * Hide the widget when the animation is running
	 */
	public void hideWhenRunning() {
		hideMode = true;
	}
} 
