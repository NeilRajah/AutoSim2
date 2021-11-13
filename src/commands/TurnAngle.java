/**
 * TurnAngle
 * Author: Neil Balaskandarajah
 * Created on: 03/01/2020
 * Use PID control to turn to an angle within a tolerance
 */

package commands;

import java.util.ArrayList;

import model.DriveLoop;
import model.Pose;

public class TurnAngle extends Command {
	//Attributes
	//General
	private DriveLoop loop; //drivetrain loop to update
	
	//Specific
	private double angle; //angle to turn to in degrees
	private double tolerance; //tolerance to be within in degrees
	private double topSpeed; //maximum linear wheel speed in ft/s
	private boolean relative; //whether the angle is relative to the current heading or not
	
	/**
	 * Turn the robot to an angle within a specific tolerance below a top speed
	 * @param loop - drivetrain loop to update
	 * @param angle - angle to turn to in degrees
	 * @param tolerance - tolerance to be within in degrees
	 * @param topSpeed - maximum linear wheel speed in ft/s
	 * @param relative - whether the angle is relative to the current heading or not
	 */
	public TurnAngle(DriveLoop loop, double angle, double tolerance, double topSpeed, boolean relative) {
		//set attributes
		this.loop = loop;
		this.angle = Math.toRadians(angle);
		this.tolerance = Math.toRadians(tolerance);
		this.topSpeed = topSpeed;
		this.relative = relative;
		
		//set robot and name
		this.robot = loop.getRobot();
	} //end constructor

	/**
	 * Initialize the command by setting the state machine state and corresponding values
	 */
	protected void initialize() {
		loop.setTurnAngleState(angle, topSpeed, tolerance, relative);
	} //end initialize

	/**
	 * Execute the command by running the command
	 */
	protected void execute() {
		loop.onLoop();
	} //end execute

	/**
	 * Return whether the robot is moving slowly within the target or not
	 * @return - true if it is, false if it is not
	 */
	protected boolean isFinished() {
		return loop.isTurnPIDAtTarget() && loop.isRobotSlowerThanPercent(0.05);
	} //end isFinished

	/**
	 * Run at the end of the command
	 */
	protected void end() {}
	
	/**
	 * Run if the command times out
	 */
	protected void timedOut() {} 
} //end class

