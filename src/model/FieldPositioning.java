/**
 * FieldPositioning
 * Author: Neil
 * Created on: 02/01/2020
 * Utility methods related to field relative positioning
 */
package model;

import java.util.ArrayList;

import util.Util;

public class FieldPositioning {

	/**
	 * Calculate the goal yaw setpoint based on the current and goal positions
	 * @param current The (x,y) position of the robot currently
	 * @param goal The desired (x,y) position of the robot
	 * @return The yaw setpoint for the robot to face the goal point in degrees
	 */
	public static double goalYaw(Point current, Point goal) {
		double dx = goal.getX() - current.getX();
		double dy = goal.getY() - current.getY();
		
		//if goal point lies on x or y axis (dx or dy equal to zero)
		if(dy == 0) { //if no change in y
			return dx > 0 ? 90 : -90;
		} else if (dx == 0) { //if no change in x
			return dy > 0 ? 0 : 180;
		} else {
			if (dy < 0) { //point is behind you
				if (dx > 0) {
					return 90 - Math.toDegrees(Math.atan(dy/dx)); //behind and right
				} else if (dx < 0) {
					return -90 - Math.toDegrees(Math.atan(dy/dx)); //behind and left
				} //if
			} //if
		} //if
		return Math.toDegrees(Math.atan2(dx,dy)); //anywhere else
	} //end calcGoalYaw

	/**
	 * Calculate the distance between two points using the Pythagorean theorem
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Absolute distance between the two points
	 */
	public static double dist(Point p1, Point p2) {
		return Math.hypot(p2.getX() - p1.getX(), p2.getY() - p1.getY());
	} //endd calcDistance
	
	/**
	 * Calculate the angle between two points in radians from the x-axis
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Angle between the two point in radians from the x-axis
	 */
	public static double angleRad(Point p1, Point p2) {
		//calculate deltas
		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();
		
		return Math.atan2(dy, dx);
	} //end calcAngleRad
	
	/**
	 * Calculate the angle between two points in degrees from the x-axis
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Angle between the two point in degrees from the x-axis
	 */
	public static double angleDeg(Point p1, Point p2) {
		return Math.toDegrees(angleRad(p1, p2));
	} //end calcAngleDeg
	
	/**
	 * Check if two points are close enough to another within a range
	 * @param target Target point to be close to
	 * @param current Point to check closeness of
	 * @param range How close current point can be to target point
	 */
	public static boolean isWithinBounds(Point target, Point current, double range) {
		//calculate deltas
		double dx = Math.abs(target.getX() - current.getX());
		double dy = Math.abs(target.getY() - current.getY());
		
		return dx < range && dy < range;
	} //end isWithinBounds
	
	/**
	 * Flip the x and y values of curve points
	 * @param curve Curve to flip
	 * @return Curve with its x and y values flipped
	 */
	public static double[][] flipCurve(double[][] curve) {
		for (int i = 0; i < curve.length; i++) {
			double buffer = curve[i][0];
			curve[i][0] = curve[i][1];
			curve[i][1] = buffer;
		} //loop
		
		return curve;
	} //end flipCurve
	
	/**
	 * Create an array of Points from a 2D array of points
	 * @param controlPts 2D array of points containing (x,y) of control points
	 * @return Control points as Point objects
	 */
	public static Point[] pointsFromDoubles(double[][] controlPts) {
		//turn double[][] to point array 
		Point[] curvePts = new Point[6];
		for (int i = 0; i <= 5; i++) {
			curvePts[i] = new Point(controlPts[i]);
		} //loop
		
		return curvePts;
	} //end pointsFromDoubles
	
	/**
	 * Calculate the radius of the circle formed by three points
	 * @param p1 First point
	 * @param p2 Second point
	 * @param p3 Third point
	 * @return Radius formed by those three points
	 */
	public static double calcRadius(Point p1, Point p2, Point p3) {
		//triangle side lengths
		double a = dist(p1, p2);
		double b = dist(p2, p3);
		double c = dist(p1, p3);
		
		//Heron's formula for area
		double s = (a + b + c) / 2.0;
		double k = Math.sqrt(s * (s - a) * (s - b) * (s - c));
		
		//radius is derived from side lengths and area
		return Util.fuzzyEquals(k, 0, 0.001) ? 100000 : (a * b * c) / (4 * k);
	} //end calcRadius
	
