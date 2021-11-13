/**
 * Motor
 * Author: Neil Balaskandarajah
 * Created on: 28/12/2019
 * Simple physics model of a standard DC motor
 */
package model;

import util.Util;

public class Motor {
	//Attributes
	//Configured	
	private double kStallTorque; //stall torque in Nm
	private double kStallCurrent; //stall current in Amps
	private double kFreeSpeed; //free speed in RPM
	private double kFreeCurrent; //free current in Amps
	
	//Calculated
	private double kResistance; //resistance across the motor
	private double kVoltage; //radians/second per volt applied to the motor
	private double kTorque; //Nm of torque per amp applied to the motor

	/**
	 * Create a motor with given parameters
	 * @param freeSpeed - free speed in RPM
	 * @param freeCurrent - free current in Amps
	 * @param stallTorque - stall torque in Nm
	 * @param stallCurrent - stall current in Amps
	 */
	public Motor(double freeSpeed, double freeCurrent, double stallTorque, double stallCurrent) {
		this.kFreeSpeed = freeSpeed;
		this.kFreeCurrent = freeCurrent;
		this.kStallTorque = stallTorque;
		this.kStallCurrent = stallCurrent;
		
		//compute other constants
		computeConstants();
	} //end constructor
	
	/**
	 * Create a motor with an array of parameters
	 * double[] parameters - free speed, free current, stall torque and stall current of the motor
	 */
	public Motor(double[] parameters) {
		this.kFreeSpeed = parameters[0];
		this.kFreeCurrent = parameters[1];
		this.kStallTorque = parameters[2];
		this.kStallCurrent = parameters[3];
		
		//compute other constants
		computeConstants();		
	} //end constructor

	/**
	 * Compute constants of the motor
	 */
	private void computeConstants() {
		//resistance across is total voltage divided by stall current
		kResistance = Util.MAX_VOLTAGE/kStallCurrent;
		
		//angular velocity of the motor per volt applied
		kVoltage = (kFreeSpeed * (Math.PI/30.0)) / (Util.MAX_VOLTAGE - (kResistance * kFreeCurrent) + Util.V_INTERCEPT);
		
		//Nm of torque per amp applied to the motor
		kTorque = kStallTorque/kStallCurrent;
	} //end computeConstants
	
	//Attributes
	
	/**
	 * Get the stall torque of the motor
	 * @return kStallTorque - stall torque of the motor in Nm
	 */
	public double getStallTorque() {
		return kStallTorque;
	} //end getStallTorque
	
	/**
	 * Get the stall current of the motor
	 * @return kStallCurrent - stall current of the motor in Amps
	 */
	public double getStallCurrent() {
		return kStallCurrent;
	} //end getStallCurrent
	
	/**
	 * Get the free speed of the motor 
	 * @return kFreeSpeed - free speed of the motor in radians/second
	 */
	public double getFreeSpeed() {
		return kFreeSpeed;
	} //end getFreeSpeed
	
	/**
	 * Get the free current of the motor
	 * @return kFreeCurrent - free current of the motor in Amps
	 */
	public double getFreeCurrent() {
		return kFreeCurrent;
	} //end getFreeCurrent
	
	/**
	 * Get the parameters of the motor in an array
	 * @return - array of motor parameters
	 */
	public double[] getParameters() {
		return new double[] {kFreeSpeed, kFreeCurrent, kStallTorque, kStallCurrent};
	} //end getParameters
	
	/**
	 * Get the resistance constant of the motor
	 * @return - resistance across the motor in Ohms
	 */
	public double getResistanceConstant() {
		return kResistance;
	} //end getFreeCurrent
	
	/**
	 * Get the voltage constant of the motor
	 * @return - radians/second per volt applied to the motor
	 */
	public double getVoltageConstant() {
		return kVoltage;
	} //end getFreeCurrent
	
	/**
	 * Get the torque constant of the motor
	 * @return - Nm of torque per amp applied to the motor
	 */
	public double getTorqueConstant() {
		return kTorque;
	} //end getTorqueConstant
} //end Motor
