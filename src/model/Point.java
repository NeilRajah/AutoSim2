/**
 * Point
 * Author: Neil
 * Created on: 28/12/2019
 * Class to define the (x,y) positions on the field
 */
package model;

import util.Util;

public class Point {
	//Attributes
	//Configured
	private double x; //x position of the point
	private double y; //y position of the point
	private double mag; //magnitude of the point from origin
	private double heading; //heading of the point normalized to [-pi,pi]
	
	/**
	 * Create an (x,y) point
	 * @param x X-value of the point
	 * @param y Y-value of the point
	 */
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	} //end constructor
	
	/**
	 * Create an (x,y) point
	 * @param xy (x,y) coordinates for the point
	 */
	public Point(double[] xy) {
		this(xy[0], xy[1]);
	} //end constructor

	/**
	 * Get the x value of the point
	 * @return x X value of the point
	 */
	public double getX() {
		return x;
	} //end getX
	
	/**
	 * Set the x value of the point
	 * @param x New x value for the point
	 */
	public void setX(double x) {
		this.x = x;
	} //end setX
	
	/**
	 * Get the y value of the point
	 * @return y Y value of the point
	 */
	public double getY() {
		return y;
	} //end getY
	
	/**
	 * Set the y value of the point
	 * @param y New y value for the point
	 */
	public void setY(double y) {
		this.y = y;
	} //end setY
	
	/**
	 * Get the magnitude of the point
	 * @return Point magnitude (distance from point to origin)
	 */
	public double getMag() {
		return mag == 0.0 ? mag = Math.hypot(x,y) : mag;
	} //end getMag
	
	/**
	 * Get the heading of the point
	 * @return Point heading in radians
	 */
	public double getHeading() {
		return heading == 0.0 ? heading = Math.atan2(y,x) : heading;
	} //end getHeading
	
	/**
	 * Get the x and y values in (x,y) format
	 * @return String representation of the point
	 */
	public String getString() {
		return String.format("(%.3f,%.3f)", x, y);
	} //end getString
	
	/**
	 * Translate the point a given magnitude at a given angle
	 * @param magnitude Distance to translate the point 
	 * @param angle Angle to translate the point at in radians
	 */
	public void translate(double magnitude, double angle) {
		//sin and cos flipped because x and y of field are flipped
		this.x += magnitude * Math.sin(angle); 
		this.y += magnitude * Math.cos(angle);
	} //end translate
	
	/**
	 * Safe copy of this point
	 * @return Clone of this point
	 */
	public Point clone() {
		return new Point(x, y);
	} //end clone
	
	/**
	 * Check if this point equals another Point
	 * @param obj Point object to compare to
	 * @return True if point components are within an epsilon
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof Point) {
			Point p = (Point) obj;
			double eps = 0.001;
			return Util.fuzzyEquals(this.x, p.getX(), eps) &&
					Util.fuzzyEquals(this.y, p.getY(), eps);
		} //if
		return false;
	} //end equals
	
	/**
	 * Create a random Point within a given range
	 * @param x Base x value
	 * @param xRange +/- range for the x value
	 * @param y Base y value
	 * @param yRange +/- range for the y value
	 * @return Point within the specified range
	 */
	public static Point randomPoint(double x, double xRange, double y, double yRange) {
		double xVal = (x - xRange) + 2 * xRange * Math.random();
		double yVal = (y - yRange) + 2 * yRange * Math.random();
		return new Point(xVal, yVal);
	}
	
	//Math
	
	/**
	 * Return the sum of two points
	 * @param p1 First point
	 * @param p2 Second point
	 * @return p1 + p2
	 */
	public static Point add(Point p1, Point p2) {
		return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	} //end add
	
	/**
	 * Return the difference of two points
	 * @param p1 First point
	 * @param p2 Second point
	 * @return p1 - p2
	 */
	public static Point subtract(Point p1, Point p2) {
		return new Point(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	} //end subtract
	
	/**
	 * Scale a point by a constant k
	 * @param p Point to scale
	 * @param k Constant scalar
	 * @return k * (px, py)
	 */
	public static Point scale(Point p, double k) {
		return new Point(p.getX() * k, p.getY() * k);
	} //end scale
	
	/**
	 * Set the magnitude of a point
	 * @param p Point to set magnitude of
	 * @param mag New magnitude for the point
	 * @return p scaled to have a magnitude of mag
	 */
	public static Point setMag(Point p, double mag) {
		return scale(p, mag / p.getMag());
	} //end setMag	
	
	/**
	 * Limit the magnitude of a point
	 * @param p Point to limit magnitude of
	 * @param mag New magnitude for the point
	 * @return p scaled to have a magnitude of min(mag, currentMag)
	 */
	public static Point limitMag(Point p, double mag) {
		return p.getMag() > mag ? setMag(p, mag) : p;
	} //end limitMag	
	
	/**
	 * Return the point with the smaller magnitude
	 * @param a First point
	 * @param b Second point
	 * @return Point with smaller magnitude
	 */
	public static Point minMag(Point a, Point b) {
		return a.getMag() < b.getMag() ? a : b;
	} //end minMag
	
	/**
	 * Normalize a point to be a unit vector
	 * @param p Point to normalize
	 * @return p with a magnitude of 1
	 */
	public static Point normalize(Point p) {
		return Point.scale(p, 1.0 / p.getMag());
	} //end normalize
	
	/**
	 * Return the scalar product of two points
	 * @param a First point
	 * @param b Second point
	 * @return Dot product of a and b
	 */
	public static double dot(Point a, Point b) {
		return a.getX() * b.getX() + a.getY() * b.getY();
	} //end dot
	
	/**
	 * Return the head of a vector
	 * @param mag Length of the vector
	 * @param hdg Heading vector is pointing at
	 * @return
	 */
	public static Point vector(double mag, double hdg) {
		return new Point(mag * Math.cos(hdg), mag * Math.sin(hdg));
	}
} //end Point