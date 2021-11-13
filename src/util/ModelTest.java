/**
 * ModelTest
 * Author: Neil Balaskandarajah
 * Created on: 25/12/2019
 * Test cases to make sure the model works
 */

package util;

import static org.junit.Assert.assertEquals;

import java.awt.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commands.Command;
import commands.DriveDistance;
import commands.DriveToGoal;
import commands.TimedVoltage;
import commands.routines.ConstantsTest;
import graphics.components.BoxButton.BUTTON_STATE;
import graphics.widgets.Circle;
import model.DriveLoop;
import model.FieldPositioning;
import model.Gearbox;
import model.Motor;
import model.PIDController;
import model.Point;
import model.Robot;
import model.motion.BezierPath;
import model.motion.TrapezoidalProfile;
import util.Util.ROBOT_KEY;

public class ModelTest {
	//Attributes
	private static Gearbox gb; //drive gearbox
	private static Robot r; //robot 
	private static DriveLoop driveLoop; //loop controlling robot
	
	@Before
	/**
	 * Set up the suite by creating the robot
	 */
	public void setUp() {
		gb = new Gearbox(8.5521, new Motor(Util.NEO), 2); //2 NEO 12ft/s drive
		r = new Robot(4, 153, 30, 30, gb); //4" wheel dia 153lb 30"x30" 
		PIDController drivePID = new PIDController(Util.kP_DRIVE, Util.kI_DRIVE, Util.kD_DRIVE, r.getMaxLinSpeed());
		PIDController turnPID = new PIDController(Util.kP_TURN, Util.kI_TURN, Util.kD_TURN, r.getMaxLinSpeed());
		driveLoop = new DriveLoop(r, drivePID, turnPID);
		r.setXY(new Point(0,0));
	} //end setUp
	
	@After
	/**
	 * Reset the suite after each test by resetting the robot values and returning to origin
	 */
	public void reset() {
		r.reset();
		r.setXY(new Point(0,0));
	} //end reset
	
	@Test
	/**
	 * Ensure the torque produced by the robot is correct
	 */
	public void gearboxTorqueTest() {
		double torque = gb.calcTorque(12);
		
		double correctValue = 57.47; //calculated from model spreadsheet
		assertEquals("Torque should be " + correctValue + ", calculated to be " + torque, correctValue, torque, 1);
	} //end gearboxTorqueTest
	
	@Test
	/**
	 * Ensure the robot displaces the correct amount
	 */
	public void displacementTest() { 
		double t = 0.00;
		while (t <= 1.0 + Util.UPDATE_PERIOD) {
			r.update(12, 12);
			t += Util.UPDATE_PERIOD;
		} //loop
		double correctDisplacement = 128.6; //calculated from model spreadsheet
		assertEquals(correctDisplacement, r.getAveragePos(), 1);
	} //end displacementTest
	
	@Test
	/**
	 * Ensure the x direction of the pose is correct
	 */
	public void poseTestX() {
		double t = 0.00;
		
		r.setHeading(2);
		
		while (t <= 1.0 + Util.UPDATE_PERIOD) {
			r.update(12,12);
			t += Util.UPDATE_PERIOD;
		} //loop
		
		Point correctPoint = new Point(116.9, -53.5); //(r, theta) = (128.6, 2)
		assertEquals(correctPoint.getX(), r.getPoint().getX(), 1);
	} //end poseTestX
	
	@Test
	/**
	 * Ensure the y direction of the pose is correct
	 */
	public void poseTestY() {
		double t = 0.00;
		
		r.setHeading(2);
		
		while (t <= 1.0 + Util.UPDATE_PERIOD) {
			r.update(12,12);
			t += Util.UPDATE_PERIOD;
		} //loop

		Point correctPoint = new Point(116.9, -53.5); //(r, theta) = (128.6, 2)
		assertEquals(correctPoint.getY(), r.getPoint().getY(), 1);
	} //end poseTestY
	