	/**
	 * Return the angle between two points
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Difference in point headings
	 */
	public static double angleBetween(Point p1, Point p2) {
		return p2.getHeading() - p1.getHeading();
	} //end angleBetween
	
	/**
	 * Constrain an angle to the [-pi,pi] domain
	 * @param ang Angle in radians to costrain
	 * @return Equivalent angle in [-pi,pi] domain
	 */
	public static double angleWrap(double ang) {
		return ang - 2*Math.PI * Math.floor((ang + Math.PI) / (2*Math.PI));
	} //end angleWrap
	
	/**
	 * Constrain an angle to the [-180,180] domain
	 * @param ang Angle in degrees to costrain
	 * @return Equivalent angle in [-180,180] domain
	 */
	public static double angleWrapDeg(double angDeg) {
		return angDeg - 360 * Math.floor((angDeg + 180) / 360);
	} //end angleWrapDeg
	
	/**
	 * Return the closest point to p from ab
	 * @param p Point to find normal of
	 * @param a Start of line segment
	 * @param b End of line segment
	 * @return Point normal to line ab from point p
	 */
	public static Point getNormalPoint(Point p, Point a, Point b) {
		Point A = Point.subtract(p, a);
		Point B = Point.subtract(b, a);
		return Point.add(a, Point.setMag(B, Point.dot(A, Point.normalize(B))));
	} //end getNormalPoint
	
	/**
	 * Check whether a point is on a line segment within a tolerance
	 * @param p Point to check 
	 * @param a First point of line segment
	 * @param b Second point of line segment
	 * @param eps Distance to less than in order to be on the line
	 * @return True if point is close enough to line, false if not
	 */
	public static boolean pointOnLine(Point p, Point a, Point b, double eps) {
		//if the distance between p & a and p & b is close enough to the distance between a and b, return true
		return Util.fuzzyEquals(FieldPositioning.dist(a, b), 
								FieldPositioning.dist(p,a) + FieldPositioning.dist(p, b),
								eps);
	} //end pointOnLine
	
	/**
	 * Check whether a point is on a line segment within a tolerance
	 * @param p Point to check 
	 * @param a First point of line segment
	 * @param b Second point of line segment
	 * @return True if point is close enough to line, false if not
	 */
	public static boolean pointOnLine(Point p, Point a, Point b) {
		return pointOnLine(p, a, b, 0.001);
	} //end pointOnLine
	
	/**
	 * Find the intersection points between a circle and a line segment
	 * Implementation of https://stackoverflow.com/a/1084899/11752569
	 * @param E Beginning point of line segment
	 * @param L End point of line segment
	 * @param C Center of the circle (Tracker center)
	 * @param r Radius of the circle (lookahead distance)
	 * @return Intersection between the circle and the line segment
	 */
	public static ArrayList<Point> lineCircleIntersect(Point E, Point L, Point C, double r) {
		Point d = Point.subtract(L, E); //direction of line segment
		Point f = Point.subtract(E, C);  //from circle center to line segment
		
		//quadratic constants
		double a = Point.dot(d, d);
		double b = 2 * Point.dot(f, d);
		double c = Point.dot(f, f) - r*r;
		double discriminant = b*b - 4*a*c;
		
		//if discriminant is less than zero, no intersections
		if (discriminant < 0)
			return new ArrayList<Point>();
		
		//if discriminant is greater than zero, there are up to two intersections
		discriminant = Math.sqrt(discriminant);
		double t1 = (-b - discriminant) / (2 * a);
		double t2 = (-b + discriminant) / (2 * a);
		
		///points calculated from the quadratic constants
		ArrayList<Point> intersects = new ArrayList<Point>();
		Point p1 = Point.add(E, Point.scale(d, t1));
		Point p2 = Point.add(E, Point.scale(d, t2));
		
		//if the points are on the line segment, add them to the intersections list and return
		if (pointOnLine(p1, E, L))
			intersects.add(p1);
		if (pointOnLine(p2, E, L))
			intersects.add(p2);
		return intersects;
	} //end lineCircleIntersect
	
	/**
	 * Get the square of the distance between points (faster than square root)
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Distance squared between points
	 */
	public static double distsq(Point p1, Point p2) {
		Point delta = Point.subtract(p2, p1);
		return delta.getX() * delta.getX() + delta.getY() * delta.getY();
	} //end distsq
} //end class