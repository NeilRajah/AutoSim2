/**
 * GraphicBezierPath
 * Author: Neil Balaskandarajah
 * Created on: 31/03/2020
 * Bezier path for graphical features
 */

package graphics;

import java.io.BufferedReader;
import java.io.FileReader;

import graphics.components.BoxButton.BUTTON_STATE;
import graphics.widgets.Circle;
import main.AutoSim;
import model.FieldPositioning;
import model.Point;
import model.motion.BezierPath;
import util.FieldPoints;

public class GraphicBezierPath extends BezierPath {
	//Attributes
	private Circle[] circles; //control points
	private int[][] poly;
	private int[][] leftPoly;
	private int[][] rightPoly;
	private double[] headings;
	private int numSegments;
	private double trackWidth;
	
	/**
	 * Create a bezier path with circles
	 * @param circles Control points as circles
	 */
	public GraphicBezierPath(Circle[] circles) {
		super(circles);
		
		this.numSegments = BezierPath.FAST_RES;
		this.trackWidth = Painter.ROBOT_WIDTH / 2;
		this.circles = circles;
	} //end constructor
	
	/**
	 * Create a blank path with fast resolution
	 */
	public GraphicBezierPath() {
		this(circlesFromPoints(FieldPositioning.pointsFromDoubles(FieldPoints.empty)));
	} //end constructor
	
	/**
	 * Create an array of Circles from an array of Points
	 * @param points Points to make the circles from
	 * @return Array of Circles with a default color
	 */
	public static Circle[] circlesFromPoints(Point[] points) {
		Circle[] circles = new Circle[6];
		
		for (int i = 0; i < 6; i++) {
			circles[i] = new Circle(points[i], Painter.BEZ_BTN_DARK);
		} //loop
		
		return circles;
	} //end circlesFromPoints
	
	/**
	 * Create an array of Circles from an array of Points
	 * @param coords (x,y) values to make the circles from
	 * @return Array of Circles with a default color
	 */
	public static Circle[] circlesFromCoordinates(double[][] coords) {
		Circle[] circles = new Circle[6];
		
		for (int i = 0; i < 6; i++) {
			circles[i] = new Circle(coords[i][0], coords[i][1], Painter.BEZ_BTN_DARK);
		} //loop
		
		return circles;
	} //end circlesFromCoordinates
	
	/**
	 * Create control points from a file
	 * @param filename Name of file containing circle (x,y) values
	 * @return Array of Circles
	 */
	public static Circle[] circlesFromFile(String filename) {
		try {
			//open the file and create the array of Circles
			BufferedReader in = new BufferedReader(new FileReader(filename));
			Circle[] circles = new Circle[6];
			
			for (int i = 0; i < 6; i++) {
				String[] xy = in.readLine().split(" ");
				double x = Double.parseDouble(xy[0]);
				double y = Double.parseDouble(xy[1]);
				circles[i] = new Circle(x, y, Painter.BEZ_BTN_DARK);
			}
			
			in.close();
			return circles;
			
		} catch (Exception e) {
			//message window
			e.printStackTrace();
			return circlesFromCoordinates(FieldPoints.curve2);
		} //try-catch
	} //end circlesFromFile

	/**
	 * Return the path's circles
	 * @return Circles representing the (x,y) points
	 */
	public Circle[] getCircles() {
		return circles;
	} //end getCircles
	
	/**
	 * Set the circles for the curve
	 * @param circles Circle control points of the curve
	 */
	public void setCircles(Circle[] circles) {
		this.circles = circles;
		this.setControlPoints(circles);
		updatePolylines();
	} //end setCircles
	
	/**
	 * Set a circle in the array to be hovered over
	 * @param i Index of the circle
	 */
	public void setCircleHover(int i) {
		circles[i].setHovered();
	} //end setCircleHover
	
	/**
	 * Set a circle in the array to be locked
	 * @param i Index of the circle
	 */
	public void lockCircle(int i) {
		circles[i].setLocked();
	} //end lockCircle
	
	/**
	 * Set a circle in the array to be returned to default
	 * @param i Index of the circle
	 */
	public void setCircleDefault(int i) {
		circles[i].setDefault();
	} //end setCircleDefault
	
	/**
	 * Check if a circle is locke in the curve
	 * @return Whether a control circle is locked or not
	 */
	public boolean circleIsLocked() {
		for (int i = 0; i < circles.length; i++) {
			if (circles[i].isLocked()) {
				return true;
			} //if
		} //loop
		
		return false;
	} //end circleIsLocked
	
	/**
	 * Unlock all circles in the curve
	 */
	public void unlockAllCircles() {
		for (int i = 0; i < circles.length; i++) {
			circles[i].setDefault();
		} //loop
	} //end unlockAllCircles
	