	@Test
	/**
	 * Ensure the robot turns properly
	 */
	public void headingTest() {
		double t = 0.00;
		while (t <= 1.00 + Util.UPDATE_PERIOD) {
			r.update(-12, 12);
			t += Util.UPDATE_PERIOD;
		} //loop
		double correctHeading = 510; //calculated from model spreadsheet
		assertEquals(correctHeading, Math.toDegrees(r.getHeading()), 1);
	} //end headingTest
	
	
	@Test
	/**
	 * Ensure the robot accelerates properly
	 */
	public void speedTest() {
		double t = 0.00;
		while (t <= 1.00 + Util.UPDATE_PERIOD) {
			r.update(12, 12);
			t += Util.UPDATE_PERIOD;
		} //loop
		double correctSpeed = 12; //calculated from model spreadsheet
		assertEquals(correctSpeed, r.getLinearVel(), 1);
	} //end speedTest
	
	@Test
	/**
	 * Ensure the top linear speed of the robot is calculated correctly
	 */
	public void topLinSpeedTest() {
		double correctTopSpeed = 12; //calculated from model spreadsheet
		assertEquals(correctTopSpeed, r.getMaxLinSpeed(), 1);
	} //end topLinSpeedTest
	
	@Test
	/**
	 * Ensure the top angular speed of the robot is calculated correctly
	 */
	public void topAngSpeedTest() {
		double correctTopSpeed = 9.6; //calculated from model spreadsheet
		assertEquals(correctTopSpeed, r.getMaxAngSpeed(), 1);
	} //end topAngTest
	
	@Test
	/**
	 * Ensure the distance the robot drives when controlled by PID is correct
	 */
	public void driveDistanceTest() {
		DriveDistance dd = new DriveDistance(driveLoop, 100, 1, 12);
		dd.run();
		assertEquals(100, r.getAveragePos(), 1);
	} //end driveDistanceTest
	
	@Test
	/**
	 * Ensure the robot calculates its yaw correctly
	 */
	public void yawTest() {
		r.setHeadingDegrees(425);
		assertEquals(65, r.getYaw(), 1);
	} //end yawTest
	
	@Test
	/**
	 * Ensure the robot moves to a goal point correctly
	 */
	public void driveToGoalTest() {
		DriveToGoal d2g = new DriveToGoal(driveLoop, FieldPoints.AUTO_POS_TWO, 1, 12, 0, false);
		d2g.run();
//		assertEquals(1, FieldPositioning.calcDistance(FieldPoints.AUTO_POS_TWO, driveLoop.getRobot().getPoint()), 1);
	} //end driveToGoalTest
	
	@Test
	/**
	 * Calculate the constants
	 */
	public void constantsTest() {
		ConstantsTest ct = new ConstantsTest(driveLoop);
		ct.execute();
		double correctSlope = 0.9908;
		assertEquals(correctSlope, Util.kV_EMPIR, 1E-2);
	} //end ConstantsTest
		
	@Test
	/**
	 * Check if the path point calculated at a t value is correct
	 */
	public void pathPointTest() {
		BezierPath testPath = new BezierPath(FieldPoints.curve);
		Point correctPoint = new Point(5.4, 3.7);
		assertEquals(0.0, FieldPositioning.dist(correctPoint, testPath.calcPoint(0)), 0.1);
	} //end pathPointTest
	
	@Test
	/**
	 * Path heading test (dx = 0, dy > 0)
	 */
	public void pathHeadingTest1() {
		Point p1 = new Point(0,0);
		Point p2 = new Point(0,1);
		double correctHeading = 0;
		
		assertEquals(correctHeading, FieldPositioning.goalYaw(p1, p2), 1E-3);
	} //end pathHeadingTest1
	
	@Test
	/**
	 * Path heading test (dy = 0, dx > 0)
	 */
	public void pathHeadingTest2() {
		Point p1 = new Point(0,0);
		Point p2 = new Point(1,0);
		double correctHeading = 90;
		
		assertEquals(correctHeading, FieldPositioning.goalYaw(p1, p2), 1E-3);
	} //end pathHeadingTest2
	
