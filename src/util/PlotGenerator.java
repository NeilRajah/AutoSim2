/**
 * PlotGenerator
 * Author: Neil Balaskandarajah
 * Created on: 17/04/2020
 * Generate plots from (x,y) data
 */

package util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import model.motion.DriveProfile;
import model.motion.JerkProfile;
import util.Util.ROBOT_KEY;

public class PlotGenerator {
	
	/**
	 * Run tests and generate plots
	 */
	public static void main(String[] args) {
		JerkProfile profile = new JerkProfile(100, 24, 12);	
		
		displayChart(createLinearTrajChart(profile, "Jerk Profile", 1024, 768, 1));
	} //end main
	
	/**
	 * Create a profile trajectory plot with position, velocity and acceleration
	 * @param profile Profile to get data from
	 * @param title Plot title
	 * @param width Width in pixels
	 * @param height Height in pixels
	 * @param key 0,1,2 -> position, velocity, acceleration
	 */
	public static XYChart createLinearTrajChart(DriveProfile profile, String title, int width, int height, int key) {		
		double[][] xy = getXYFromProfile(profile, key);
		
		//create chart
		//re-factor this to HashMap, separate method
		String yAxis = "";
		
		switch (key) {
			case 0: 
				yAxis = "Position (\"";
				break;
				
			case 1: 
				yAxis = "Velocity (ft/s)";
				break;
				
			case 2:
				yAxis = "Acceleration (in/s^2)";
				break;
		} //switch
		
		XYChart chart = buildChart(width, height, title, "Time (s)", yAxis);
		chart.addSeries("Profile", xy[0], xy[1]);
		
		return chart;
	} //end createLinearTrajPlot
	
	/**
	 * Build a chart with the XYChartBuilder
	 * @param w Width in pixels
	 * @param h Height in pixels
	 * @param t Title
	 * @param x X axis title
	 * @param y Y axis title
	 * @return Chart configured with above parameters
	 */
	public static XYChart buildChart(int w, int h, String t, String x, String y) {
		return new XYChartBuilder().width(w).height(h).title(t).xAxisTitle(x).yAxisTitle(y).build();
	} //end buildChart
	
	/**
	 * Build a chart with the XYChartBuilder at a defualt 1920x1080 size
	 * @param t Title
	 * @param x X axis title
	 * @param y Y axis title
	 * @return Chart configured with above parameters
	 */
	public static XYChart buildChart(String t, String x, String y) {
		return buildChart(1920, 1080, t, x, y);
	} //end buildChart
	
	/**
	 * Display a chart in a separate Swing windowe
	 * @param c Chart to display
	 */
	public static void displayChart(XYChart c) {		
		new SwingWrapper(c).displayChart();
	} //end displayChart
	
	/**
	 * Display a chart in the default image viewing application
	 * @param c Chart to display
	 */
	public static void displayChartWindow(XYChart c) {		
		PlotGenerator.saveChartToFile(c, "displayChartWindow");
		try {
			Desktop.getDesktop().open(new File("src//util//displayChartWindow.png"));
		} catch (IOException e) {
			e.printStackTrace();
		} //try-catch
	} //end displayChartWindow
	
	/**
	 * Save the chart to a file
	 * @param c Chart to save to file
	 * @param filename Name of file
	 */
	public static void saveChartToFile(XYChart c, String filename) {
		try {
			BitmapEncoder.saveBitmapWithDPI(c, Util.UTIL_DIR + filename, BitmapFormat.PNG, 100);
		} catch (IOException i) {
			i.printStackTrace();
		} //try-catch
	} //end saveChartToFile
	
	/**
	 * Get the (x,y) points from a chart from the profile
	 * @param profile Profile to get data from
	 * @param key Index in trajectory point array (0,1,2 -> position, velocity, acceleration)
	 * @return Array containing the x and y arrays
	 */
	public static double[][] getXYFromProfile(DriveProfile profile, int key) {
		//get (x,y) points for profile
		double[] x = new double[profile.getSize()];
		double[] y = new double[profile.getSize()];
		
		//velocity profile
		for (int i = 0; i < profile.getSize(); i++) {
			x[i] = i * Util.UPDATE_PERIOD;
			y[i] = profile.getLeftTrajPoint(i)[key];
		} //loop
		
		return new double[][] {x, y};
	} //end getXYFromProfile
	
	/**
	 * Get the (x,y) points for a chart from robot data
	 * @param data Robot data points
	 * @param key Key indicating what data point to get
	 * @return Array containing the x and y arrays
	 */
	public static double[][] getXYFromRobotData(ArrayList<HashMap<Util.ROBOT_KEY, Object>> data, ROBOT_KEY key) {
		double[] x = new double[data.size()];
		double[] y = new double[data.size()];
		
		for (int i = 0; i < data.size(); i++) {
			x[i] = i * Util.UPDATE_PERIOD;
			y[i] = (double) data.get(i).get(key);
		} //loop
		
		return new double[][] {x, y};
	} //end getXYFromRobotData
	
	/**
	 * Create a chart from data points in a list
	 * @param w Width in pixels
	 * @param h Height in pixels
	 * @param t Title of chart
	 * @param x X-axis title
	 * @param y Y-axis title
	 * @param data Data points in list to be parsed
	 * @return Chart object
	 */
	public static XYChart createChartFromList(int w, int h, String t, String x, String y, ArrayList<String> data) {
		XYChart chart = buildChart(w, h, t, x, y);
		double[] xVals = new double[data.size()];
		double[] yVals = new double[data.size()];
		
		for (int i = 0; i < data.size(); i++) {
			xVals[i] = i;
			yVals[i] = Double.parseDouble(data.get(i));
		} //loop
		
		chart.addSeries("t", xVals, yVals);
		
		return chart;
	} //end createChartFromList
	
	/**
	 * Create a chart from data points in a list
	 * @param w Width in pixels
	 * @param h Height in pixels
	 * @param t Title of chart
	 * @param x X-axis title
	 * @param y Y-axis title
	 * @param data Data points in array
	 * @return Chart object
	 */
	public static XYChart createChartFromArray(int w, int h, String t, String x, String y, double[] data) {
		XYChart chart = buildChart(w, h, t, x, y);
		chart.addSeries("data", data);
		
		return chart;
	} //end createChartFromArray
} //end class
