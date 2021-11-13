/**
 * TimedVoltage
 * Author: Neil Balaskandarajah
 * Created on: 20/01/2020
 * Set a voltage to the left and right sides of the robot for a number of seconds
 */
package commands;

import java.util.ArrayList;

import model.DriveLoop;
import model.Pose;
import util.Util;

public class TimedVoltage extends Command {
	//Attributes
	//General
	private DriveLoop loop; //drivetrain loop to update
	
	//Configured
	private double leftVoltage; //voltage applied to left gearbox
	private double rightVoltage; //voltage applied to right gearbox
	private double time; //seconds to apply voltages for
	
	//Utility
	private double counter; //tracking time for isFinished
	
	/**
	 * Set a voltage to the left and right sides of the robot
	 * @param loop - drivetrain loop to update
	 * @param leftVoltage - voltage to apply to left gearbox
	 * @param rightVoltage - voltage to apply to right gearbox
	 * @param time - seconds to apply voltage
	 */
	public TimedVoltage(DriveLoop loop, double leftVoltage, double rightVoltage, double time) {
		//set attributes
		this.loop = loop;
		this.leftVoltage = leftVoltage;
		this.rightVoltage = rightVoltage;
		this.time = time;
		counter = 0.0;
		
		//set robot and name
		this.robot = loop.getRobot();
	} //end constructor
	
	/**
	 * Set a voltage to the left and right sides of the robot
	 * @param loop - drivetrain loop to update
	 * @param voltage - voltage to apply to both gearboxes
	 * @param time - seconds to apply voltage
	 */
	public TimedVoltage(DriveLoop loop, double voltage, double time) {
		this(loop, voltage, voltage, time);
	} //end constructor

	protected void initialize() {}

	/**
	 * Execute the command by applying voltage to the robot andd increasing the counter
	 */
	protected void execute() {
		loop.getRobot().update(leftVoltage, rightVoltage);
		counter += Util.UPDATE_PERIOD;
	} //end execute

	/**
	 * Return whether or not the time has passed
	 * return - whether counter is greater than or equal to time
	 */
	protected boolean isFinished() {
		if (counter >= time) {
			return true;
		} //if
		return false;
	} //end isFinished

	/**
	 * Print useful information
	 */
	protected void end() {
//		Util.println("Final Velocity:", loop.robot().getLinearVel());
//		Util.println("Velocity Ratio:", loop.robot().getLinearVel()/loop.getRobot().getMaxLinSpeed());
//		Util.println("Voltage Ratio:", (leftVoltage + rightVoltage) / (2 * Util.MAX_VOLTAGE));
//		Util.println("kV:", Util.MAX_VOLTAGE / loop.getRobot().getMaxLinSpeed());
	} //end isFinished
	
	/**
	 * Run if the command times out
	 */
	protected void timedOut() {} 
} //end class
