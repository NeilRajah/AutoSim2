/**
 * PursuitPath
 * Author: Neil Balaskandarajah
 * Created on: 10/05/2020
 * Path object for Pure Pursuit tracking
 */

package model.motion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import model.FieldPositioning;
import model.Point;
import util.Util;

public class PursuitPath {
	//Attributes
	private BezierPath path; //quintic bezier spline
	private double trackWidth; //wheel-wheel robot width in inches
	private double maxVel; //top speed in in/s
	private double acc; //acceleration constant in in/s^2
	private double dec; //deceleration constant in in/s^2
	private double DIST_STEP; //spacing between points
	
	private Point[] points; //points along the path
	private double totalLength; //total length of the path in inches
	private double[] distAlongPath; //distance along the path at each point in inches
	private double[] radius; //radius of the path at each point
	private double[] vel; //velocity robot should be following at each point
	
	/**
	 * Create a Pursuit Path for the robot to follow
	 * @param controlPts Control points for quintic spline
	 * @param trackWidth Wheel-wheel robot width in inches
	 * @param maxVel Top velocity for robot to reach in in/s
	 * @param acc Magnitude of acceleration constant in in/s^2
	 * @param dec Magnitude of deceleration constant in in/s^2
	 * @param spacing Linear distance between points in inches
	 */
	public PursuitPath(Point[] controlPts, double trackWidth, double maxVel, double acc, double dec, double spacing) {
		//set attributes
		this.path = new BezierPath(controlPts);
		this.trackWidth = trackWidth;
		this.maxVel = maxVel;
		this.acc = acc;
		this.dec = dec;
		this.DIST_STEP = spacing;
		
		//create the necessary arrays 
		createPath();
	} //end constructor
	
	/**
	 * Create a Pursuit Path for the robot to follow
	 * @param controlPts Control points for quintic spline
	 * @param trackWidth Wheel-wheel robot width in inches
	 * @param maxVel Top velocity for robot to reach in in/s
	 * @param acc Magnitude of acceleration and deceleration constants in in/s^2
	 * @param spacing Linear distance between points in inches
	 */
	public PursuitPath(Point[] controlPts, double trackWidth, double maxVel, double acc, double spacing) {
		this(controlPts, trackWidth, maxVel, acc, -acc, spacing);
	} //end constructor
	
	/**
	 * Create a Pursuit Path for the robot to follow
	 * @param controlPts Control points for quintic spline
	 * @param trackWidth Wheel-wheel robot width in inches
	 * @param maxVel Top velocity for robot to reach in in/s
	 * @param acc Magnitude of acceleration constant in in/s^2
	 * @param dec Magnitude of deceleration constant in in/s^2
	 * @param spacing Linear distance between points in inches
	 */
	public PursuitPath(double[][] controlPts, double trackWidth, double maxVel, double acc, double dec, double spacing) {
		this(FieldPositioning.pointsFromDoubles(controlPts), trackWidth, maxVel, acc, dec, spacing);
	} //end constructor
	
	/**
	 * Create a Pursuit Path for the robot to follow
	 * @param controlPts Control points for quintic spline
	 * @param trackWidth Wheel-wheel robot width in inches
	 * @param maxVel Top velocity for robot to reach in in/s
	 * @param acc Magnitude of acceleration constant in in/s^2
	 * @param spacing Linear distance between points in inches
	 */
	public PursuitPath(double[][] controlPts, double trackWidth, double maxVel, double acc, double spacing) {
		this(FieldPositioning.pointsFromDoubles(controlPts), trackWidth, maxVel, acc, -acc, spacing);
	} //end constructor
	
	/**
	 * Create a Pursuit Path object wrapping data together
	 * @param points (x,y) points
	 * @param distAlongPath Distance along path at each point
	 * @param radius Radius of travel at each point
	 * @param vel Robot velocity at each point
	 */
	public PursuitPath(Point[] points, double[] distAlongPath, double[] radius, double[] vel) {
		//Set the values directly
		this.points = points;
		this.distAlongPath = distAlongPath;
		this.radius = radius;
		this.vel = vel;
	} //end constructor
	
	/**
	 * Create the path object
	 */
	private void createPath() {
		/*
		 * Create a list of points with even t value spacing. This is later used to split the
		 * curve up into segments of equal length.
		 */
		double[] tDistances = parameterizeByT();
		
		/*
		 * Create the evenly spaced points list by linearly interpolating between the points based on the
		 * distance along the curve. The curve radius (inverse of curvature) and heading (degrees) is
		 * also calculated at this point.
		 */
		parameterizeByD(tDistances);
		
		/**
		 * Calculate the radius of travel between adjacent points. Beginning and end segments
		 * are assumed to be straight (ie. very large radius) such that the curvature value 
		 * (C = 1/r) is very small.
		 */
		calcRadii();
		
		/*
		 * Constrain the center velocity of the robot by the path's curvature. 
		 */
		applyCurvatureConstraint();
		
		/*
		 * Constrain the center velocity of the robot by the maximum acceleration with a forward pass
		 * of the center velocity list.
		 */
		applyAccelerationConstraint();
		
		/*
		 * Constrain the center velocity of the robot by the maximum deceleration with a backward pass
		 * of the center velocity list.
		 */
		applyDecelerationConstraint();
	} //end createPath
	
