/**
 * DriveClosedLoopProfile
 * Author: Neil Balaskandarajah
 * Created on: 17/04/2020
 * Follow a profile in closed loop
 */

package commands;

import model.DriveLoop;
import model.motion.DriveProfile;
import model.motion.TrapezoidalProfile;
import util.Util;

public class DriveClosedLoopLinearProfile extends Command {
	//Attributes
	private DriveLoop loop; //Loop to control robot
	private DriveProfile traj; //trajectory to follow
	private int index; //index of the point in the trajectory
	private double tolerance; //how close to be to the goal to be considered done
	
	/**
	 * Follow a simple, symmetric trapezoidal motion profile
	 * @param loop Loop that controls robot
	 * @param totalDist Total distance of the profile
	 * @param accDist Acceleration and deceleration distance (must be less than totalDist)
	 * @param maxVel The top speed of the robot during the profile
	 * @param tolerance How close to be to the goal to be considered done
	 */
	public DriveClosedLoopLinearProfile(DriveLoop loop, double totalDist, double accDist, double maxVel, double tolerance) {
		this(loop, new TrapezoidalProfile(totalDist, accDist, maxVel), tolerance);
	} //end constructor
	
	/**
	 * Follow a simple, symmetric trapezoidal motion profile
	 * @param loop Loop that controls robot
	 * @param traj Trapezoidal motion profile
	 * @param tolerance How close to be to the goal to be considered done
	 */
	public DriveClosedLoopLinearProfile(DriveLoop loop, DriveProfile traj, double tolerance) {
		//set attributes
		this.loop = loop;
		this.traj = traj;
		this.robot = loop.getRobot();
		this.tolerance = tolerance + 1;
	} //end constructor

	/**
	 * Set the state of the loop and zero the index
	 */
	protected void initialize() {
		loop.setClosedLoopLinearProfileState(tolerance, traj.getTotalDist(), robot.getAveragePos());
		index = 0;			
	} //end initialize

	/**
	 * Send the trajectory points to the loop and update it
	 */
	protected void execute() {
		double time = index * Util.UPDATE_PERIOD; //would be getting actual time on real robot
		
		loop.updateClosedLoopLinearProfileState(traj.getLeftTrajPoint(time), traj.getRightTrajPoint(time));
		loop.onLoop();
		
		index++;
	} //end execute

	/**
	 * End the command when the trajectory time has passed
	 */
	protected boolean isFinished() {
		//better is finished
		return (loop.isDrivePIDAtTarget() ||
				loop.isRobotSlowerThanPercent(0.1)) &&
				(index * Util.UPDATE_PERIOD) > traj.getTotalTime();
	} //end isFinished
	
	protected void end() {
		Util.println(loop.getRobot().getAveragePos());
	}
	
	protected void timedOut() {}

} //end class
