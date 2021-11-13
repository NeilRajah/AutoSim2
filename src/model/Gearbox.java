/**
 * Gearbox
 * Author: Neil Balaskandarajah
 * Created on: 28/12/2019
 * Simple physics model for a gearbox powered by DC motors
 */
package model;

import util.Util;

public class Gearbox {
	//Attributes
	//Configured
	private double kGearRatio; //gear reduction of the gearbox 
	private Motor kMotor; //motor used in the gearbox
	private double kNumMotors; //number of motors in the gearbox
	
	//Calculated
	private double position; //amount the motor has turned in radians
	private double velocity; //velocity of the motor in radians per second
	private double acceleration; //acceleration of the motor in radians per second^2
	
	//Computed constants
	private double cVoltage; //constant proportional to voltage in torque calculations
	private double cVelocity; //constant proportional to velocity in torque calculations
	
	/**
	 * Create a gearbox with given parameters
	 * @param kGearRatio - gear reduction of the gearbox
	 * @param kMotor - motor used in the gearbox
	 * @param kNumMotors - number of motors in the gearbox
	 */
	public Gearbox(double gearRatio, Motor motor, double numMotors) {
		//set attributes
		this.kGearRatio = gearRatio;
		this.kMotor = motor;
		this.kNumMotors = numMotors;
		
		//calculate constants
		computeConstants();
		
		//set the kinematics to zero
		reset();
	} //end constructor
	
	/**
	 * Get the correct gear ratio from desired robot parameters
	 * @param motor Motor in gearbox
	 * @param wheelDiaIN Wheel diameter in inches
	 * @param topSpeedFPS Top robot speed in feet per second
	 * @return Ratio that fulfills above parameters
	 */
	public static double ratioFromTopSpeed(double[] motor, double wheelDiaIN, double topSpeedFPS) {
		return (Math.PI * motor[0] * wheelDiaIN) / (720 * topSpeedFPS);
	} //end ratioFromTopSpeed
	
	/**
	 * Safely get a copy of this gearbox
	 * @return - identical copy of this gearbox
	 */
	public Gearbox clone() {
		return new Gearbox(kGearRatio, new Motor(kMotor.getParameters()), kNumMotors);
	} //end clone
	
	/**
	 * Compute constants of the gearbox
	 */
	private void computeConstants() {
		//constants used in torque calculation
		cVoltage = (kGearRatio * kMotor.getTorqueConstant() * kNumMotors) / (kMotor.getResistanceConstant());
		
		cVelocity = -(kGearRatio * kGearRatio * kMotor.getTorqueConstant() * kNumMotors) / 
						(kMotor.getResistanceConstant() * kMotor.getVoltageConstant());
	} //end computeConstants
	
	//Attributes
	
	/**
	 * Get the gear ratio of the gearbox
	 * @return kGearRatio - reduction of the gearbox
	 */
	public double getGearRatio() {
		return kGearRatio;
	} //end getGearRatio
	
	/**
	 * Get the number of motors in the gearbox
	 * @return kNumMotors - number of motors in the gearbox
	 */
	public double getNumMotors() {
		return kNumMotors;
	} //end getNumMotors
	
	/**
	 * Get the parameter array for the motors in the gearbox
	 * @return - array of motor parameters
	 */
	public double[] getMotorParameters() {
		return kMotor.getParameters();
	} //end getMotorName
	
	//Kinematics
	
	/**
	 * Get the position of the gearbox
	 * @return position - position of the gearbox in radians
	 */
	public double getPos() {
		return position;
	} //end getPos
	
	/**
	 * Get the velocity of the gearbox
	 * @return velocity - velocity of the gearbox in radians
	 */
	public double getVel() {
		return velocity;
	} //end getVel
	
	/**
	 * Zero the velocity of the gearbox
	 */
	public void zeroVel() {
		velocity = 0;
	} //end zeroVel
	
	/**
	 * Get the acceleration of the gearbox
	 * @return acceleration - acceleration of the gearbox in radians
	 */
	public double getAcc() {
		return acceleration;
	} //end getAcc
	
	//Dynamics
	
	/**
	 * Calculate the torque the motor provides given a specified voltage
	 * @param voltage - voltage applied to the motor
	 * @return - calculated torque based on voltage
	 */
	public double calcTorque(double voltage) {
		//torque is proportional to voltage applied and angular velocity of the motor		
		return cVoltage * voltage + cVelocity * velocity;
	} ///end calcTorque
	
	/**
	 * Update the position and velocity of the gearbox by assuming constant acceleration in a timestamp
	 * @param acceleration - acceleration of the gearbox for this timestamp
	 */
	public void update(double acceleration) {
		this.acceleration = acceleration; //save the acceleration to memory
		
		//v2 = v1 + at
		this.velocity += this.acceleration * Util.UPDATE_PERIOD; 
		
		//d2 = d1 + vt + 0.5at^2
		this.position += this.velocity * Util.UPDATE_PERIOD + 0.5 * this.acceleration * Util.UPDATE_PERIOD * Util.UPDATE_PERIOD;
	} //end update
	
	/**
	 * Reset the kinematics of the gearbox
	 */
	public void reset() {
		position = 0;
		velocity = 0;
		acceleration = 0;
	} //end reset
} //end Gearbox
