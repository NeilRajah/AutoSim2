/**
 * Wait
 * Author: Neil Balaskandarajah
 * Created on: 04/01/2020
 * Have the robot wait at its current position for an amount of time
 */
package commands;

import model.DriveLoop;
import model.DriveLoop.STATE;
import util.Util;

public class Wait extends Command {
	//Attributes
	//General
	private DriveLoop loop; //drivetrain loop to update
		
	//Specific
	private double waitTime; //time to wait in seconds
	private int counter; //counter for knowing when to end command
	private int updates; //number of updates to run before finished 
	
	/**
	 * Have the robot wait at its position in seconds
	 * @param driveLoop - drivetrain loop to update
	 * @param waitTime - time to wait in seconds
	 */
	public Wait(DriveLoop driveLoop, double waitTime) {
		//set attributes
		this.loop = driveLoop;
		this.waitTime = waitTime;
		
		//set robot and name
		this.robot = loop.getRobot();
	} //end constructor

	/**
	 * Initialize the command by setting the state and the number of updates
	 */
	protected void initialize() {
		updates = (int) (waitTime / Util.UPDATE_PERIOD);
		loop.setState(STATE.WAITING);
		robot.setToWait();
	} //end initialize

	/**
	 * Execute the command by incrementing the counter
	 */
	protected void execute() {
		counter++;
	} //end execute
 
	/**
	 * Return whether the time in seconds has elapsed
	 * @return - true if time has elapsed, false if not
	 */
	protected boolean isFinished() {
		return counter > updates;
	} //end isFinished

	/**
	 * Run at the end of the command
	 */
	protected void end() {}
	
	/**
	 * Run if the command times out
	 */
	protected void timedOut() {
		Util.println("timed out");
	} 
} //end class
