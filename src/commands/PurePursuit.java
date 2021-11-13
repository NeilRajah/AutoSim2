/**
 * PurePursuit
 * Author: Neil Balaskandarajah
 * Created on: 17/05/2020
 * Follow a set of goals using the pure pursuit algorithm
 */

package commands;

import graphics.Environment;
import model.DriveLoop;
import model.Point;
import model.DriveLoop.STATE;
import model.motion.PurePursuitController;

public class PurePursuit extends Command {
	//Attributes
	private DriveLoop loop; //drivetrain state machine to control
	private PurePursuitController ppc; //Pure pursuit controller to follow
	private Point[] goals;
	
	/**
	 * Create a PurePursuit command
	 * @param loop Drivetrain loop
	 * @param ppc Pure Pursuit controller
	 */
	public PurePursuit(DriveLoop loop, Point[] goals) {
		//set attributes
		this.loop = loop;
		this.goals = goals;
		this.robot = loop.getRobot();
		this.ppc = loop.getPurePursuitController();
	} //end constructor

	/**
	 * Set up the loop and the graphics
	 */
	protected void initialize() {
		loop.setState(STATE.PURE_PURSUIT);
		ppc.setWaypoints(goals);
		
		//graphics
		Environment.getInstance().setWaypoints(goals);
		this.robot.setLookahead(ppc.getLookahead());
		this.robot.setGoalPoint(ppc.getGoal());
	} //end initialize

	/**
	 * Update the controller and execute the loop
	 */
	protected void execute() {
		loop.updatePurePursuitState(loop.getRobot().getPose(), loop.getRobot().getLinearVel());
		loop.onLoop();
		
		//graphics
		this.robot.setGoalPoint(ppc.getGoal());
	} //end execute

	/**
	 * Stop when the controller has arrived
	 */
	protected boolean isFinished() {
		return ppc.isArrived();
	} //end isFinished

	protected void end() {}

	protected void timedOut() {}
} //end class
