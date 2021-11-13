/**
 * CommandGroup
 * Author: Neil Balaskandarajah
 * Created on: 03/01/2020
 * A group of commands to create routines
 */
package commands;

import java.util.ArrayList;
import java.util.HashMap;

import model.Pose;
import util.Util.ROBOT_KEY;
import util.Util;

public abstract class CommandGroup {
	//Attributes
	private ArrayList<Command> commands; //all commands to be run
	private boolean isRunning = false; //whether the command is running
	private ArrayList<Pose> poses; //poses of the robot
	private ArrayList<int[][]> curves; //curves the robot follows
	private ArrayList<HashMap<ROBOT_KEY, Object>> data; //data points of the robot
	
	protected boolean testing; //whether the CommandGroup is for testing
	private int passed; //number of tests passed
	
	/**
	 * Create a command group
	 */
	public CommandGroup() {
		initialize();
	} //end constructor
	
	/**
	 * Initialize the command group by setting the attributes
	 */
	private void initialize() {
		commands = new ArrayList<Command>();
		poses = new ArrayList<Pose>();
		curves = new ArrayList<int[][]>();
		data = new ArrayList<HashMap<ROBOT_KEY, Object>>();
		
		testing = this.getClass().getSimpleName().contains("Test"); //if test is in group name
		passed = 0;
	} //end initialize
	
	/**
	 * Add a command to the list
	 * @param c - command to be added
	 */
	public void add(Command c) {
		commands.add(c);
	} //end add
	
	/**
	 * Runs the entire command group
	 */
	public void run() {
		poses.clear();
		isRunning = true;
		
		//run each command and add its poses to the total list
		for (int i = 0; i < commands.size(); i++) {			
			commands.get(i).run(); //run the command
			
			//add curves if list exists and is not empty
			if (curves != null && !curves.isEmpty())
				curves.addAll(commands.get(i).getCurves());
			
			//sum the tests passed and failed
			if (testing) {
				if (commands.get(i).testing) {
					//increment the pass counter
					passed += commands.get(i).getPassed();
					
					//add the data if the command failed
					if (commands.get(i).getPassed() == Util.FAILED) {
						poses.addAll(commands.get(i).getPoses());
						data.addAll(commands.get(i).getData());
						
						//output the command that ran
						Util.println("Simulated command " + (i+1) + ": " + commands.get(i).getName());
					}
				}
				
			} else { //not testing
				poses.addAll(commands.get(i).getPoses());
				data.addAll(commands.get(i).getData());
				Util.println("Simulated command " + i + ": " + commands.get(i).getName());
			}
		}
		
		//output test information
		if (testing) {
			outputTestInformation();
		} //if
		
		isRunning = false;
	} //end start
	
	/**
	 * Return whether the command is running or not
	 * @return isRunning - whether the command is running or not
	 */
	public boolean isRunning() {
		return isRunning;
	} //end isRunning
	
	/**
	 * Get the poses from the command group 
	 * @return poses All poses from each command
	 */
	public ArrayList<Pose> getPoses() {
		return poses;
	} //end getPoses
	
	/**
	 * Get the curve from the command group 
	 * @return curve All curves from each command
	 */
	public ArrayList<int[][]> getCurves() {
		return curves;
	} //end getPoses
	
	/**
	 * Add a curve to be drawn
	 * @param curve array of curve points
	 */
	public void addCurve(int[][] curve) {
		curves.add(curve);
	} //end addCurve
	
	/**
	 * Get the data points from the robot
	 * @return list of data points from the robot
	 */
	public ArrayList<HashMap<ROBOT_KEY, Object>> getData() {
		return data;
	} //end getData
	
	/**
	 * Output all the test information to the screen
	 */
	private void outputTestInformation() {
		//get the number of commands being tested
		int numTests = 0;
		ArrayList<Integer> failIndices = new ArrayList<Integer>();
		
		for (int i = 0; i < commands.size(); i++) {
			numTests += commands.get(i).testing ? 1 : 0; //add if the command is testing
			
			if (commands.get(i).getPassed() == Util.FAILED) {
				failIndices.add((i - 2) / 4 + 1); //every fourth command starting
			} //if
		} //loop
		
		//output test information to the screen
		System.out.println();
		Util.println("");
		System.out.println("NUMBER OF TESTS: " + numTests);
		System.out.println("TESTS PASSED: " + passed);
		System.out.print("TESTS FAILED: " + (numTests - passed) + " [");	
		for (int i = 0; i < failIndices.size() - 1; i++) {
			System.out.print(failIndices.get(i) + ", ");
		} //loop
		System.out.print(failIndices.isEmpty() ? 0 : failIndices.get(failIndices.size() - 1));
		System.out.println("]");
	} //end outputTestInformation
} //end class
