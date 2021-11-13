/**
 * Environment
 * @author Neil Balaskandarajah
 * Created on: 08/11/2019
 * The environment the robot acts in
 */
package graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import graphics.widgets.Circle;
import main.AutoSim;
import model.Point;
import model.Pose;
import model.motion.BezierPath;
import util.Util;
import util.Util.ROBOT_KEY;

public class Environment extends JComponent {
	//Attributes
	//Singleton Instance
	private static Environment mInstance; //single instance of the Environment
	
	//Configured
	private int width; //width of the environment
	private int height; //height of the environment
	
	//Elements
	private Image field; //field image
	private UIBar bar; //user interface bar to update
	
	//Updated
	private ArrayList<Pose> poses; //list of robot poses to draw
	private ArrayList<HashMap<ROBOT_KEY, Object>> data; //data from the robot
	private static int poseIndex; //index in pose list of pose to draw
	private boolean debug; //whether to display the field or not
	private boolean simulating; //true when the animation is running
	
	//Curves
	private ArrayList<int[][]> curves; //points for the bezier path
	private BezierPath curve; //curve from the BPC widget
	private Circle[] controlPoints; //control points for path
	private Point[] waypoints; //waypoints the robot is following
	private boolean drawCurves; //whether the widget wants to draw the curve or not
	
