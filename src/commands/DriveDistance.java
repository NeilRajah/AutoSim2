/**
 * DriveDistance
 * Author: Neil Balaskandarajah
 * Created on: 03/01/2020
 * Use PID control to drive a distance within a tolerance
 */

package commands;

import model.DriveLoop;
import util.Util;

public class DriveDistance extends Command {
	//Attributes
	//General
	private DriveLoop loop; //drivetrain loop to update
	
	//Specific
	private double distance; //distance to drive in inches
	private double tolerance; //tolerance to be within in inche
	private double topSpeed; //top speed to drive at in inches
	
	/**
	 * Drive the robot a specified distance within a tolerance and below a top speed
	 * @param loop Drivetrain loop to update
	 * @param distance Distance to drive in inches
	 * @param tolerance Tolerance to be within in inches
	 * @param topSpeed Top speed to drive at in inches
	 */
	public DriveDistance(DriveLoop loop, double distance, double tolerance, double topSpeed) {
		//set attributes
		this.loop = loop;
		this.distance = distance;
		this.tolerance = tolerance; 
		this.topSpeed = topSpeed;
		
		//set robot and name
		this.robot = loop.getRobot();
	} 

	/**
	 * Initialize the command by setting the state machine state and corresponding values
	 */
	protected void initialize() {
		loop.setDriveDistanceState(distance + tolerance, topSpeed, tolerance);
	} 

	/**
	 * Execute the command by running the state machine
	 */
	protected void execute() {
		loop.onLoop();
	} 

	/**
	 * Return whether the robot is moving slowly within the target or not
	 * @return True if conditions are met, false if not
	 */
	protected boolean isFinished() {
		/*
		 * Having a small position error and low speed can be considered the 'settling position'
		 * The PID controller has 'settled' when it is within a comfortable distance from the target
		 */
		return loop.isDrivePIDAtTarget() && 
				loop.isRobotSlowerThanPercent(loop.getDrivePID().getP() * tolerance * 0.01);
	} 

	/**
	 * Run at the end of the command
	 */
	protected void end() {
		loop.getRobot().update(0, 0); //stop the robot
	}

	/**
	 * Run if the command times out
	 */
	protected void timedOut() {
		loop.getRobot().update(0, 0); //stop the robot
	} 
} 
