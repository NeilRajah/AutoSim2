/**
 * DriveToGoalTest
 * Author: Neil Balaskandarajah
 * Created on: 14/03/2020
 * Test command group for DriveToGoal
 */

package commands.routines;

import java.util.ArrayList;

import commands.Command;
import commands.CommandGroup;
import commands.DriveToGoal;
import commands.SetPose;
import commands.Wait;
import main.AutoSim;
import model.DriveLoop;
import util.Util;;

public class DriveToGoalTest extends CommandGroup {
	//Attributes
	private DriveLoop loop; //loop to control
	private double startX; //starting x position
	private double startY; //starting y position
	
	/**
	 * All test cases
	 */
	public DriveToGoalTest() {
		loop = AutoSim.driveLoop; //get the main loop
		
		//set the starting (x,y) position of the robot
		startX = Util.FIELD_HEIGHT / 2;
		startY = Util.FIELD_WIDTH / 2;
		double dist = 75;
		
		int[] testIndices = new int[]{-1};
		
		//set the robot to its starting pose (heading = 0)
		resetRobot();
		
		//List of all tests
		ArrayList<DriveToGoal> tests = new ArrayList<DriveToGoal>();
		
		//Forward Straight ahead
		tests.add(new DriveToGoal(loop, startX, startY + dist, 1, 12, 2, false));
		
		//Forward Straight behind
		tests.add(new DriveToGoal(loop, startX, startY - dist, 1, 12, 2, false));
		
		//Forward Left
		tests.add(new DriveToGoal(loop, startX - dist, startY, 1, 12, 2, false));
		
		//Forward Right 
		tests.add(new DriveToGoal(loop, startX + dist, startY, 1, 12, 2, false));
		
		//Reverse Straight ahead %
		tests.add(new DriveToGoal(loop, startX, startY + dist, 1, 12, 2, true));
		
		//Reverse Straight behind %
		tests.add(new DriveToGoal(loop, startX, startY - dist, 1, 12, 2, true));
		
		//Reverse Left %
		tests.add(new DriveToGoal(loop, startX - dist, startY, 1, 12, 2, true));
		
		//Reverse Right
		tests.add(new DriveToGoal(loop, startX + dist, startY, 1, 12, 2, true));
		
		//Forward + + 
		tests.add(new DriveToGoal(loop, startX + dist, startY + dist, 1, 12, 2, false));
		
		//Forward + -
		tests.add(new DriveToGoal(loop, startX + dist, startY - dist, 1, 12, 2, false));
		
		//Forward - -
		tests.add(new DriveToGoal(loop, startX - dist, startY - dist, 1, 12, 2, false));

		//Forward - +
		tests.add(new DriveToGoal(loop, startX - dist, startY + dist, 1, 12, 2, false));
		
		//Reverse + + 
		tests.add(new DriveToGoal(loop, startX + dist, startY + dist, 1, 12, 2, true));
		
		//Reverse + -
		tests.add(new DriveToGoal(loop, startX + dist, startY - dist, 1, 12, 2, true));
		
		//Reverse - -
		tests.add(new DriveToGoal(loop, startX - dist, startY - dist, 1, 12, 2, true));

		//Reverse - +
		tests.add(new DriveToGoal(loop, startX - dist, startY + dist, 1, 12, 2, true));
		
		//add all tests to the command group
		for (int i = 0; i < tests.size(); i++) {
			//add testing parameters
			Command test = tests.get(i);
			test.setTimeout(2.0);
			test.enableTesting();
			
			//add to group
			this.add(test);
			
			//move robot back to starting configuration
			resetRobot();
		} //loop
	} //end constructor
	
	/**
	 * Reset the robot to a starting configuration before each test
	 */
	private void resetRobot() {
		this.add(new Wait(loop, 0.5));
		this.add(new SetPose(loop, startX, startY));
		this.add(new Wait(loop, 0.5));
	} //end resetRobot
} //end class
