/**
 * ConstantsTest
 * Author: Neil Balaskandarajah
 * Created on: 01/20/2020
 * Run the robot at various voltages to determine useful constants
 */
package commands.routines;

import commands.Command;
import commands.TimedVoltage;
import model.DriveLoop;
import util.Util;

public class ConstantsTest {
	//Attributes
	private DriveLoop loop; //drivetrain loop to update
	private double[] voltages;
	private double[] velocities;
	private double voltStep;
	private int index;
	
	public ConstantsTest(DriveLoop loop) {
		//set attributes
		this.loop = loop;

		//constants
		voltStep = 0.1;
		
		//variables
		voltages = new double[(int) Math.ceil(Util.MAX_VOLTAGE / voltStep) + 1];
		velocities = new double[voltages.length];
		index = 0;
	} //end constructor
	
	/**
	 * Run the tests and print out the constants
	 */
	public void execute() {
		calckV();
		calckA();
	} //end execute
	
	/**
	 * Calculate using commands and linear regression
	 */
	private void calckV() {
		for(double volt = 0; volt < Util.MAX_VOLTAGE; volt += voltStep) {
			//run the robot at a voltage for 5 seconds
			Command c = new TimedVoltage(loop, volt, 5);
			c.run();
			
			//save the velocities and the voltages
			double vel = loop.getRobot().getLinearVel();
			voltages[index] = volt;
			velocities[index] = vel;
			
			//print point values
//			Util.tabPrint(volt, vel);
			
			//reset the robot
			loop.getRobot().reset();
			index++;
		} //loop
		
		Util.kV_EMPIR = Util.regressedSlope(voltages, velocities);
//		Util.println("kV:", Util.kV_EMPIR);
	} //end calckV
	
	private void calckA() {
		
	}
} //end class
