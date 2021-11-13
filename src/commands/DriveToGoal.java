/**
 * DriveToGoal
 * Author: Neil Balaskandarajah
 * Created on: 03/01/2020
 * Drive the robot to an (x,y) position on the field
 */
package commands;

import model.DriveLoop;
import model.FieldPositioning;
import model.Point;
import util.Util;

public class DriveToGoal extends Command {
	// Attributes
	// General
	private DriveLoop loop; // drivetrain loop to update

	// Specific
	private Point goalPoint; // goal point to drive to
	private double setpoint; // distance to drive to in inches
	private double goalAngle; // angle to drive to in radians
	private double tolerance; // tolerance to be within in inches
	private double topSpeed; // top speed of robot in ft/s
	private double minSpeed; // min speed of robot in ft/s
	private boolean reverse; // whether the robot is driving backwards or not
	private double scale; // amount to scale the top speed by
	private double lookahead; // additional distance to add onto setpoint to cruise to minSpeed
	
	/*
	 * Why do you spin so much
	 * make a bunch of test cases, check first deltaAngle to see if its most efficient (ie. > 180, > 360, etc.)
	 * has happened when switching from fwd to rev
	 */
	
	/**
	 * Drive to a goal point using the P2P control scheme
	 * @param driveLoop drivetrain loop to update
	 * @param goal goal point to drive to
	 * @param tolerance tolerance to be within in inches
	 * @param topSpeed top speed of the robot in ft/s
	 * @param minSpeed min speed of the robot in ft/s
	 * @param reverse whether the robot is driving backwards or not
	 */
	public DriveToGoal(DriveLoop driveLoop, Point goal, double tolerance, double topSpeed, double minSpeed,
			boolean reverse) {
		// set attributes
		this.loop = driveLoop;
		this.goalPoint = goal;
		this.tolerance = tolerance;
		this.topSpeed = topSpeed;
		this.minSpeed = minSpeed;
		this.reverse = reverse;
		
		//set robot
		this.robot = loop.getRobot();
	} // end constructor
	
	/**
	 * Drive to a goal point given the x, y values of the point, tolerance to be within, maximum and minimum 
	 * 	speeds, and whether the robot should drive in reverse
	 * @param driveLoop drivetrain loop to update
	 * @param x X value of the point
	 * @param y Y value of the point
	 * @param tolerance tolerance to be within in inches
	 * @param topSpeed top speed of the robot in ft/s
	 * @param minSpeed min speed of the robot in ft/s
	 * @param reverse whether the robot is driving backwards or not
	 */
	public DriveToGoal(DriveLoop driveLoop, double x, double y, double tolerance, double topSpeed,
			double minSpeed, boolean reverse) {
		this(driveLoop, new Point(x, y), tolerance, topSpeed, minSpeed, reverse);
	} //end constructor

	/**
	 * Initialize the command by updating the setpoints and setting the state
	 * machine state
	 */
	protected void initialize() {
		updateSetpoints();
		loop.setDriveToGoalState(setpoint, goalAngle, tolerance, topSpeed * scale, minSpeed, reverse);
		robot.setGoalPoint(goalPoint);
	} // end initialize

	/**
	 * Execute the command by updating the state machine and running it
	 */
	protected void execute() {
		updateSetpoints();
		loop.updateDriveToGoalState(setpoint, goalAngle, tolerance, topSpeed * scale, minSpeed, reverse);
		loop.onLoop();
	} // end execute

	/**
	 * Return whether or not the robot is within the target point tolerance circle
	 * @return - true if within, false if not
	 */
	protected boolean isFinished() {
		return FieldPositioning.isWithinBounds(goalPoint, loop.getRobot().getPoint(), tolerance)
				&& loop.isRobotSlowerThanVel(minSpeed);
	} // end isFinished

	/**
	 * Run at the end of the command
	 */
	protected void end() {}
	
	/**
	 * Run when the command is timed out
	 */
	protected void timedOut() {
		Util.println(String.format("Timed out | errorX: %f, errorY: %f, errorH: %f", 
					goalPoint.getX() - loop.getRobot().getX(), goalPoint.getY() - loop.getRobot().getY(),
					goalAngle - loop.getRobot().getHeading()));
	} //end timedOut
	
	/**
	 * Test function for Command
	 */
	protected void test() {
		boolean inBounds = FieldPositioning.isWithinBounds(goalPoint, this.robot.getPoint(), tolerance);
		boolean slowedDown = this.robot.getLinearVel() <= minSpeed;
		
		//Pass if the robot is within bounds and at/below minimum speed
		if (inBounds && slowedDown) {
			this.passed = 1;
			
		//fail if not
		} else {
			this.passed = 0;
			
			System.out.println();
			Util.println("\nTEST FAILED:", this.name);
			
			//point and distance
			if (!inBounds) {
				System.out.println("Expected Point value: " + goalPoint.getString());
				System.out.println("  Actual: " + this.robot.getPoint().getString());
				System.out.println(String.format("  Distance from goal: %.3f", 
									FieldPositioning.dist(this.robot.getPoint(), goalPoint)));
			} //if
			
			//Speed
			if (!slowedDown) {
				System.out.println("Expected Speed: " + minSpeed);
				System.out.println(String.format("  Actual Speed: %.3f", this.robot.getLinearVel()));
			} //if
			
			//reverse
			System.out.println("Reversing: " + Boolean.toString(reverse));
		} //if
		
		System.out.println();
	} //end test
	
	//Helper Methods

	/**
	 * Update the state machine setpoints based on the robot's current position
	 */
	private void updateSetpoints() {
		// distance from robot to goal point
		double dist = FieldPositioning.dist(loop.getRobot().getPoint(), goalPoint);

		// update distance and angle setpoints if more than half a foot away
		if (dist >= tolerance) {
			// distance setpoint
			setpoint = robot.getAveragePos() + (reverse ? -dist : dist) + lookahead;

			// angle setpoint
			goalAngle = Math.toRadians(calcDeltaAngle()) + robot.getHeading();
		} // if

		// scale for top speed
		double dA = Math.abs(Math.toDegrees(goalAngle - robot.getHeading()));
		scale = calcScale(dA);
	} // end updateSetpoints

	/**
	 * Calculate the scalar value for the linear output based on the heading error
	 * @param dA magnitude of angle change in degrees
	 * @return output value to scale linear top speed by
	 */
	private double calcScale(double dA) {
		if (dA > 90) { // if difference is greater than 90
			return 0; // do not move linearly, only turn

		} else {
			// scale linear output quadratically based on the angle error
			double coeff = (1 / Math.pow(90, 2));
			double output = coeff * Math.pow(dA - 90, 2);

			return output;
		} // if
	} // end calcScale

	/**
	 * Calculate the angle change required to face point based on the angle from the
	 * robot to the point
	 * @return - required angle change in degrees
	 */
	private double calcDeltaAngle() {
		double pointYaw = FieldPositioning.goalYaw(robot.getPoint(), goalPoint); // calculate the yaw to
		
		pointYaw -= reverse ? Math.copySign(180, pointYaw) : 0; // add 180 in opposite direction if reversing

		double deltaAngle = pointYaw - robot.getYaw(); // yaw difference between point yaw and robot yaw
				
		//if angle change is in wrong direction
		if (Math.abs(deltaAngle) >= 180) {
			deltaAngle += 360;
		} //if
		
		return deltaAngle;
	} // end calcDeltaAngle
} // end class