	/**
	 * Parameterize the curve with evenly spaced t-values
	 * @return Array with distance along path at each step
	 */
	private double[] parameterizeByT() {
		final int size = 500; //total resolution
		final double step = 1.0/size; //step size for t-value
		
		double t = 0; //parametric value
		double dist = 0; //cumulative distance
		
		double[] distances = new double[size];
		distances[0] = 0; //start with zero distance
		
		for (int i = 1; i < size; i++) {
			t += step;
			//cumulatively sum the distance
			dist += FieldPositioning.dist(path.calcPoint(t), path.calcPoint(t - step));
			distances[i] = dist;
		} //loop
		
		//total length of the path is the fully summed value
		this.totalLength = distances[distances.length-1];
		return distances;
	} //end parameterizeByT
	
	/**
	 * Split the curve up into equally spaced points
	 * @param tDistances Distance along the path when parameterically defined
	 */
	private void parameterizeByD(double[] tDistances) {
		final int size = (int) Math.ceil(this.totalLength / DIST_STEP); //total length / step length
		final double step = 1.0 / tDistances.length; //for new t values
		double dist = 0; //cumulative distance
		
		//initialize the points array
		this.points = new Point[size];
		this.points[0] = path.getControlPoints()[0]; //first point of path object
		
		//initialize the distance array
		this.distAlongPath = new double[size];
		distAlongPath[0] = 0; //start at zero units
		
		for (int i = 1; i < size; i++) {			
			double d = (double) i * DIST_STEP;
			//index of the distance in tDistances just below dist (bottom of the sandwich)
			int k = Util.findSandwichedElements(tDistances, d, 1E-3)[0];
			
			//linear interpolation
			double t2 = (k+1) * step;
			double t1 = k * step;
			double d2 = tDistances[k+1];
			double d1 = tDistances[k];
			
			//add the point at the interpolated t value to the curve
			double t = Util.interpolate(d, t1, d1, t2, d2);
			points[i] = path.calcPoint(t);
			
			//add the distance to the distances array
			dist += FieldPositioning.dist(points[i-1], points[i]);
			distAlongPath[i] = dist;
		} //loop
	} //end parameterizeByD
	
	/**
	 * Calculate the radius of travel at each point on the curve
	 */
	private void calcRadii() {
		radius = new double[points.length];
		
		//first and last segments have max radius
		radius[0] = 100000;
		radius[radius.length-1] = 100000;
		
		//calculate radii at each point
		for (int i = 1; i < radius.length-1; i++) {
			radius[i] = FieldPositioning.calcRadius(points[i-1], points[i], points[i+1]);
		} //loop
	} //end calcRadii
	
	/**
	 * Constrain the center velocity based on the curvature of the path
	 */
	private void applyCurvatureConstraint() {
		//create the velocities array
		vel = new double[points.length];
		
		//fill the center velocities
		for (int i = 0; i < points.length; i++) {
			vel[i] = (maxVel * radius[i]) / (radius[i] + trackWidth/2);
		} //loop
	} //end applyCurvatureConstraint
	
	/**
	 * Constrain the center velocity based on the maximum acceleration
	 */
	private void applyAccelerationConstraint() {
		vel[0] = 0; //start at zero initial velocity
		
		//calculate the new center velocity at each point, skipping the first one
		for (int i = 1; i < vel.length; i++) {
			double velFromAcc = Math.sqrt(Math.pow(vel[i-1], 2) + 2 * acc * DIST_STEP); //sqrt(v^2 + 2ad)
			vel[i] = Math.min(velFromAcc, vel[i]); //minimum of this new constraint and old value
		} //loop
	} //end applyAccelerationConstraint
	
	/**
	 * Constrain the center velocity based on the maximum deceleration
	 */
	private void applyDecelerationConstraint() {
		//start at final speed of zero
		vel[vel.length - 1] = 0;
		
		//calculate the new center velocity looping backwards, skipping the first one
		for (int i = points.length - 2; i >= 0; i--) {
			double velFromAcc = Math.sqrt(Math.pow(vel[i+1], 2) + 2 * dec * DIST_STEP); //sqrt(v^2 + 2ad)
			vel[i] = Math.min(velFromAcc, vel[i]); //minimum of this new constraint and old value
		} //loop
	} //end applyDecelerationConstraint
	
	//Getters
	
	//File
	
