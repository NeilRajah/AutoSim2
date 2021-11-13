/*
 * Command
 * Author: Neil Balaskandarajah
 * Created on: 03/01/2020
 * Interface for running commands
 */
package commands;

import java.util.ArrayList;
import java.util.HashMap;

import model.Point;
import model.Pose;
import model.Robot;
import util.Util;
import util.Util.ROBOT_KEY;

public abstract class Command implements Runnable {
	//Attributes
	private boolean isRunning; //whether the command is running or not
	private boolean isTimedOut; //whether the command times out or not
	private ArrayList<HashMap<ROBOT_KEY, Object>> data; //robot data
	private ArrayList<Pose> poses; //list of robot poses
	private int maxIterations; //maximum number of iterations command can have
	
	protected ArrayList<int[][]> curves; //list of curves
	protected Robot robot; //robot being commanded
	protected String name; //name of the command for debugging
	protected boolean testing; //whether the command is being tested or not
	protected int passed; //whether the test has passed
	protected int printCounter; //used when testing to not fill the console
	
	/**
	 * Runs once before command starts
	 */
	protected abstract void initialize();
	
	/**
	 * Runs while isFinished is false
	 */
	protected abstract void execute();
	
	/**
	 * Checks whether the command isFinished
	 */
	protected abstract boolean isFinished();
	
	/**
	 * Ends the command
	 */
	protected abstract void end();
	
	/**
	 * Run when the command is timed out
	 */
	protected abstract void timedOut();
	
	/**
	 * Test function that prints whether the command has passed or failed (meant to be overwritten)
	 */
	protected void test() {}
	
	/**
	 * Initialize all behind-the-scenes values for the Command
	 */
	private void initCommand() {
		data = new ArrayList<HashMap<ROBOT_KEY, Object>>(); //list of data points
		poses = new ArrayList<Pose>(); //robot poses
		curves = new ArrayList<int[][]>(); //bezier curves
		passed = Util.INITIALIZED; //default not passed value
		printCounter = 0;
		
		//set the max iterations if it has not been set
		if (maxIterations == 0) { //zero is default int value
			this.maxIterations = (int) (10 * (1.0 / Util.UPDATE_PERIOD)); //10 seconds
		} //if
		
		//set the name
		this.name = this.getClass().getSimpleName();
		robot.setCommandName(this.name);
	} //end initCommand
	
	/**
	 * Runs a command until finished
	 */
	public void run() {
		//initialize the command
		this.initCommand();
		isRunning = true;
		isTimedOut = false;
		this.initialize();
		int iterations = 0;
		
		//execute the command until it is finished or timed out
		while (!this.isFinished() && !this.isTimedOut) {
			this.execute(); //run the command
			
			//add the robot information to the respective collections
			poses.add(robot.getPose());
			data.add(robot.getData());

			//loop the number of iterations
			iterations++;
			
			//the command is timed out if there are too many iterations
			isTimedOut = iterations >= maxIterations;
		} //loop
		
		//end the command
		this.end();	
		isRunning = false;
		isTimedOut = false;
		
		//run the test function at the end
		if (testing) {
			this.test();
		} //if
	} //end run
	
	/**
	 * Check if the command is running
	 * @return isRunning - whether the command is running or not
	 */
	public boolean isRunning() {
		return isRunning;
	} //end isRunning
	
	/**
	 * Get the list of poses 
	 * @return list of poses from robot
	 */
	public ArrayList<Pose> getPoses() {
		return poses;
	} //end getPoses
	
	/**
	 * Get the curve
	 * @return - list of curves added from command
	 */
	public ArrayList<int[][]> getCurves() {
		return curves;
	} //end getCurves
	
	/**
	 * Get the data points of the robot at each pose
	 * @return list of HashMaps of data points
	 */
	public ArrayList<HashMap<ROBOT_KEY, Object>> getData() {
		return data;
	} //end data
	
	/**
	 * Get the name of the command
	 * @return String name of the command
	 */
	public String getName() {
		return name;
	} //end getName
	
	/**
	 * Set the timeout in seconds
	 * @param timeout Command timeout in seconds
	 */
	public void setTimeout(double timeout) {
		maxIterations = (int) (timeout * (1.0 / Util.UPDATE_PERIOD));
	} //end setTimeout
	
	/**
	 * Enable the test function
	 */
	public void enableTesting() {
		this.testing = true;
	} //end setTesting
	
	/**
	 * Get the test result
	 * @return passed
	 */
	public int getPassed() {
		return passed;
	} //end getPassed
} //end class