/**
 * Robot
 * Author: Neil Balaskandarajah
 * Created on: 28/12/2019
 * A simple physics model for a differential drive robot
 */

package model;

import java.awt.Color;
import java.util.HashMap;

import main.AutoSim;
import util.Util;
import util.Util.ROBOT_KEY;

public class Robot {
	//Attributes
	//Configured
	private double kMass; //mass of the robot in pounds
	private double kLength; //length of the robot in inches
	private double kWidth; //width of the robot in inches
	private Gearbox leftGearbox; //left gearbox
	private Gearbox rightGearbox; //right gearbox
	
	//Kinematics
	private double averagePos; //position of robot
	private double angularVel; //angular speed of the robot
	private double linearVel; // translational speed of robot
	
	//Pose
	private double heading; //heading of the robot in degrees
	private double yaw; //yaw of the robot
	private Point point; //point representing robot's position on the field
	
	//Graphics
	private Color color; //color of the robot
	private String commandName; //name of the command
	private Point goalPoint; //used for graphics
	private double lookahead; //lookahead circle radius
	
	//Controller
	private double pidOut; //output from a PID controller
	
	//Computed constants
	private double kWheelRad; //wheel radius in meters
	private double kMOI; //moment of inertia in kg*m^2
	private double kPivotArm; //distance from robot center to wheel in meters (used in torque calculations)
	private double fP; //constant used in gearbox acceleration calculation
	private double fM; //constant used in gearbox acceleration calculation
	private double maxLinSpeed; //top linear speed of the robot
	private double maxAngSpeed; //top angular speed of the robot
	
	/**
	 * Create a robot with base parameters
	 * double kWheelDia Wheel diameter in inches
	 * double kMass Mass of the robot in pounds
	 * double kLength Length of the robot in inches
	 * double kWidth Width of the robot in inches
	 * Gearbox gearbox Drive gearboxes
	 */
	public Robot(double wheelDia, double mass, double length, double width, Gearbox gearbox) {
		//set attributes in metric units
		this.kMass = mass * Util.LBS_TO_KG; //convert to kg
		this.kLength = length * Util.INCHES_TO_METERS; //convert to m
		this.kWidth = width * Util.INCHES_TO_METERS; //convert to m
		this.kWheelRad = wheelDia/2 * Util.INCHES_TO_METERS; //convert radius to m
		
		//create gearboxes
		this.leftGearbox = new Gearbox(gearbox.getGearRatio(), new Motor(gearbox.getMotorParameters()),gearbox.getNumMotors());
		this.rightGearbox = new Gearbox(gearbox.getGearRatio(), new Motor(gearbox.getMotorParameters()),gearbox.getNumMotors());
		
		//compute constants
		computeConstants();
		
		//set values to zero
		reset();
	} 
	
	/**
	 * Safely get a copy of this Robot
	 * @return Identical copy of this Robot 
	 */
	public Robot clone() {
		//convert the constants to imperial
		double wheelDia = kWheelRad * 2 / Util.INCHES_TO_METERS;
		double mass = kMass / Util.LBS_TO_KG;
		double length = kLength / Util.INCHES_TO_METERS;
		double width = kWidth / Util.INCHES_TO_METERS;
		
		//left gearbox identical to right gearbox, doesn't matter which is used
		return new Robot(wheelDia, mass, length, width, leftGearbox.clone());
	} 
	
	/**
	 * Compute constants of the robot
	 */
	private void computeConstants() {
		//robot simplified to rectangular slab rotating about axis through center perpendicular to surface
		kMOI = kMass * (kLength * kLength + kWidth * kWidth) / 12;
		
		//half the width of the robot
		kPivotArm = kWidth/2;
		
		//constants used in force calculations
		fM = (1/kMass) - (kPivotArm * kPivotArm)/kMOI;
		fP = (1/kMass) + (kPivotArm * kPivotArm)/kMOI;
		
		//top speeds
		maxLinSpeed = (Math.PI * leftGearbox.getMotorParameters()[0] * kWheelRad)/
					(360 * leftGearbox.getGearRatio() * Util.INCHES_TO_METERS); //ft/s
		maxAngSpeed = (24 * maxLinSpeed * Util.INCHES_TO_METERS) / (kWidth); //rad/s
	} 
	
