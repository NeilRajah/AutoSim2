/**
 * SetPose
 * Author: Neil Balaskandarajah
 * Created on: 13/01/2020
 * Set the pose of the robot
 */
package commands;

import model.DriveLoop;
import model.DriveLoop.STATE;
import model.Point;

public class SetPose extends Command {
	//Attributes
	private DriveLoop loop; //drivetrain loop to update
	private Point p; //point for robot to be at
	private double heading; //heading for robot to be at
	
	/**
	 * Set the pose of the robot 
	 * @param driveLoop - drivetrain loop to update
	 * @param p - point to set (x,y) of robot to
	 * @param heading - heading to set robot to in degrees
	 */
	public SetPose(DriveLoop driveLoop, Point p, double heading) {
		//set attributes
		this.loop = driveLoop;
		this.p = p;
		this.heading = heading;
		
		//set name and robot waited status
		this.robot = loop.getRobot();
	} //end constructor
	
	/**
	 * Set the pose of the robot 
	 * @param driveLoop - drivetrain loop to update
	 * @param x - x value to set robot to
	 * @param y - y value to set robot to
	 * @param heading - heading to set robot to in degrees
	 */
	public SetPose(DriveLoop driveLoop, double x, double y, double heading) {
		this(driveLoop, new Point(x, y), heading);
	} //end constructor
	
	/**
	 * Set the pose of the robot with zero heading
	 * @param driveLoop drivetrain loop to update
	 * @param x x value to set robot to
	 * @param y y value to set robot to
	 */
	public SetPose(DriveLoop driveLoop, double x, double y) {
		this(driveLoop, new Point(x, y), 0);
	} //end setPose

	/**
	 * Set the pose of the robot
	 */
	protected void initialize() {
		loop.getRobot().setXY(p);
		loop.getRobot().setHeading(Math.toRadians(heading));
		loop.setState(STATE.WAITING);
	} //end initialize
	
	
	protected void execute() {}

	/**
	 * Return true upon initialization
	 */
	protected boolean isFinished() {
		return true;
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
