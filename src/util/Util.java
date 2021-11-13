/**
 * Util
 * Author: Neil Balaskandarajah
 * Created on: 10/11/2019
 * Holds static utility methods and constants
 */
package util;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimerTask;

import main.AutoSim;

public class Util {
	//Robot Constants
	public static final double INCHES_TO_METERS = 0.0254;
	public static final double LBS_TO_KG = 0.453592;
	public static final double UPDATE_PERIOD = 0.005; //5ms
	public static final double V_INTERCEPT = 0.206; //voltage required to overcome frictional losses
	public static final int FIELD_WIDTH = 648; //width of field in inches
	public static final int FIELD_HEIGHT = 324; //height of field in inches
	public static final double MAX_VOLTAGE = 12.0; //max voltage in Volts
	public static final double LOOKAHEAD_DIST = 6; //additional distance to look ahead
	
	//PID constants
	//DrivePID constants
	public static final double kP_DRIVE = 0.9; //0.3 P2P, 0.9 linear traj
	public static final double kI_DRIVE = 0;
	public static final double kD_DRIVE = 0.13; //1.25 P2P, 0.13 linear traj

	//TurnPID constants
	public static final double kP_TURN = 0.06; //7.5 P2P, 0.06 curve
	public static final double kI_TURN = 0.0;
	public static final double kD_TURN = 0.05;
	
	//Motion Profiling Constants (calculated with model)
	public static final double kV_MODEL = 0.182; //ft/s -> V is 0.0833333
	public static final double kA_MODEL = 0.0203;
	
	//Motion Profiling Constants (calculated empirically)
	public static double kV_EMPIR = 1.07; //voltage -> ft/s, 1.07 linTraj, 1.07 curve
	public static double kA_EMPIR = 0.005; //voltage -> ft/s^2, 0.07 linTraj, 0.07 curve
	
	//RAMSETE Controller Constants
	public static final double kBETA = 2.0;
	public static final double kZETA = 0.7;
	
	//Motors (values from https://motors.vex.com/)
	//Free Speed (RPM), Free Current (A), Stall Torque (Nm), Stall Current (A)
	public static final double[] NEO = new double[] {5880, 1.3, 3.36, 166}; 
	public static final double[] CIM = new double[] {5330, 2.7, 2.41, 131}; 
	public static final double[] MINI_CIM = new double[] {5840, 3, 1.41, 89}; 
	public static final double[] PRO_775 = new double[] {18730, 0.7, 0.71, 134};
	public static final double[] FALCON_500 = new double[] {6380, 1.5, 4.69, 257}; 
	
	//Path Constants
	public static final int[] FIVENOMIAL_CONSTANTS = {1,5,10,10,5,1};
	
	//Directory Constants
	public static final String UTIL_DIR = "./src//util//";
	
	//Filter Constants
	public static final String NUMBER_INPUT = "0123456789.";
	
	//Widget IDs
	public static enum WIDGET_ID {
		SPEED_DISPLAY
	} //end enum
	
	//Robot HashMap Keys
	public static enum ROBOT_KEY {
		AVG_POS, 	
		LIN_VEL, 	
		ANG_VEL, 	
		HEADING, 	
		YAW,	 	
		POINT,		
		COLOR,		
		LEFT_POS,	
		RIGHT_POS,
		LEFT_VEL,
		RIGHT_VEL,
		LEFT_ACC,
		RIGHT_ACC,
		LIN_ACC,
		ANG_ACC,
		STATE,
		CURRENT_COMMAND,
		GOAL_POINT,
		LOOKAHEAD_DIST,
		PID_OUTPUT
	}
	
	//Testing Constants
	public static final int PASSED = 1;
	public static final int FAILED = 0;
	public static final int INITIALIZED = -1;
	
	//Output Constants
	private static final int DEFAULT_STACK_INDEX = 3;
	
	//Animation Constants
	public static final int ANIMATION_PERIOD = (int) (1000 * UPDATE_PERIOD);
	
	//Graphics Constants
	public static final float FONT_SIZE = AutoSim.PPI * 8f;
	
	//Calculations
	
	/**
	 * Clamp a number between two values
	 * @param num - number to clamp
	 * @param low - bottom value
	 * @param high - high value
	 * @return - value within [low, high]
	 */
	public static double clampNum(double num, double low, double high) {
		if (num > high) { //above limit
			return high;
		} else if (num < low) { //below limit
			return low;
		} //if
		
		return num;
	} //end clampNum
	