	/**
	 * Reset the gearboxes, pose and kinematics of the robot
	 */
	public void reset() {
		averagePos = 0;
		heading = 0;
		point = new Point(0,0);
		leftGearbox.reset();
		rightGearbox.reset();
		angularVel = 0;
		linearVel = 0;
		color = Color.YELLOW;
		commandName = "";
	} //end reset
	
	/**
	 * Set the robot into waiting state
	 */
	public void setToWait() {
		//reset speeds
		angularVel = 0;
		linearVel = 0;
		leftGearbox.zeroVel();
		rightGearbox.zeroVel();
		
		color = Color.yellow;
	} 
	
	//Kinematics
	
	/**
	 * Get the average distance travelled by robot
	 * @return Average distance travelled by robot in inches
	 */
	public double getAveragePos() {
		return averagePos;
	} 
	
	/**
	 * Get the linear velocity of the robot
	 * @return Linear speed of the robot in inches per second
	 */
	public double getLinearVel() {
		return linearVel;
	} 
	
	/**
	 * Get the angular velocity of the robot
	 * @return Angular speed of the robot in radians per second
	 */
	public double getAngularVel() {
		return angularVel;
	} 
	
	/**
	 * Get the max linear speed of the robot
	 * @return Top linear speed of the robot in ft/s
	 */
	public double getMaxLinSpeed() {
		return maxLinSpeed;
	} 
	
	/**
	 * Get the max angular speed of the robot
	 * @return Top angular speed of the robot in rad/s
	 */
	public double getMaxAngSpeed() {
		return maxAngSpeed;
	} 
	
	/**
	 * Get the width of the robot in inches
	 * @return Width of robot in inches
	 */
	public double getWidthInches() {
		return kWidth / Util.INCHES_TO_METERS;
	} 
	
	/**
	 * Get the length of the robot in inches
	 * @return length of robot in inches
	 */
	public double getLengthInches() {
		return kLength / Util.INCHES_TO_METERS;
	} 
	
	/**
	 * Get the position of the left side of the drive
	 * @return Left wheel position in inches
	 */
	public double getLeftPos() {
		return leftGearbox.getPos() * kWheelRad / Util.INCHES_TO_METERS;
	} 
	
	/**
	 * Get the position of the right side of the drive
	 * @return Right gearbox position in inches
	 */
	public double getRightPos() {
		return rightGearbox.getPos() * kWheelRad / Util.INCHES_TO_METERS;
	} 
	
	/**
	 * Check if the robot's speed is lower than a percent of its top speed
	 * @param percent Percent of the top speed to be under between -1 and 1
	 * @return True if under, false if equal or over
	 */
	public boolean isSlowerThanPercent(double percent) {
		return Math.abs(linearVel) < percent * maxLinSpeed;
	} 
	
	//Pose
	
	/**
	 * Get the coordinates of the robot
	 * @return (x,y) values of the robot as a Point
	 */
	public Point getPoint() {
		return new Point(point.getX(), point.getY());
	} 
	
	/**
	 * Get the x position of the robot
	 * @return X position of robot
	 */
	public double getX() {
		return point.getX();
	} 
	
	/**
	 * Get the y position of the robot
	 * @return Y position of robot
	 */
	public double getY() {
		return point.getY();
	} 
	
	/**
	 * Set the (x,y) coordinates of the robot
	 * @param p New (x,y) coordinates of the robot
	 */
	public void setXY(Point p) {
		point = new Point(p.getX(), p.getY());
	} 
	
	/**
	 * Get the pose of the robot
	 * @return Current pose of the robot
	 */
	public Pose getPose() {
		return new Pose(new Point(point.getX(), point.getY()), heading, 
						new Color(color.getRed(), color.getGreen(), color.getBlue()));	
	} 
	
	/**
	 * Get the heading of the robot
	 * @return Heading of the robot in radians
	 */
	public double getHeading() {
		return heading;
	} 
	