	/**
	 * The environment the robot is simulated in
	 */
	private Environment() {
		super();
		
		//reset values
		debug = false;
		poseIndex = -1;
		simulating = false;
		drawCurves = true;
		
		//add border
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, AutoSim.PPI * 2));
	} 
	
	/**
	 * Get the single instance of the Environment
	 * @return Single instance of the environment
	 */
	public static Environment getInstance() {
		return mInstance == null ? mInstance = new Environment() : mInstance;
	}
	
	/**
	 * Set the size of the Environment
	 * @param width Width in pixels
	 * @param height Height in pixels
	 */
	public void setSize(int width, int height) {
		//set the width and height of the component
		mInstance.width = width;
		mInstance.height = height;
		mInstance.setPreferredSize(new Dimension(width, height));
		
		//open field image
		try {
			field = ImageIO.read(getClass().getResource("/resources/2020Field.png"));
			
			//scale the image for the machine's screen resolution
			field = field.getScaledInstance(width, height, BufferedImage.SCALE_FAST); 
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} //try-catch
		
		//set focus traversable
		this.setFocusable(true);
	} 
	
	//Simulation
	
	/**
	 * Set the status of the simulation
	 * @param isSimulating Whether or not the simulation is running
	 */
	public void setSimulating(boolean isSimulating) {
		this.simulating = isSimulating;
	} 
	
	/**
	 * Get the time of the simulation in seconds
	 * @return Simulation time in seconds
	 */
	public static double getTime() {
		return poseIndex * Util.UPDATE_PERIOD;
	} 
	
	//Pose
	
	/**
	 * Add poses to be later drawn
	 * @param poses List of poses to be drawn
	 */
	public void setPoses(ArrayList<Pose> poses) {
		this.poses = poses;
	} 
	
	/**
	 * Get the number of poses in the environment
	 * @return Number of poses in the poses list if the list is not null
	 */
	public int getNumPoses() {
		return poses != null ? poses.size() : null;
	} 
	
	/**
	 * Increment the pose index by one and repaint the component
	 */
	public void incrementPoseIndex() {
		poseIndex++;
		bar.setTime(poseIndex);
		repaint();
	} 
	
	/**
	 * Set the pose index to a specified value
	 * @param index Index to set pose index to
	 */
	public void setPoseIndex(int index) {
		poseIndex = index;
	}
	
	/**
	 * Get the pose index
	 * @return poseIndex Current pose index
	 * @return
	 */
	public double getPoseIndex() {
		return poseIndex;
	}
	
	//Curve
	
	/**
	 * Add curve to be used for graphics
	 * @param paths Bezier curve to be followed
	 */
	public void setCurves(ArrayList<int[][]> paths) {
		curves = paths;
	} 
	
	/**
	 * Increment the curve index by one and repaint the component
	 */
	public void incrementCurveIndex() {
		bar.setTime(poseIndex);
		repaint();
	} 
	
	/**
	 * Set the curve to be drawn (intended for widget use)
	 * @param path Curve to be drawn
	 */
	public void setPath(GraphicBezierPath path) {
		//clear the list (if it isn't null) and add the curve
		if (curves != null)
			curves.clear();
		else
			curves = new ArrayList<int[][]>();
		
		//add the points and update the environment
		curves.add(path.getPolyline());
		curves.add(path.getLeftPolyline());
		curves.add(path.getRightPolyline());
		this.controlPoints = path.getCircles();
		update();
	} 
	
	/**
	 * Set the waypoints to be displayed
	 * @param waypoints Waypoints for the robot to follow
	 */
	public void setWaypoints(Point[] waypoints) {
		this.waypoints = waypoints;
	} 
	
	/**
	 * Set whether to draw the curves
	 * @param val Whether to draw curves or not
	 */
	public void setDrawCurve(boolean val) {
		drawCurves = val;
		Util.println(drawCurves);
	}
	
	//Data
	
	/**
	 * Set the data for the simulation
	 * @param data list of robot data points at each timestamp
	 */
	public void setData(ArrayList<HashMap<ROBOT_KEY, Object>> data) {
		this.data = data;
	} 
	
	/**
	 * Get the data point at an index
	 * @param index list index to get data point at
	 * @return data point at index
	 */
	public HashMap<ROBOT_KEY, Object> getDataPoint(int index) {
		return data.get(index);
	} 
	
	//Graphics
	
	/**
	 * Return the width of the environment
	 * @return width Width of component in pixels
	 */
	public int width() {
		return width;
	} 

	/**
	 * Return the height of the environment
	 * @return height Height of component in pixels
	 */
	public int height() {
		return height;
	} 
	
	/**
	 * Update the environment
	 */
	public void update() {	
		repaint();
	} 
	
	/**
	 * Set the debug mode of the Environment (do not draw field if debug mode)
	 */
	public void setDebug() {
		debug = true;
	} 
	
	/**
	 * Set the focus state of the Enviroinment
	 * @param focus Focus state of the Environment
	 */
	public void setFocused(boolean focus) {
		if (focus) {
			this.setFocusable(true);
			this.requestFocus();
		} else {
			this.setFocusable(false);
		} //if
		
		Color color = focus ? Color.YELLOW : Color.BLACK;
		this.setBorder(BorderFactory.createLineBorder(color, AutoSim.PPI * 2));
		update();
	} 
	
	/**
	 * Draw the environment
	 * @param g Responsible for drawing
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; //Graphics2D for better graphics
	    
		//draw the background
		drawBackground(g2);
		
		//reset the stroke
		g2.setStroke(new BasicStroke((float) (AutoSim.PPI * 2), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		
		//draw the current path
		drawPath(g2);
		
		//draw the waypoints
		drawWaypoints(g2);
		
		//draw the goal point
		drawGoalPoint(g2);
		
		//draw the current pose
		drawCurrentPose(g2);
		
		//draw the lookahead
		drawLookAhead(g2);
	} 
	
	//User Interaction
	
	/**
	 * Add a UI bar
	 * @param bar Bar to store for the environment to update
	 */
	public void addUIBar(UIBar bar) {
		this.bar = bar;
	}
	
	/**
	 * Set the mouse coordinates for the UI to draw
	 * @param x X location of the mouse
	 * @param y Y location of the mouse
	 */
	public void setBarCursorLocation(int x, int y) {
		bar.setCursorLocation(x, y);
	} 
	
	//Graphics
	
	/**
	 * Draw the background of the Environment
	 * @param g2 Object for drawing
	 */
	private void drawBackground(Graphics2D g2) {
		//draw the field image as the background
		if (!debug) {
			g2.drawImage(field, 0, 0, null);
			
		} else {
			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRect(0, 0, width, height);
			
			g2.setColor(Color.black);
			Painter.drawGrid(g2, width, height, 12 * AutoSim.PPI); //12 inches
		} //if
		
		//draw the x and y indicators
		g2.setFont(Painter.createFont(Painter.SF_UI_FONT, AutoSim.PPI * 8));
		g2.drawString("y", (int) (width * 0.975),(int) (height * 0.03));
		g2.drawString("x", (int) (width * 0.015),(int) (height * 0.985));
	}
	
	/**
	 * Draw the current path 
	 * @param g2 Object for drawing
	 */
	private void drawPath(Graphics2D g2) {
		//draw the current path
		if (!simulating && curves != null && !curves.isEmpty()) {
			//stroke for lines
			g2.setColor(Color.WHITE);
			
			//draw the curve
			g2.drawPolyline(curves.get(0)[0], curves.get(0)[1], curves.get(0)[0].length);
			
			g2.setColor(Color.RED);
			g2.drawPolyline(curves.get(1)[0], curves.get(1)[1], curves.get(1)[0].length);
			
			g2.setColor(Color.BLUE);
			g2.drawPolyline(curves.get(2)[0], curves.get(2)[1], curves.get(1)[0].length);
						
			//draw the control points
			if (curves.size() == 3) {					
				//control points
				for (int i = 0; i < 6; i++)
					Painter.drawCircle(g2, controlPoints[i]);
				
				//tangents
				g2.setColor(Color.GRAY);
				Painter.setTransparency(g2, 0.8);
				Painter.drawLine(g2, controlPoints[0], controlPoints[1]);
				Painter.drawLine(g2, controlPoints[4], controlPoints[5]);
				Painter.setTransparency(g2, 1.0);
			} 
		}
	}

	/**
	 * Draw the current pose in the simulation
	 * @param g2 Object for drawing
	 */
	private void drawCurrentPose(Graphics2D g2) {
		AffineTransform oldTransform = g2.getTransform();
		if (poses != null && !poses.isEmpty()) { //if the pose is not null or empty
			Painter.drawPose(g2, poses.get(poseIndex));
		} //if
		g2.setTransform(oldTransform);
	} 
	
	/**
	 * Draw the goal point the robot is travelling to, if it has one
	 * @param g2 Object for drawing
	 */
	private void drawGoalPoint(Graphics2D g2) {
		if (poseIndex > 0 && data != null && (data.get(poseIndex).get(ROBOT_KEY.GOAL_POINT) != null)) {
			//drawing values
			g2.setColor(Color.GRAY);
			Point goal = (Point) data.get(poseIndex).get(ROBOT_KEY.GOAL_POINT);
			Point robot = poses.get(poseIndex).getPoint();
			
			//points to draw and line between them
			Painter.drawPoint(g2, goal);
			Painter.drawPoint(g2, robot);
			Painter.drawLine(g2, goal, robot);
		} 
	} 
	
	/**
	 * Draw the lookahead distance, if it exists
	 * @param g2 Object for drawing
	 */
	private void drawLookAhead(Graphics2D g2) {
		if (poseIndex > 0 && data != null && ((double) data.get(poseIndex).get(ROBOT_KEY.LOOKAHEAD_DIST) != 0)) {
			g2.setColor(Color.BLACK);
			Point robot = poses.get(poseIndex).getPoint();
			double lookahead = (Double) data.get(poseIndex).get(ROBOT_KEY.LOOKAHEAD_DIST);
			int dia = (int) (lookahead * 2.0);
			
			Painter.drawEmptyCircle(g2, robot, dia);
		} 
	} 
	
	/**
	 * Draw the waypoints the robot is following, if they exist
	 * @param g2 Object for drawing
	 */
	private void drawWaypoints(Graphics2D g2) {
		if (waypoints != null) {
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke((float) (AutoSim.PPI * 1.0), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			
			for (int i = 1; i < waypoints.length; i++)
				Painter.drawLine(g2, waypoints[i-1], waypoints[i]);
			
			for (int i = 0; i < waypoints.length; i++) 
				Painter.drawPoint(g2, waypoints[i], 3);
		} 
	} 
	
	/**
	 * Check whether the animation is running or not 
	 * @return simulating Whether the animation is running or not
	 */
	public boolean isSimulating() {
		return simulating;
	}
}