	/**
	 * Translate a circle in the x and y directions
	 * @param i Index of the circle in the array
	 * @param dx Change in x position
	 * @param dy Change in y position
	 */
	public void moveCircle(int i, double dx, double dy) {
		circles[i].setX(circles[i].getX() + dx);
		circles[i].setY(circles[i].getY() + dy);
	} //end moveCircle

	/**
	 * Get the index of the locked circle
	 * @return index of locked circle, -1 if none are locked
	 */
	private int getLockedCircleIndex() {
		//extend Lockable interface/extend Lockable for Circles, buttons (share common methods)
		
		int lockIndex = -1; //index of locked circle
		for (int i = 0; i < circles.length; i++) {
			if (circles[i].isLocked())
				lockIndex = i;
		} //loop
		
		return lockIndex;
	} //end getLockedButton
	
	/**
	 * Request to lock a circle
	 * @param index Index of the circle requested
	 */
	public void requestCircleLock(int index) {
		int lockIndex = getLockedCircleIndex();
		
		if (lockIndex == -1) { //no buttons locked
			//lock this
			circles[index].setState(BUTTON_STATE.LOCK); 
		
		} else if (lockIndex != index) { //another button is locked
			//lock this, unlock that
			circles[lockIndex].setState(BUTTON_STATE.DEFAULT);
			circles[index].setState(BUTTON_STATE.LOCK); 
			
		} else if (lockIndex == index) { //this button is locked
			circles[index].setState(BUTTON_STATE.DEFAULT);
		} //if
	} //end requestCircleLock
	
	//Polyline
	
	private void updatePolylines() {
		updateCenterPolyline();		
		updateHeadings();
		updateSidePolylines();
	}
	
	private void updateCenterPolyline() {
		int[] x = new int[numSegments];
		int[] y = new int[numSegments];
		
		double t = 0;
		
		for (int i = 0; i < numSegments; i++) {
			Point p = this.calcPoint(t);
			
			//flip x and y because of field config
			x[i] = (int) (p.getY() * (double) AutoSim.PPI); 
			y[i] = (int) (p.getX() * (double) AutoSim.PPI);				
			
			t += 1.0 / numSegments;
		} //loop
		
		poly = new int[][]{x, y};
	} //end updateCenterPolyline
	
	/**
	 * Update the heading values for path offsetting
	 */
	private void updateHeadings() {
		headings = new double[numSegments];
		
		for (int i = 0; i < numSegments; i++) {
			headings[i] = calcHeading(i);
		} //loop
	} //end updateHeadings
	
	/**
	 * Calculate a heading at i knowing all the (x,y) values at regular t intervals
	 * @param i Index in array
	 * @return Heading at i in degrees
	 */
	private double calcHeading(int i) {
		int[] x = poly[1];
		int[] y = poly[0];
		
		if (i == 0) {
			Point p1 = new Point(x[1], y[1]);
			Point p2 = new Point(x[0], y[0]);
			return FieldPositioning.goalYaw(p1, p2);
		} //if
		
		Point p1 = new Point(x[i-1], y[i-1]);
		Point p2 = new Point(x[i], y[i]);
		return FieldPositioning.goalYaw(p1, p2);
	} //end calcHeading
	
	/**
	 * Calculate the polylines for the left and right sides of the drive
	 */
	private void updateSidePolylines() {
		int[] x = poly[0];
		int[] y = poly[1];
		
		int[] xL = new int[numSegments];
		int[] yL = new int[numSegments];
		int[] xR = new int[numSegments];
		int[] yR = new int[numSegments];
		
		double r = this.trackWidth;
		
		for (int i = 0; i < numSegments; i++) {
			//calc left and right sides
			double thetaL, thetaR; //angle offsets for left and right
			thetaL = Math.toRadians(headings[i] + 90);
			thetaR = Math.toRadians(headings[i] - 90);
			
			if (i == 0) { //swap values for first point
				double buffer = thetaL;
				thetaL = thetaR;
				thetaR = buffer;
			} //if
			
			//left side
			xL[i] = (int) (x[i] + r * Math.cos(thetaL)); 
			yL[i] = (int) (y[i] + r * Math.sin(thetaL)); 

			//right side
			xR[i] = (int) (x[i] + r * Math.cos(thetaR)); 
			yR[i] = (int) (y[i] + r * Math.sin(thetaR)); 
		} //loop
		
		leftPoly = new int[][] {xL, yL};
		rightPoly = new int[][] {xR, yR};
	} //end updateSidePolylines
	
	/**
	 * Get the polyline for animation purposes
	 * @return X points and Y points of the curve
	 */
	public int[][] getPolyline() {
		return poly;
	} //end getPolyline
	
	/**
	 * Get the left polyline for animation purposes
	 * @return (x,y) points for the left side curve
	 */
	public int[][] getLeftPolyline() {
		return leftPoly;
	} //end getLeftPolyline
	
	/**
	 * Get the right polyline for animation purposes
	 * @return (x,y) points for the right side curve
	 */
	public int[][] getRightPolyline() {
		return rightPoly;
	} //end getRightPolyline
} //end class