	@Test
	/**
	 * Path heading test (dx = 0, dy < 0)
	 */
	public void pathHeadingTest3() {
		Point p1 = new Point(0,0);
		Point p2 = new Point(0,-1);
		double correctHeading = 180;
		
		assertEquals(correctHeading, FieldPositioning.goalYaw(p1, p2), 1E-3);
	} //end pathHeadingTest3
	
	@Test
	/**
	 * Path heading test (dy = 0, dx < 0)
	 */
	public void pathHeadingTest4() {
		Point p1 = new Point(0,0);
		Point p2 = new Point(-1,0);
		double correctHeading = -90;
		
		assertEquals(correctHeading, FieldPositioning.goalYaw(p1, p2), 1E-3);
	} //end pathHeadingTest4
	
	@Test
	/**
	 * Linear interpolation test
	 */
	public void linearInterpolationTest() {
		assertEquals(1.17608, Util.interpolate(212, 1.1555, 200, 1.1898, 220), 1E-3);
	} //end linearInterpolationTest
	
	@Test
	/**
	 * Test getting data HashMap from the robot
	 */
	public void robotDataTest() {
		Command c = new TimedVoltage(driveLoop, 6.0, 2);
		c.run();
		
		double dataPoint = (double) driveLoop.getRobot().getData().get(ROBOT_KEY.AVG_POS);
		assertEquals(135, dataPoint, 1);
	} //end robotDataTest
	
	@Test
	/**
	 * Test the upper limits of regulated clamping
	 */
	public void regulatedClampUpperLimitTest() {
		assertEquals(-1, Util.regulatedClamp(-1.2, 0.2, 1), 0E-3);
	} //end regulatedClampingTest
	
	@Test
	/**
	 * Test the lower limits of regulated clamping
	 */
	public void regulatedClampingLowerLimitTest() {
		assertEquals(-0.2, Util.regulatedClamp(-0.1, 0.2, 1), 0E-3);
	} //end regulatedClampingTest
	
	@Test
	/**
	 * Test the default case of regulated clamping
	 */
	public void regulatedClampingTest() {
		assertEquals(-0.6, Util.regulatedClamp(-0.6, 0.2, 1), 0E-3);
	} //end regulatedClampingTest
	
	@Test
	/**
	 * Test the number parsing utility function
	 */
	public void numberParseTest() {
		assertEquals(0.7125, Util.stringToNum("0.7125"), 0E-2);
	} //end numberParseTest
	
	@Test
	/**
	 * Test the functionality of getting the Circle's state
	 */
	public void circleStateTest() {
		Circle c = new Circle(20, 20, Color.GREEN);
		c.setHovered();
		try {
			assertEquals(true, c.getState().equals(BUTTON_STATE.HOVER));
		} catch (AssertionError a) {
			System.out.println("Expected: HOVERED, Actual: " + c.getState());
			throw a;
		} //try-catch
	} //end circleStateTest
	
	@Test
	/**
	 * Test the functionality of the trapezoidal profile
	 */
	public void trapProfileTest() {
		TrapezoidalProfile tp = new TrapezoidalProfile(100, 24, 12);
		
		/* Print positions and velocities
		 for (double t = 0; t < tp.getTotalTime(); t += Util.UPDATE_PERIOD) {
			double[] l = tp.getLeftTrajPoint(t);
			double[] r = tp.getRightTrajPoint(t);
			Util.tabPrint(t, l[0], r[0], l[1], r[1], l[2], r[2]);
			System.out.println();
		} //loop
		*/		
		
		//open chart plot
		//PlotGenerator.displayChart(PlotGenerator.createLinearTrajChart(tp, "trapProfileTest", 1920, 1080, 1));
		
		assertEquals(100.0, tp.getLeftTrajPoint(tp.getTotalTime())[0], 1);
	} //end trapProfileTest
	
