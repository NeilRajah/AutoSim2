/**
 * DriveOpenLoopProfile
 * Author: Neil Balaskandarajah
 * Created on: 17/04/2020
 * Drive following an open loop profile
 */

package commands;

import model.DriveLoop;
import model.motion.DriveProfile;
import model.motion.TrapezoidalProfile;
import util.Util;

public class DriveOpenLoopProfile extends Command {
	//Attributes
	private DriveLoop loop; //loop that controls robot
	private DriveProfile traj; //trajectory to follow
	private int index; //index of the point in the trajectory
	
	/**
	 * Follow a simple, symmetric trapezoidal motion profile
	 * @param loop Loop that controls robot
	 * @param totalDist Total distance of the profile
	 * @param accDist Acceleration and deceleration distance (must be less than totalDist)
	 * @param maxVel The top speed of the robot during the profile
	 */
	public DriveOpenLoopProfile(DriveLoop loop, double totalDist, double accDist, double maxVel) {
		this(loop, new TrapezoidalProfile(totalDist, accDist, maxVel));
	} //end constructor
	
	/**
	 * Follow a simple, symmetric trapezoidal motion profile
	 * @param loop Loop that controls robot
	 * @param trap Trapezoidal motion profile
	 */
	public DriveOpenLoopProfile(DriveLoop loop, TrapezoidalProfile trap) {
		//set attributes
		this.loop = loop;
		this.traj = trap;
		this.robot = loop.getRobot();
	} //end constructor

	/**
	 * Set the state of the loop and zero the index
	 */
	protected void initialize() {
		loop.setOpenLoopProfileState();
		index = 0;
	} //end initialize

	/**
	 * Send the trajectory points to the loop and update it
	 */
	protected void execute() {
		double time = index * Util.UPDATE_PERIOD; //would be getting actual time on real robot
		loop.updateOpenLoopProfileState(traj.getLeftTrajPoint(time), traj.getRightTrajPoint(time));
		loop.onLoop();
		index += 1;
	} //end execute

	/**
	 * End the command when the trajectory time has passed
	 */
	protected boolean isFinished() {
		return (index * Util.UPDATE_PERIOD) > traj.getTotalTime();
	} //end isFinished
	
	protected void end() {
		Util.println(loop.getRobot().getAveragePos());
	}
	
	protected void timedOut() {}
} //end class