	/**
	 * Clamp a number between a minimum and maximum magnitude
	 * @param num number to be clamped
	 * @param low minimum magnitude limit
	 * @param high maximum magnitude limit
	 * @return number clamped between the two magnitudes
	 */
	public static double regulatedClamp(double num, double low, double high) {
		low = Math.abs(low);
		high = Math.abs(high);
		
		if (Math.abs(num) > high) {
			return Math.copySign(high, num);
		} else if (isBetween(num, -low, low)) {
			return Math.copySign(low, num);
		} //if
		
		return num;
	} //end regulatedClamp
	
	/**
	 * Check if a number is between two other numbers
	 * @param num number to test
	 * @param low low value number has to be greater than
	 * @param high high value number has to be lower than
	 * @return whether num is between low and high
	 */
	public static boolean isBetween(double num, double low, double high) {
		return low <= num && num <= high;
	} //end isBetween
	
	/**
	 * Check if an integer is an element in an integer array
	 * @param num integer to check array with
	 * @param nums array to check
	 * @return if nums contains num
	 */
	public static boolean numInArray(int num, int[] nums) {
		boolean inNums = false;
		
		//loop through array, checking if each element is equal to num or not
		for (int i = 0; i < nums.length; i++) {
			if (num == nums[i]) {
				inNums = true;
				break;
			} //if
		} //loop
		
		return inNums;
	} //end numInArray
	
	/**
	 * Calculate a slope of best fit for equally sized double arrays
	 * Math based on videos by Eugene O'Loughlin:
	 * https://youtu.be/2SCg8Kuh0tE
	 * https://youtu.be/GhrxgbQnEEU
	 * @param x - x values in data set
	 * @param y - y values in data set
	 * @return slope calculated using linear regression
	 */
	public static double regressedSlope(double[] x, double[] y) {
		//throw error if arrays are not same length
		if (x.length != y.length) {
			String errorMsg = "Arrays must be same size!\n";
			errorMsg += "x Array Size: " + x.length + "\n";
			errorMsg += "y Array Size: " + y.length + "\n";
			
			throw new IllegalArgumentException(errorMsg);
		} //if
		
		//compute average of each array
		double xSum = 0.0, ySum = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			xSum += x[i];
			ySum += y[i];
		} //loop
		
		double xAvg = xSum / x.length;
		double yAvg = ySum / y.length;
		
		//create deviation arrays 
		double[] xDevs = new double[x.length];
		double[] yDevs = new double[y.length];
		
		for (int i = 0; i < x.length; i++) {
			xDevs[i] = x[i] - xAvg;
			yDevs[i] = y[i] - yAvg;
		} //loop
		