	@Test
	/**
	 * Testing time calculations for linear trajectories
	 */
	public void linearTimeCalculationTest() {
		double maxVel = 144; //inches
		double accDist = 24; //inches
		
		double trajAcc = Math.pow(maxVel, 2) / (2 * accDist);
		double t1 = Math.sqrt((2 * accDist) / trajAcc);
		
		double t2 = (2 * accDist) / maxVel;
		
		assertEquals(t1, t2, 0.05);
	} //end linearTimeCalculationTest
	
	@Test
	/**
	 * Test the sandwiching elements function
	 */
	public void findSandwichedElementsTest() {
		double[] list = new double[] {0,2,5,9};
		double value = 1;
		double eps = 0.01;
		
		int[] expected = new int[] {0,1};
		int[] output = Util.findSandwichedElements(list, value, eps);
		
		assertEquals(true, Util.fuzzyEquals(expected[0], output[0], eps)
					 && Util.fuzzyEquals(expected[1], output[1], eps));
	} //end findSandwichedElementsTest
	
	@Test
	/**
	 * Test the fuzzy equals function
	 */
	public void fuzzyEqualsTest() {
		assertEquals(false, Util.fuzzyEquals(10, 20, 0.1));
	} //end fuzzyEqualsTest
	
	@Test
	/**
	 * Test linear interpolation along with sandwiching
	 */
	public void sandwichInterpolationTest() {
		double[] x = new double[] {0, 0.1, 0.2, 0.3};
		double[] y = new double[] {0, 2,   5,   9};
		
		double input = 3.5;
		double expected = 0.15;
		double tolerance = 1E-3;
		
		int[] limits = Util.findSandwichedElements(y, input, tolerance);
		int bot = limits[0];
		int top = limits[1];
		
		double output = Util.interpolate(input, x[bot], y[bot], x[top], y[top]);
				
		assertEquals(expected, output, tolerance);
	} //end sandwichInterpolataionTest
	
	@Test
	/**
	 * Test the radius calculation method
	 */
	public void calcRadiusTest() {
		Point p1 = new Point(-2,0);
		Point p2 = new Point(0,2);
		Point p3 = new Point(2,0);
		
		double expected = 2.0;
		double output = FieldPositioning.calcRadius(p1, p2, p3);
		
		assertEquals(expected, output, 0.001);
	} //end calcRadiusTest
	
	@Test
	/**
	 * Test the add function of the Point class
	 */
	public void pointAddTest() {
		assertEquals(2, Point.add(new Point(1,0), new Point(1,0)).getX(), 0.001);
	} //end pointAddTest
	
	@Test
	/**
	 * Test the subtract function of the Point class
	 */
	public void pointSubtractTest() {
		assertEquals(0, Point.subtract(new Point(1,0), new Point(1,0)).getX(), 0.001);
	} //end pointSubtractTest
	
	@Test
	/**
	 * Test the scale function of the Point class
	 */
	public void pointScaleTest() {
		assertEquals(3, Point.scale(new Point(1,0), 3).getX(), 0.001);
	} //end pointScaleTest
	
	@Test
	/**
	 * Test the magnitude function of the Point class
	 */
	public void pointMagTest() {
		assertEquals(2.828, new Point(2,2).getMag(), 0.001);
	} //end pointMagTest
	
	@Test
	/**
	 * Test the heading function of the Point class
	 */
	public void pointHeadingTest() {
		assertEquals(Math.toRadians(45), new Point(2,2).getHeading(), 0.001);
	} //end pointHeadingTest
		
	@Test
	/**
	 * Test the setMag function of the Point class
	 */
	public void pointSetMagTest() {
		assertEquals(3, Point.setMag(new Point(1,1), 3).getMag(), 0.001);
	} //end pointSetMagTest
	
