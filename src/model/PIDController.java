/**
 * PIDController
 * Author: Neil Balaskandarajah
 * Created on: 03/01/2019
 * A simple PID controller that assumes regular loop intervals
 */
package model;

import util.Util;

public class PIDController {
	//Attributes
	//Configured
	private double kP; //proportionality constant
	private double kI; //integral constant
	private double kD; //derivative constant
	private double topSpeed; //max velocity of the robot

	//Calculated
	private double errorSum; //sum of all errors
	private double lastError; //previous error
	private boolean atTarget; //whether within epsilon bounds
	private double initPos; //initial position value

	/**
	 * Create a PID controller with gains
	 * @param p Proportionality constant
	 * @param i Integral constant
	 * @param d Derivative constant
	 * @param topSpeed Top speed the controller can output
	 */
	public PIDController(double p, double i, double d, double topSpeed) {
		//set attributes
		kP = p;
		kI = i;
		kD = d;
		this.topSpeed = topSpeed;
		
		errorSum = 0; //no error sum at beginning
		lastError = 0; //zero previous error at beginning
		initPos = 0; //initial position is zero
	} 

	//Attributes
	
	/**
	 * Return whether the error is within the epsilon bounds or not
	 * @return atTarget Whether controller is at target
	 */
	public boolean isDone() {
		return atTarget;
	} 

	/**
	 * Get the proportionality constant of the controller
	 * @return kP Proportionality constant
	 */
	public double getP() {
		return kP;
	}
	
	/**
	 * Set the proportionality constant
	 * @param kP New proportionality constant for PID controller
	 */
	public void setP(double kP) {
		this.kP = kP;
	}

	/**
	 * Get the integral constant of the controller
	 * @return kI Integral constant
	 */
	public double getI() {
		return kI;
	} 
	
	/**
	 * Set the integral constant
	 * @param kI New integration constant for PID controller
	 */
	public void setI(double kI) {
		this.kI = kI;
	}

	/**
	 * Get the derivative constant of the controller
	 * @return kD Derivative constant
	 */
	public double getD() {
		return kD;
	}
	
	/**
	 * Set the derivative constant
	 * @param kD New derivative constant for PID controller
	 */
	public void setD(double kD) {
		this.kD = kD;
	}
	
	/**
	 * Set the gains for the controller
	 * @param kP Proportionality constant
	 * @param kI Integration constant
	 * @param kD Derivative constant
	 */
	public void setGains(double kP, double kI, double kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}
	
	/**
	 * Reset the controller for the next set of calculations
	 */
	public void reset() {
		errorSum = 0;
		lastError = 0;
		atTarget = false;
	} 
	
	/**
	 * Set the initial position of the controller
	 * @param initPos Initial position
	 */
	public void setInitPos(double initPos) {
		this.initPos = initPos;
	}
	
	/**
	 * Get the initial position of the controller
	 * @return Initial position in inches
	 */
	public double getInitPos() {
		return initPos;
	}
	
	//Calculations
	
	/**
	 * Calculate the PID output based on the setpoint, current value and tolerance
	 * @param setpoint Setpoint to reach
	 * @param current Current value
	 * @param epsilon Range the controller can be in to be considered "at goal"
	 * @return Calculated PID output in volts
	 */
	public double calcPID(double setpoint, double current, double epsilon) {		
		//calculate error
		double error = setpoint - current;
		
		//update atTarget
		atTarget = Math.abs(error) <= epsilon;
		
		//proportional output
		double pOut = kP * error;
		
		//integral output
		errorSum += error;
		double iOut = kI * errorSum;
		
		//derivative output
		double dOut = 0;
		if (lastError != 0)
			dOut = kD * (error - lastError);
		lastError = error;
		
		//output is sum of each constant's output
		return pOut + iOut + dOut;
	} 
	
	/**
	 * Calculate a regulated PID output based on setpoint, current value and tolerance
	 * @param setpoint setpoint to reach
	 * @param current current value
	 * @param epsilon range the controller can be in to be considered "at goal"
	 * @param topSpeed magnitude of top of speed limit
	 * @param minSpeed magnitude of bottom of speed limit
	 * @return Regulated PID output in volts
	 */
	public double calcRegulatedPID(double setpoint, double current, double epsilon, double topSpeed, double minSpeed) {
		double output = calcPID(setpoint, current, epsilon);
		double topLimit = 12 * (Math.abs(topSpeed) / this.topSpeed);
		double botLimit = 12 * (Math.abs(minSpeed) / this.topSpeed);
		
		return Util.regulatedClamp(output, botLimit, topLimit);
	} 
	
	/**
	 * Return PID output based on a Distance and Velocity goal
	 * @param setpoint Goal distance
	 * @param current Current distance
	 * @param goalVel Goal velocity
	 * @param epsilon Distance range to be within to be considered done
	 * @return Feedback output based on distance and velocity goals
	 */
	public double calcDVPID(double setpoint, double current, double goalVel, double epsilon) {
		//distance error
		double error = setpoint - current;
		
		//update atTarget
		atTarget = Math.abs(current) >= initPos + setpoint - epsilon;
		
		//output based on distance error
		double pOut = kP * error;
		
		//output based on velocity difference from goal velocity
		double errorVel = (error - lastError) / (Util.UPDATE_PERIOD * 12); //convert to FPS
		double dOut = kD * (errorVel - goalVel);
						
		//set lastError for next loop
		lastError = error;
		
		//output is sum of both terms
		return pOut + dOut;
	}
} 
