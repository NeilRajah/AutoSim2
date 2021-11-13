/**
 * DriveToGoalDemo
 * Author: Neil Balaskandarajah
 * Created on: 07/01/2020
 * Demonstration for point to point driving
 */

package commands.routines;

import commands.CommandGroup;
import commands.DriveToGoal;
import main.AutoSim;
import model.DriveLoop;
import model.Point;
import util.Util;

public class DriveToGoalDemo extends CommandGroup {

	/**
	 * Drive between ten random points on the field
	 */
	public DriveToGoalDemo() {
		DriveLoop loop = AutoSim.driveLoop; //get the main loop
		loop.getRobot().setXY(new Point(30,30)); //starting point
		
		//Variables for the commands
		
		//X and Y values for ranges
		//height and width flipped because x and y are flipped
		double minX = 15, maxX = Util.FIELD_HEIGHT - minX;
		double minY = 15, maxY = Util.FIELD_WIDTH/2 - minY;
		
		//topSpeed, minSpeed and reverse
		double maxSpeed = loop.getRobot().getMaxLinSpeed();
		double topSpeed = maxSpeed;
		double minSpeed = 6;
		boolean reverse = false;
		int numPoints = 20;
		
		//add points to drive to
		for (int i = 0; i < numPoints; i++) {
			//create random x and y values to be within
			double x = Math.random() * (maxX - minX) + minX;
			double y = Math.random() * (maxY - minY) + minY;
			
			//set the speeds and reverse
			if (i == 0) { //first drive
//				topSpeed = maxSpeed;
				minSpeed = 6;
				reverse = false;
				
			} else if (i == numPoints-1) { //last drive
				minSpeed = 0;
				reverse = false;
				
			} else { //other points
//				topSpeed = Math.random() * (loop.getRobotClone().getMaxLinSpeed()/2) + loop.getRobotClone().getMaxLinSpeed()/2;
				reverse = Math.random() > 0.5;
				minSpeed = topSpeed/2;								
			} //if
			
			//add the command to the group
			add(new DriveToGoal(loop, new Point(x,y), 1, maxSpeed, minSpeed, reverse));
		} //loop
	} //end constructor
} //end class