	/**
	 * Set the heading of the robot
	 * @param Heading in radians for the robot to point at
	 */
	public void setHeading(double heading) {
		this.heading = heading;
	} 
	
	/**
	 * Set the heading of the robot in degrees
	 * @param Heading in degrees to set the robot to
	 */
	public void setHeadingDegrees(double heading) {
		this.heading = Math.toRadians(heading);
	} 
	
	/**
	 * Get the yaw of the robot in degrees
	 * @param Robot yaw in degrees
	 */
	public double getYaw() {
		yaw = Math.toDegrees(heading) % 360; //convert heading to degrees
		return yaw;
	}

	/**
	 * Set the yaw (and heading) of the robot
	 * @param Yaw value to set the robot yaw to
	 */
	public void setYaw(double yaw) {
		this.yaw = yaw;
		this.heading = Math.toRadians(yaw);
	}

	//Dynamics
	
	/**
	 * Update the pose of the robot given voltages applied over an interval
	 * @param leftVoltage Voltage applied to left gearbox
	 * @param rightVoltage Voltage applied to right gearbox
	 */
	public void update(double leftVoltage, double rightVoltage) {
		//clamp the voltages between min and max voltage values
		leftVoltage = Util.clampNum(leftVoltage, -Util.MAX_VOLTAGE, Util.MAX_VOLTAGE);
		rightVoltage = Util.clampNum(rightVoltage, -Util.MAX_VOLTAGE, Util.MAX_VOLTAGE);
		
		//calculate force exerted by each gearbox on robot
		double leftForce = leftGearbox.calcTorque(leftVoltage) / kWheelRad;
		double rightForce = rightGearbox.calcTorque(rightVoltage) / kWheelRad;
		
		//calculate each side's acceleration 
		updateGearboxes(leftForce, rightForce);
		
		//update the speeds of the robot
		updateSpeeds();
		
		//update the pose of the robot
		updatePose();
		
		//update the graphics of the robot
		updateGraphics();
	}
	
	/**
	 * Calculate the accelerations of each side of the drive
	 * @param leftTorque Torque of the left gearbox
	 * @param rightTorque Torque of the right gearbox
	 */
	private void updateGearboxes(double leftForce, double rightForce) {
		//calculate accelerations using forces
		double leftAcc = (fP * leftForce + fM * rightForce) / kWheelRad; //convert from m/s^2 to rad/s^2
		double rightAcc = (fM * leftForce + fP * rightForce) / kWheelRad;
				
		//update the position and velocity of the gearbox
		leftGearbox.update(leftAcc);
		rightGearbox.update(rightAcc);
	} 
	
	/**
	 * Calculate the angular velocity of the robot and update its heading
	 */
	private void updateSpeeds() {
		//angular velocity based on left, right wheel velocities and robot width
		angularVel = (kWheelRad / (2 * kPivotArm)) * (rightGearbox.getVel() - leftGearbox.getVel());
		
		//linear speed is average of two velocities
		linearVel = kWheelRad / Util.INCHES_TO_METERS / 12 * (rightGearbox.getVel() + leftGearbox.getVel()) / 2;
	} 
	
	/**
	 * Update the pose of the robot
	 */
	private void updatePose() {
		//calculate the magnitude of displacement in time interval
		double leftDisp = leftGearbox.getPos(); 
		double rightDisp = rightGearbox.getPos();
		double disp = (leftDisp + rightDisp) / 2;
		
		//update average position and coordinates
		double newPos = disp * kWheelRad / Util.INCHES_TO_METERS;
		point.translate(newPos - averagePos, heading);
		averagePos = newPos;
		
		//update heading
		heading += angularVel * Util.UPDATE_PERIOD;
	} 
	
	//Graphics
	
	/**
	 * Get the color of the robot
	 * @return Color based on direction scaled to speed
	 */
	public Color getColor() {
		return color;
	} 
	
	/**
	 * Set the color of the robot
	 * @param c New Color for the robot
	 */
	public void setColor(Color c) {
		this.color = c;
	} 
	
	/**
	 * Update the robot graphics
	 */
	private void updateGraphics() {
		updateColor();
	} 
	
