/**
 * Jerk Profile
 * Author: Neil Balaskandarajah
 * Created on: 13/01/2020
 * A 1-dimensional 5-segment motion profile
 */

package model.motion;

import util.Util;

public class JerkProfile extends DriveProfile {
	//Attributes
	private double dT; //total distance in inches
	private double dA; //acceleration distance in inches
	private double dD; //deceleration distance in inches
	private double vM; //cruising velocity in ft/s
	private double tT; //total time
	private double tA; //acceleration time
	private double tD; //deceleration time
	
	private double jA; //jerk during acceleration phase
	private double jD; //jerk during deceleration phase
	
	/**
	 * Create a symmetric jerk profile
	 * @param totalDist Total distance to travel in inches
	 * @param accDist Distance to accelerate AND decelerate over in inches
	 * @param maxVel Maximum velocity to reach when cruising in ft/s
	 */
	public JerkProfile(double totalDist, double accDist, double maxVel) {
		this.dT = totalDist;
		this.dA = accDist;
		this.dD = accDist;
		this.vM = maxVel * 12; //convert to in/s
				
		computeConstants();
		fillProfiles();
	} //end computeConstants

	/**
	 * Compute all profile constants
	 */
	protected void computeConstants() {
		//jerk constants
//		jA = vM / (0.75 * dA * dA); //v / 0.75d^2
//		jD = -vM / (0.75 * dD * dD); //v / 0.75d^2
				
//		double fudge = 1.0; //4.385 for 100, 32, 12, based on d and v
//		jA = (fudge * vM * vM) / dA;
//		jD = -(fudge * vM * vM) / dD;
		
		//times
		this.tA = (2 * dA) / vM; //2d / v
		this.tD = (2 * dD) / vM; //2d / v
		double tC = (dT - dA - dD) / vM; //cruise dist / cruise vel
		this.tT = tA + tC + tD;
		
		//jerk constants
//		double s = 1;
//		jA = (s * vM) / (tA * tA);
//		jD = -(s * vM) / (tD * tD);
//		jA = (4 * vM) / (tA * tA);
//		jD = -(4 * vM) / (tD * tD);
//		double t = tA / 2;
//		jA = (6 / Math.pow(t, 3)) * (2.5 * vM * t - dA);
//		jD = -(6 / Math.pow(t, 3)) * (2.5 * vM * t - dD);
		
		//vel
//		jA = (4 * vM) / (tA * tA);
//		double s = 1.0;
		jA = (4 * vM) / Math.pow(tA, 2); //4v / t^2
		jD = -jA;
		
		//profile
		this.totalTime = tT;
	} //end computeConstants
	
	/**
	 * Fill the left and right sides using a 5-segment jerk profile function
	 */
	protected void fillProfiles() {
		double p = 0; //position
		double v = 0; //velocity
		double a = 0; //acceleration
		double dt = Util.UPDATE_PERIOD; //time interval
		
		//time boundaries for each phase
		double t1 = tA / 2;
		double t2 = tA;
		double t3 = tT - tD;
		double t4 = tT - tD / 2;
		
		double prevV = 0;
		
		int loops = (int) Math.ceil(tT / dt); //number of timesteps
		this.size = loops;
		
		//calculate like piecewise function instead of numerically
		for (int i = 1; i <= loops; i++) {
			double t = i * dt;
			
			if (t <= t1) { //increasing acceleration
				v = 0.5 * jA * Math.pow(t, 2);
			} else if (t > t1 && t <= t2) { //decreasing acceleration
				v = -0.5 * jA * Math.pow(t - t2, 2) + jA * Math.pow(t1, 2);
			} else if (t > t2 && t <= t3) { //cruising
				v = vM;
			} else if (t > t3 && t <= t4) { //increasing deceleration
				v = vM + 0.5 * jD * Math.pow(t - t3, 2);
			} else { //decreasing deceleration
				v = -0.5 * jD * Math.pow(t - tT, 2);
			} //if
			
			a = (v - prevV) / dt;
			p += v * dt;
			prevV = v;
			
			this.leftProfile.add(new double[] {p, v / 12, a}); //vel back to ft/s
			this.rightProfile.add(new double[] {p, v / 12, a});
		} //loop
	} //end fillProfiles
	
} //end JerkProfile
