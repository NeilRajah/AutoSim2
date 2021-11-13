/**
 * ProfileGenerator
 * Author: Neil Balaskandarajah
 * Created on: 30/04/2020
 * Save profiles to files to be read and played
 */

package model.motion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.knowm.xchart.XYChart;

import model.Point;
import model.Pose;
import util.FieldPoints;
import util.PlotGenerator;
import util.Util;

public class ProfileGenerator {

	/**
	 * Main script for saving profiles to files
	 */
	public static void main(String[] args) {
		/*
		double[][] curve = FieldPoints.niceLongCurve;
		long init = System.currentTimeMillis();
		BezierProfile bezTraj = new BezierProfile(curve, 30, 12 * 12, 200, 200);
		Util.println("Profile generation delta time: " + (System.currentTimeMillis() - init) + "ms");
		
		String filename = "niceLongCurve";
		posesToFile(bezTraj, filename);
		Util.println("Vels file saved: " + bezTraj.saveVelsToFile(filename));
		
		ArrayList<Pose> poses = posesFromVelsFile(filename, bezTraj);
		Util.println("Poses written to file: " + posesToFile(poses, filename));	
		Util.println("Path points to file: " + printPathPointsToFile(new BezierPath(curve), "curve", 100));
		*/
		
		PursuitPath path = new PursuitPath(FieldPoints.wrongL, 30, 12*12, 200, 200, 6);
		Util.println("PursuitPath made: " + path.saveXYToJS("wrongL", 2.5, 2.75, true));
		
//		double[] vels = path.getVels();
//		XYChart c = PlotGenerator.createChartFromArray(1920, 1080, "Pursuit Path Velocities", "Index", "Velocity", vels);
//		PlotGenerator.displayChart(c);
	} //end main
	
	/**
	 * Create a list of robot poses given left and right wheel velocities
	 * @param filename Name of the velocities file
	 * @param profile Profile the robot followed
	 * @return List of robot poses every UPDATE_PERIOD s
	 */
	private static ArrayList<Pose> posesFromVelsFile(String filename, BezierProfile profile) {
		ArrayList<Pose> poses = new ArrayList<Pose>();
		
		try {
			Scanner s = new Scanner(new File(Util.UTIL_DIR + filename + ".vels"));
			
			//parameters for calculating displacement vector
			double width = profile.getTrackWidth();
			double heading = Math.toRadians(profile.getInitialHeading());
			Point start = profile.getStartPosition();
			double dt = Util.UPDATE_PERIOD;
			
			//add first pose
			poses.add(new Pose(start, heading));
			double x = start.getX();
			double y = start.getY();
			
			while (s.hasNext()) {
				s.next(); //skip time token
				double leftDisp = s.nextDouble() * dt * 12; //v * t, inches
				double rightDisp = s.nextDouble() * dt * 12; //v * t, inches
				
				//displacement vector
				double mag = (leftDisp + rightDisp) / 2;
				double angle = (rightDisp - leftDisp) / width;
				
				//create point for pose
				x += mag * Math.sin(heading);
				y += mag * Math.cos(heading);
				Point p = new Point(x, y);
				
				//add pose
				poses.add(new Pose(p, heading));

				//update heading
				heading += angle;
			} //loop
			
			s.close();
			
		} catch (FileNotFoundException e) {
			Util.println("Could not find " + filename);
		} //try-catch
		
		return poses;
	} //end posesFromVelsFile
	
	/**
	 * Write a list of poses to a file
	 * @param poses List of poses
	 * @param filename Name of the file to write to 
	 * @return True if writing was successful, false if not
	 */
	private static boolean posesToFile(ArrayList<Pose> poses, String filename) {
		try {
			PrintWriter pw = new PrintWriter(new File(Util.UTIL_DIR + filename + ".poses"));
			
			//loop through poses list, adding (x,y,theta) to file
			for (int i = 0; i < poses.size(); i++) {
				double x = poses.get(i).getPoint().getX();
				double y = poses.get(i).getPoint().getY();
				double heading = poses.get(i).getHeading();
								
				pw.write(String.format("%f %f %f\n", x, y, heading));
			} //loop
			
			pw.close();
			return true;
			
		} catch (FileNotFoundException e) {
			Util.println("Could not find " + filename);
			return false;
		} //try-catch
	} //end posesToFile
	
	/**
	 * Get a list of poses from a file
	 * @param filename Name of the file with the poses
	 * @return List of poses, one from each line
	 */
	public static ArrayList<Pose> posesFromFile(String filename) {
		ArrayList<Pose> poses = new ArrayList<Pose>();
		
		try {
			Scanner s = new Scanner(new File(Util.UTIL_DIR + filename + ".poses"));

			//loop through each token, getting the x, y, and heading 
			while(s.hasNext()) {
				double x = s.nextDouble();
				double y = s.nextDouble();
				double heading = s.nextDouble();
				
				poses.add(new Pose(new Point(x, y), heading));
			} //loop
			
			s.close();
			
		} catch (FileNotFoundException e) {
			Util.println("Could not find " + filename);
		} //try-catch
		
		return poses;
	} //end posesFromFile
	
	public static boolean posesToFile(BezierProfile profile, String filename) {
		try {
			PrintWriter pw = new PrintWriter(new File(Util.UTIL_DIR + filename + ".ramsete"));
			
			for (double time = 0; time < profile.getTotalTime(); time += Util.UPDATE_PERIOD) {
				Pose p = profile.getPose(time);
				double x = p.getX();
				double y = p.getY();
				double theta = p.getHeading();
				double v = profile.getCenter(time);
				double w = profile.getOmega(time);
				pw.println(String.format("%.3f %.3f %.3f %.3f %.3f %.3f", time, x, y, theta, v, w));
			}
			
			pw.close();
			return true;
			
		} catch(IOException e) {
			return false;
		}
	}

	/**
	 * Print the points of a curve to a file
	 * @param path Path to calculate points with
	 * @param filename Name of the file to write to
	 * @param numPoints Number of points to split the path into
	 * @return True if writing was successful, false if not
	 */
	public static boolean printPathPointsToFile(BezierPath path, String filename, int numPoints) {
		try {
			PrintWriter pw = new PrintWriter(new File(Util.UTIL_DIR + filename + ".path"));
			
			double step = 1.0 / numPoints;
			for (double t = 0; t <= 1.0; t += step) {
				Point p = path.calcPoint(t);
				pw.write(String.format("%.3f %.3f\n", p.getX(), p.getY()));
			}
			
			pw.close();
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
} //end class 