	/**
	 * Update the color of the robot
	 */
	private void updateColor() {
		//percentage of top speed
		double modifier = Math.min(1, Math.abs(linearVel) / maxLinSpeed);
		int val = 127 + (int) (128 * modifier);
		
		if (linearVel > 0) { //moving forward
			color = new Color(0, val, 0); //green
			
		} else if (linearVel < 0) { //reversing
			color = new Color(val, 0, 0); //red
			
		} else { //not moving
			color = Color.YELLOW;
		} 
	} 
	
	/**
	 * Get the width of the robot in pixels
	 * @return Width of robot in pixels
	 */
	public int getWidthPixels() {
		return (int) ((kWidth * AutoSim.PPI) / Util.INCHES_TO_METERS);
	} 
	
	/**
	 * Get the length of the robot in pixels
	 * @return Length of robot in pixels
	 */
	public int getLengthPixels() {
		return (int) ((kLength * AutoSim.PPI) / Util.INCHES_TO_METERS);
	} 
	
	/**
	 * Set the name of the Command the robot is running
	 * @param name Name of Command robot is currently running
	 */
	public void setCommandName(String name) {
		this.commandName = name;
	} 
	
	/**
	 * Set the goal point to be drawn
	 * @param goal Goal point Robot is attempting to reach
	 */
	public void setGoalPoint(Point goal) {
		this.goalPoint = goal;
	} 
	
	/**
	 * Set the lookahead for the robot
	 * @param lookahead New lookahead distance in inches
	 */
	public void setLookahead(double lookahead) {
		this.lookahead = lookahead;
	} 
	
	/**
	 * Set the PID output to be displayed
	 * @param pidOut Output from PID controller to be displayed
	 */
	public void setPIDOutput(double pidOut) {
		this.pidOut = pidOut;
	}
	
	/**
	 * Get the data of the robot
	 * @return All robot data point in a HashMap
	 */
	public HashMap<ROBOT_KEY, Object> getData() {
		HashMap<ROBOT_KEY, Object> data = new HashMap<ROBOT_KEY, Object>();
		
		//add all keys and values
		data.put(ROBOT_KEY.AVG_POS, averagePos);
		data.put(ROBOT_KEY.ANG_VEL, angularVel);
		data.put(ROBOT_KEY.LIN_VEL, linearVel);
		data.put(ROBOT_KEY.HEADING, heading);
		data.put(ROBOT_KEY.YAW, yaw);
		data.put(ROBOT_KEY.POINT, point);
		data.put(ROBOT_KEY.COLOR, color);
		data.put(ROBOT_KEY.LEFT_POS, leftGearbox.getPos() * kWheelRad / Util.INCHES_TO_METERS); //in
		data.put(ROBOT_KEY.RIGHT_POS, rightGearbox.getPos() * kWheelRad / Util.INCHES_TO_METERS); //in
		data.put(ROBOT_KEY.LEFT_VEL, leftGearbox.getVel() * kWheelRad / Util.INCHES_TO_METERS / 12); //ft/s
		data.put(ROBOT_KEY.RIGHT_VEL, rightGearbox.getVel() * kWheelRad / Util.INCHES_TO_METERS / 12); //ft/s
		data.put(ROBOT_KEY.LEFT_ACC, leftGearbox.getAcc());
		data.put(ROBOT_KEY.RIGHT_ACC, rightGearbox.getAcc());
		data.put(ROBOT_KEY.LIN_ACC, ((leftGearbox.getAcc() + rightGearbox.getAcc()) / 2) * kWheelRad / Util.INCHES_TO_METERS);
		data.put(ROBOT_KEY.ANG_ACC, (kWheelRad / (2 * kPivotArm)) * (rightGearbox.getAcc() - leftGearbox.getAcc()));
		data.put(ROBOT_KEY.CURRENT_COMMAND, commandName);
		data.put(ROBOT_KEY.GOAL_POINT, goalPoint);
		data.put(ROBOT_KEY.LOOKAHEAD_DIST, lookahead);
		data.put(ROBOT_KEY.PID_OUTPUT, pidOut);
		
		return data;
	} 
} 