	/**
	 * Write the path to a file
	 * @param filename Name of the file
	 * @return True if writing was successful, false if not
	 */
	public boolean writeToFile(String filename) {
		try {
			filename = Util.UTIL_DIR + filename + ".prstpath";
			PrintWriter pw = new PrintWriter(new File(filename));
			
			//header of file is size of path
			pw.println(points.length);
			
			//(x,y), distAlongPath, radius, velocity
			for (int i = 0; i < points.length; i++) {
				//x y distAlongPath radius vel
				pw.println(String.format("%.3f %.3f %.3f %.3f %.3f", 
							points[i].getX(), points[i].getY(), //(x,y) point
							distAlongPath[i], //based off index because evenly spaced
							radius[i], //radius
							vel[i])); //velocity
			} //loop
			
			pw.close();
			
			return true;
			
		} catch (FileNotFoundException f) {
			Util.println("Could not find", filename);
			return false;
		} //try-catch
	} //end writeToFile
	
	/**
	 * Write the path to a JS file
	 * @param filename Name of the file
	 * @return True if writing was successful, false if not
	 */
	public boolean writeToJSFile(String name) {
		try {
			String filename = Util.UTIL_DIR + name + ".js";
			PrintWriter pw = new PrintWriter(new File(filename));
			
			//JS info
			pw.println("//Create an invisible element in the DOM with the contents of the path to read later");
			pw.println("var " + name + " = document.createElement('" + name + "');");
			pw.print(name + ".innerText = `");
			
			//header of file is size of path
			pw.println(points.length);
			for (int i = 0; i < points.length; i++) {
				//x y distAlongPath radius vel
				pw.println(String.format("%.3f %.3f %.3f %.3f %.3f", 
							points[i].getX(), points[i].getY(), //(x,y) point
							distAlongPath[i], //based off index because evenly spaced
							radius[i], //radius
							vel[i])); //velocity
			} //loop
			
			pw.println("`");
			pw.println(name + ".style.display = \"none\";");
			
			pw.close();
			
			return true;
			
		} catch (FileNotFoundException f) {
			Util.println("Could not find", name);
			return false;
		} //try-catch
	} //end writeToFile

	/**
	 * Write the (x,y) points straight to a JS file 
	 * @param name Name of the file and of the variable with the arrays
	 * @param sx Scale factor for x values
	 * @param sy Scale factor for y values
	 * @param flip Whether to flip the x and y points or not
	 * @return True if writing was successful, false if not
	 */
	public boolean saveXYToJS(String name, double sx, double sy, boolean flip) {
		try {
			String filename = Util.UTIL_DIR + name + ".js";
			PrintWriter pw = new PrintWriter(new File(filename));
			
			//set up the variable
			pw.println(String.format("%s = [", name));
			for (int i = 0; i < points.length; i++) {
				double x = points[i].getX() * sx;
				double y = points[i].getY() * sy;
				if (flip) 
					pw.println(String.format("[%.3f, %.3f],", y, x));
				else
					pw.println(String.format("[%.3f, %.3f],", x, y));
			}
			pw.print("]");
			pw.close();
			
			return true;
			
		} catch (FileNotFoundException f) {
			Util.println("Could not find", name);
			return false;
		} //try-catch
	} //end saveXYtoJS
	
	/**
	 * Create a path object from a file
	 * @param filename Name of the file
	 * @return Path object if reading & creating was successful, null if not
	 */
	public static PursuitPath createFromFile(String filename) {
		try {
			Scanner s = new Scanner(new File(Util.UTIL_DIR + filename + ".prstPath"));
			final int size = Integer.parseInt(s.nextLine()); //size from first line
			
			//Values for path
			Point[] points = new Point[size];
			double[] distAlongPath = new double[size];
			double[] radius = new double[size];
			double[] vel = new double[size];
			
			//fill the above arrays
			for (int i = 0; i < size; i++) {
				points[i] = new Point(s.nextDouble(), s.nextDouble());
				distAlongPath[i] = s.nextDouble();
				radius[i] = s.nextDouble();
				vel[i] = s.nextDouble();
			} //loop
			
			//close the Scanner
			s.close();
					
			return new PursuitPath(points, distAlongPath, radius, vel);
		
		//Could not find the file
		} catch (FileNotFoundException f) {
			Util.println("Could not find", filename);			
			return null;
		
		//Issue parsing the file
		} catch (NumberFormatException n) {
			Util.println("Issue parsing", filename); 
			return null;
		} //try-catch
	} //end createFromFile
	
	/**
	 * Get the points in the path
	 * @return Evenly spaced points on the path
	 */
	public Point[] getPoints() {
		return points;
	} //end getPoints
	
	/**
	 * Get the path's initial heading
	 * @return Initial heading of the path for the robot to point at
	 */
	public double getInitialHeading() {
		Point delta = Point.subtract(points[1], points[0]);
		return Math.atan2(delta.getY(), delta.getX()) - Math.PI/2; 
	} //end getInitialHeading
} //end class