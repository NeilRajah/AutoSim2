package model.motion;

import java.util.ArrayList;

import util.Util;

public abstract class DriveProfile {
	//Attributes
	protected ArrayList<double[]> leftProfile = new ArrayList<double[]>();
	protected ArrayList<double[]> rightProfile = new ArrayList<double[]>();
	protected ArrayList<Double> headings = new ArrayList<Double>();
	protected double totalTime;
	protected int size;
	protected double length;
	
	/**
	 * Compute all profile constants
	 */
	protected abstract void computeConstants();
	
	/**
	 * Fill the profile with trajectory points
	 */
	protected abstract void fillProfiles();
	
	/**
	 * Return left trajectory point
	 * @param time Time in the profile the point should be run at
	 * @return Trajectory point at the corresponding time
	 */
	public double[] getLeftTrajPoint(double time) {
		int index = Math.max(0, Math.min((int) (time / Util.UPDATE_PERIOD), leftProfile.size() - 1));
		return leftProfile.get(index);
	} //end getLeftTrajPoint
	
	/**
	 * Return left trajectory point
	 * @param index Index in the profile
	 * @return Trajectory point at the corresponding index
	 */
	public double[] getLeftTrajPoint(int index) {
		return leftProfile.get(index);
	} //end getLeftTrajPoint

	/**
	 * Return right trajectory point
	 * @param time Time in the profile the point should be run at
	 * @return Trajectory point at the corresponding time
	 */
	public double[] getRightTrajPoint(double time) {
		int index = Math.max(0, Math.min((int) (time / Util.UPDATE_PERIOD), rightProfile.size() - 1));
		return rightProfile.get(index);
	} //end getRightTrajPoint
	
	/**
	 * Return right trajectory point
	 * @param index Index in the profile
	 * @return Trajectory point at the corresponding index
	 */
	public double[] getRightTrajPoint(int index) {
		return rightProfile.get(index);
	} //end getRightTrajPoint
	
	/**
	 * Return heading point
	 * @param time Time in the profile 
	 * @return Heading at the corresponding time
	 */
	public double getHeading(double time) {
		return headings.get((int) (time / Util.UPDATE_PERIOD));
	} //end getLeftTrajPoint

	/**
	 * Return heading point
	 * @param index Index in the list
	 * @return Heading at the corresponding index
	 */
	public double getHeading(int index) {
		return headings.get(index);
	} //end getHeading
	
	/**
	 * Return total time of the profile
	 * @return Total time of the profile
	 */
	public double getTotalTime() {
		return totalTime;
	} //end getTotalTime
	
	/**
	 * Get the size of the profile
	 * @return Size of the lists
	 */
	public int getSize() {
		return size;
	} //end getSize
	
	/**
	 * Fill the entire headings list with a single heading
	 * @param heading Heading to follow
	 */
	public void fillHeadings(double heading) {
		for (int i = 0; i < headings.size(); i++) 
			headings.set(i, heading);
	} //end fillHeadings
	
	/**
	 * Get the total distance of the profile
	 * @return Final distance in list
	 */
	public double getTotalDist() {
		return leftProfile.get(leftProfile.size() - 1)[0];
	} //end getTotalDist
	
	/**
	 * Get the left velocities (ft/s)
	 * @return Left wheel velocities as an array
	 */
	public double[] getLeftVelocities() {
		double[] leftVel = new double[this.size];
		
		for (int i = 0; i < this.size; i++) 
			leftVel[i] = leftProfile.get(i)[1];
		
		return leftVel;
	} //end getLeftVelocities
	
	/**
	 * Get the right velocities (ft/s)
	 * @return Right wheel velocities as an array
	 */
	public double[] getRightVelocities() {
		double[] rightVel = new double[this.size];
		
		for (int i = 0; i < this.size; i++) 
			rightVel[i] = rightProfile.get(i)[1];
		
		return rightVel;
	} //end getRightVelocities
	
	/**
	 * Get the times the profile occurs at
	 * @return Times each setpoint runs at in seconds as an array
	 */
	public double[] getTimes() {
		ArrayList<Double> times = new ArrayList<Double>();
		
		for (double time = 0; time < this.totalTime; time += Util.UPDATE_PERIOD)
			times.add(time);
		
		return Util.doubleListToArray(times);
	} //end getTimes
} //end class