		//compute Pearson's Coefficient
		double rNum = 0.0;
		double xDevSqr = 0.0, yDevSqr = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			rNum += xDevs[i] * yDevs[i];
			xDevSqr += Math.pow(xDevs[i], 2);
			yDevSqr += Math.pow(yDevs[i], 2);
		} //loop
		
		double r = rNum / Math.sqrt(xDevSqr * yDevSqr);
		
		//compute standard deviations
		double xStdDevNum = 0.0, yStdDevNum = 0.0;
		
		for (int i = 0; i < x.length; i++) {
			xStdDevNum += Math.pow(xDevs[i], 2);
			yStdDevNum += Math.pow(yDevs[i], 2);
		} //loop
		
		double xStdDev = Math.sqrt(xStdDevNum / (x.length - 1));
		double yStdDev = Math.sqrt(yStdDevNum / (y.length - 1));
		
		return r * (yStdDev / xStdDev);
	} //end regressedSlope
	
	/**
	 * Linearly interpolate between two points
	 * @param y - input value to find x value of
	 * @param x1 - bottom x value
	 * @param y1 - bottom y value
	 * @param x2 - top x value
	 * @param y2 - top y value
	 * @return - corresponding linearly interpolated x value of y
	 */
	public static double interpolate(double y, double x1, double y1, double x2, double y2) {
		return (y2 - y1) == 0 ? x1 : ((y - y1) * (x2 - x1)) / (y2 - y1) + x1;
	} //end interpolate
	
	//Output
	
	/**
	 * Get the information of the caller method
	 * @param stackIndex index of element in stack
	 */
	private static void printCallerInfo(int stackIndex) {
		StackTraceElement stack = Thread.currentThread().getStackTrace()[stackIndex];
		String methodName = stack.getMethodName();
		String className = stack.getClassName();
		
		System.out.print("[" + className + "." + methodName + "()] ");
	} //end println
	
	/**
	 * Print a single string to the console 
	 * @param s Message to be printed
	 */
	public static void println(String s) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		System.out.println(s);
	} //end println
	
	/**
	 * Print a single boolean to the console
	 * @param b Boolean to be printed
	 */
	public static void println(boolean b) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		System.out.println(b);
	} //end println
	
	/**
	 * Print a variable amount of strings to the console
	 * @param ... s - variable number of strings to print
	 */
	public static void println(String ... s) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		
		for(String str : s) {
			System.out.print(str + " "); //print each with a space in between
		} //loop
		System.out.println();
	} //end println
	
	/**
	 * Print a variable amount of doubles to the console
	 * @param ... d - variable number of doubles to print
	 */
	public static void println(double ... d) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		
		for (double dbl : d) {
			System.out.printf("%.4f ", dbl); //four decimal places
		} //loop
		System.out.println();
	} //end println
	
	/**
	 * Print a variable amount of doubles to the console with a message in front
	 * @param msg - message to print before numbers
	 * @param ... d - variable number of doubles to print
	 */
	public static void println(String msg, double ... d) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		System.out.print(msg + " ");
		
		for (double dbl : d) {
			System.out.printf("%.4f ", dbl); //four decimal places
		} //loop
		System.out.println();
	} //end println
	
	/**
	 * Print a variable amount of ints to the console
	 * @param ... ints - variable number of ints to print
	 */
	public static void println(int ... ints) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		
		for (int i: ints) {
			System.out.print(i + " "); //four decimal places
		} //loop
		System.out.println();
	} //end println
	
	/**
	 * Print a variable amount of ints to the console with a message in front
	 * @param msg message to print before numbers
	 * @param ... ints variable number of doubles to print
	 */
	public static void println(String msg, int ... ints) {
		printCallerInfo(DEFAULT_STACK_INDEX);
		System.out.print(msg + " ");

		for (int i: ints) {
			System.out.print(i + " "); //four decimal places
		} //loop
		System.out.println();
	} //end println
	
	/**
	 * Print a string with tabs at the end
	 * @param msg - string to print
	 */
	public static void tabPrint(String msg) {
		System.out.print(msg + "\t \t");
	} //end tabPrint
	
	/**
	 * Print a variable number of doubles with a tab after each one
	 * @param ... nums - variable number of doubles to print
	 */
	public static void tabPrint(double ... nums) {
		for (double num : nums) {
			System.out.printf("%.3f\t", num); //format to 3 digits
		} //loop
	} //end tabPrint
	
	/**
	 * Pauses the thread for a specified amount of time
	 * @param delay - time in milliseconds the pause the thread
	 */
	public static void pause(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException t) {
			t.printStackTrace();
		} //try-catch
	} //end pause
	
	/*
	 * Get a font from a file
	 * String location Location of the file
	 * return font Font retrieved from file OR default font
	 */
	public static Font getFileFont(String location) {
		Font f;
		
		//try to create and set the font if the file is present
		try {
//			String path = Util.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//			String decodedPath = URLDecoder.decode(path, "UTF-8");
//			Util.println(decodedPath);
			
//			ClassLoader cl = Thread.currentThread().getContextClassLoader();
//			InputStream is = cl.getResourceAsStream(location); 
			//"sf-ui-display-light.ttf"
			//"Oxygen-Regular.ttf"
			InputStream is = Util.class.getResourceAsStream(location);
			f = Font.createFont(Font.TRUETYPE_FONT, is);
			f = f.deriveFont(FONT_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
			f = new Font(Font.SANS_SERIF, Font.PLAIN, (int) FONT_SIZE);
		}
		
		return f;
	}
	
	/**
	 * Checks if a String is a valid number
	 * @param text String to check for number
	 * @return Whether or not text is a valid number
	 */
	public static boolean isNumber(String text) {
		//if parse succeeds, string is a number and true is returned
		try {
			Integer.parseInt(text); //no need to save; if error isn't caught, text is number
			return true;
		} catch (NumberFormatException n) {
			return false;
		} //try-catch
	} //end isNumber
	
	/**
	 * Convert a String to an double
	 * @param str String to convert
	 * @return num Numerical form of the String OR a default value of zero
	 */
	public static double stringToNum(String str) {
		double num;
		
		//set parsed number
		try { 
			num = Double.parseDouble(str);
		} catch (NumberFormatException n) { //set default
			num = 0;
		} //try-catch
		
		return num;
	} //end stringToNum
	
	/**
	 * Scale all elements of an array by a constant
	 * @param d Array to scale
	 * @param s Constant scalar
	 * @return Scaled array
	 */
	public static double[] scaleArray(double[] d, double s) {
		for (int i = 0; i < d.length; i++) {
			d[i] *= s;
		} //loop
		
		return d;
	} //end scaleArray
	
	/**
	 * Find the elements sandwiching value in list. Returns the index of the values this number is between in 
	 * ascending order, or an array of just the index of the equivalent value in the list within tolerance. 
	 * If value is outside the range of the list, the closest boundary index is returned.
	 * @param list List of elements to search through
	 * @param value Value to search with
	 * @param eps Value to be within to be considered the same
	 * @return Index of the elements in list surrounding value
	 */
	public static int[] findSandwichedElements(double[] list, double value, double eps) {
		//if first value is equal
		if (fuzzyEquals(list[0], value, eps)) 
			return new int[] {0, 0};
		
		for (int i = 1; i < list.length; i++) {
			//if value is equal
			if (fuzzyEquals(list[i], value, eps)) 
				return new int[] {i, i};
			
			if (list[i-1] < value && list[i] > value) {
				return new int[] {i-1, i};
			} //if
		} //loop
		
		//didn't find any match, value is outside boundaries of the list
		int key = value < list[0] ? 0 : list.length-1;
		return new int[] {key, key};
	} //end findSandwichedElements
	
	/**
	 * Returns whether or not two doubles are close enough to one another to be equal
	 * @param a First number
	 * @param b Second number
	 * @param eps Range they can be within
	 * @return True if the difference of a and b is less than eps, false if not
	 */
	public static boolean fuzzyEquals(double a, double b, double eps) {
		return Math.abs(a-b) <= eps;
	} //end fuzzyEquals
	
	/**
	 * Create an integer array with values from zero to n-1 inclusive
	 * @param n End value of array
	 * @return Array of indices
	 */
	public static double[] indexArray(int n) {
		double[] range = new double[n];
		
		for (int i = 0; i < n; i++)
			range[i] = i;
		
		return range;
	} //end indexArray
	
	/**
	 * Save a list of data to a text file
	 * @param data List of data points to be printed line by line
	 * @return True if writing was successful, false if not
	 */
	public static boolean saveListToFile(ArrayList<String> data, String filename) {
		try {
			//print every line to the file, return true if no errors
			PrintWriter pw = new PrintWriter(new File(Util.UTIL_DIR + filename + ".txt"));
			for (String line : data)
				pw.print(line);
			pw.close();
			
			Util.println(String.format("Saved %s", filename));
			return true;
			
		} catch (Exception e) {
			//return false because of errors
			return false;
		} //try-catch
	} //end saveListToFile
	
	/**
	 * Save the elements of a double array line by line to a text file
	 * @param data Double array with data points
	 * @param filename Name of the file to write to
	 * @return True if writing was successful, false if not
	 */
	public static boolean saveDoubleArrayToFile(double[] data, String filename) {
		try {
			//print every line to the file, return true if no errors
			PrintWriter pw = new PrintWriter(new File(Util.UTIL_DIR + filename + ".txt"));
			for (int i = 0; i < data.length; i++)
				pw.print(data[i] + "\n");
			pw.close();
			
			return true;
			
		} catch (Exception e) {
			//return false because of errors
			return false;
		} //try-catch
	} //end saveDoubleArrayToFile
	
	/**
	 * Convert a list of doubles to an array of doubles
	 * @param list List of doubles
	 * @return Array of doubles
	 */
	public static double[] doubleListToArray(ArrayList<Double> list) {
		return Arrays.stream(list.toArray()).mapToDouble(num -> Double.parseDouble(num.toString())).toArray();
	} //end doubleListToArray
	
	/**
	 * Return the number with the smaller magnitude
	 * @param a First number
	 * @param b Second number
	 * @return Number with the smaller magnitude (regardless of sign)
	 */
	public static double minMag(double a, double b) {
		return Math.abs(a) < Math.abs(b) ? a : b;
	} //end minMag
	
	/**
	 * Run a group of Runnables
	 * @param r Runnables to run
	 */
	public static void runRunnables(Runnable ... r) {
		for (int i = 0; i < r.length; i++)
			r[i].run();
	}
	
	/**
	 * Create a TimerTask from a group of Runnables
	 * @param r Runnables to run
	 * @return Task containing the Runnables
	 */
	public static TimerTask task(Runnable ... r) {
		return new TimerTask() {
			public void run() {
				runRunnables(r);
			}
		};
	}
} //end Util