	@Test
	/**
	 * Test the angleBetween method in the FieldPositioning class
	 */
	public void fieldPositioningAngleBetweenTest() {
		assertEquals(Math.PI/2, FieldPositioning.angleBetween(new Point(1,0), new Point(0,1)), 0.001);
	} //end fieldPositioningAngleBetweenTest
	
	@Test
	/**
	 * Test the limitMag function of the Point class
	 */
	public void pointLimitMagTest() {
		assertEquals(3, Point.limitMag(new Point(5,5), 3).getMag(), 0.001);
	} //end pointLimitMagTest
	
	@Test
	/**
	 * Test the angleWrap function of the FieldPositioning class
	 */
	public void angleWrapRadTest() {
		assertEquals(Math.toRadians(-90), FieldPositioning.angleWrap(Math.toRadians(270)), 0.001);
	} //end angleWrapRadTest
	
	@Test
	/**
	 * Test the angleWrapDeg function of the FieldPositioning class
	 */
	public void angleWrapDegTest() {
		assertEquals(-90, FieldPositioning.angleWrapDeg(270), 0.001);
	} //end angleWrapDegTest
	
	@Test
	/**
	 * Test the minMag function of the Point class
	 */
	public void pointMinMagTest() {
		assertEquals(2.828, Point.minMag(new Point(2,2), new Point(25,25)).getMag(), 0.001);
	} //end minMagTest
	
	@Test
	/**
	 * Test the normalize function of the Point class
	 */
	public void normalizeTest() {
		assertEquals(1.0, Point.normalize(new Point(5,3)).getMag(), 0.001);
	} //end normalizeTest
	
	@Test
	/**
	 * Test the normalize function of the Point class
	 */
	public void dotTest() {
		assertEquals(4, Point.dot(new Point(1,1), new Point(2,2)), 0.001);
	} //end dotTest
	
	@Test
	/**
	 * Test the equals method of the Point class
	 */
	public void pointEqualsTest() {
		assertEquals(true, new Point(4,4).equals(new Point(3.9997, 4.00001)));
	} //end pointEqualsTest
	
	@Test
	/**
	 * Test the getNormalPoint function of the FieldPositioning class
	 */
	public void getNormalPointTest() {
		Point p = new Point(0.5,5); //point to find normal of
		Point a = new Point(0,0); //start of line segment
		Point b = new Point(1,0); //end of line segment
		Point expected = new Point(0.5,0);
		
		assertEquals(true, FieldPositioning.getNormalPoint(p,a,b).equals(expected));
	} //end getNormalPointTest
	
	@Test
	/**
	 * Test the pointOnLine function of the FieldPositioning class
	 */
	public void pointOnLineTest() {
		Point a = new Point(0,0); //start of segment
		Point b = new Point(3,3); //end of segment
		Point p = new Point(1,1); //point supposedly on line
		
		assertEquals(true, FieldPositioning.pointOnLine(p, a, b, 0.001));
	} //end pointOnLineTest
	
	@Test
	/**
	 * Test the lineCircleIntersect function of the FieldPositioning class
	 */
	public void lineCircleIntersect() {
		Point E = new Point(2, -2); //start of line segment
		Point L = new Point(2, 2); //end of line segment
		Point C = new Point(0, 0); //center of circle
		double r = 2; //circle radius
		Point expected = new Point(2,0);
		
		assertEquals(true, FieldPositioning.lineCircleIntersect(E, L, C, r).get(0).equals(expected));
	} //end lineCircleIntersect
	
	@Test
	/**
	 * Test the minMag function of the Util class
	 */
	public void minMagTest() {
		assertEquals(-2, Util.minMag(-2, 13.05), 0.001);
	} //end minMag
	
	@Test
	/**
	 * Test the distanceSquared method in FieldPositioning
	 */
	public void distsqTest() {
		assertEquals(100.0, FieldPositioning.distsq(new Point(0,0), new Point(10,0)), 0.001);
	} //end distsqTest
} //end class