/**
 * PIDSim
 * Author: Neil Balaskandarajah
 * Created on: 28/07/2020
 * Simulating PID controllers
 */

package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import org.knowm.xchart.XYChart;

import commands.CommandGroup;
import commands.CommandList;
import commands.DriveDistance;
import graphics.Painter;
import graphics.Window;
import graphics.components.TextInput;
import graphics.widgets.ChartWidget;
import graphics.widgets.SpeedDisplay;
import graphics.widgets.SpeedDisplayWidget;
import graphics.widgets.TextInputWidget;
import model.DriveLoop;
import model.Gearbox;
import model.Motor;
import model.PIDController;
import model.Point;
import model.Robot;
import util.Util;
import util.Util.ROBOT_KEY;

public class PIDSim {
	//Attributes
	private static Window w; //Window to add components to
	private static Robot r; //robot to control
	private static DriveLoop driveLoop; //DriveLoop instance to be controlled
	private static CommandGroup cg; //CommandGroup to be run
	private static TextInputWidget kPWidg, kIWidg, kDWidg; //Widgets for PID constants
	private static ChartWidget outputWidg, errorWidg; //PID output and error widgets 
	
	private static double distance; //Distance robot will be driving
	private static double tolerance; //Distance away from endpoint robot can acceptably be within
	private static double maxSpd; //Maximum speed the robot can travel at
	private static final Point startXY = new Point(162, 75); //Robot start position
	private static final double startHeading = 0; //Robot start heading
	
	/**
	 * Create a Window and launch the program
	 */
	public static void main(String[] args) {			
		//initialize the program
		initializeScreen();
		initializeSimulation(); 
		
		//create the window 
		w = new Window("PIDSim", true); //true for debug, false for not
		addWidgets(); //add widgets to the widget hub
		w.addStartButtonActions(PIDSim::updateCommand, r::reset, errorWidg::reset, outputWidg::reset);
		
		//add the command group and plot data
		w.addCommandGroup(cg);
		
		//launch the application
		w.launch();
	} 
	
	/**
	 * Set the constants related to the screen
	 */
	private static void initializeScreen() {
		//Get the screen size for scaling the application
		AutoSim.SCREEN_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width);
		AutoSim.SCREEN_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height);
		
		//5 pixels per inch on a 3840x2160 screen
		AutoSim.PPI = (int) Math.floor(5.0 * (AutoSim.SCREEN_WIDTH/3840.0));
	} 
	
	/**
	 * Initialize the program by creating the robot and the command group
	 */
	private static void initializeSimulation() {
		//Create the robot to control by setting its physical parameters
		Gearbox gb = new Gearbox(Gearbox.ratioFromTopSpeed(Util.NEO, 4, 12), new Motor(Util.NEO), 2); //12ft/s 4 NEO
		r = new Robot(4, 153, 30, 30, gb); //153lb 4" wheel dia 30"x30" chassis
		r.setXY(startXY);
		r.setHeadingDegrees(startHeading);
		
		//Set graphics parameters for drawing the robot
		Painter.ROBOT_LENGTH = r.getLengthPixels();
		Painter.ROBOT_WIDTH = r.getWidthPixels();
		
		//Create the feedback controllers and state machine for the robot
		//Controllers are configured with their kP, kI, kD constants, along with the respective top speed for regulation
		//The state machine uses these controllers to link user input and robot output
		PIDController drivePID = new PIDController(0, 0, 0, r.getMaxLinSpeed());
		PIDController turnPID = new PIDController(Util.kP_TURN, Util.kI_TURN, Util.kD_TURN, r.getMaxAngSpeed()); 
		driveLoop = new DriveLoop(r, drivePID, turnPID); 
		
		//Create the command the robot is going to follow
		//CommandList is a utility object for creating a CommandGroup
		//The command to be followed is a simple DriveDistance command
		//Drive to 100 inches ahead with 1 inch of tolerance, limited to max speed (no speed limit)
		//The simulation will stop when the command ends or when 10 seconds has passed, whichever comes first
		distance = 100;
		maxSpd = r.getMaxLinSpeed();
		tolerance = 1;
		cg = new CommandList(new DriveDistance(driveLoop, distance, tolerance, maxSpd));
		
		//Save a point distance inches ahead of the robot for visualization
		r.setGoalPoint(Point.add(r.getPoint(), Point.vector(distance, r.getHeading() + Math.PI/2)));
	} 
	
	/**
	 * Add Widgets to the Widget Hub
	 */
	private static void addWidgets() {
		//The Widget Hub holds the widgets on the side of the screen
		w.setUpWidgetHub();
		
		//Widget for each constant
		kPWidg = new TextInputWidget(new TextInput("kP", w.getHubHeight(), w.getHubWidth(), Util.NUMBER_INPUT));
		w.addWidget(kPWidg);
		kIWidg = new TextInputWidget(new TextInput("kI", w.getHubHeight(), w.getHubWidth(), Util.NUMBER_INPUT));
		w.addWidget(kIWidg);
		kDWidg = new TextInputWidget(new TextInput("kD", w.getHubHeight(), w.getHubWidth(), Util.NUMBER_INPUT));
		w.addWidget(kDWidg);
		
		//Widget for PID output
		XYChart outputChart = new XYChart(w.getHubWidth(), w.getHubHeight() / 3);
		outputWidg = new ChartWidget(outputChart, ROBOT_KEY.PID_OUTPUT, "PID Output");
		outputWidg.setColor(Color.MAGENTA);
		w.addWidget(outputWidg);
		
		//Widget for error
		XYChart errorChart = new XYChart(w.getHubWidth(), w.getHubHeight() / 3);
		errorWidg = new ChartWidget(errorChart, ROBOT_KEY.AVG_POS, "Error");
		errorWidg.setColor(Color.RED);
		errorWidg.setKeyOperation(pos -> (double) (distance - pos));  //calculate the error
		errorWidg.setInitialY(distance);
		w.addWidget(errorWidg);
	}
	
	/**
	 * Update the command with new PID gains
	 */
	private static void updateCommand() {
		//Set PID values
		double kP = Double.parseDouble(kPWidg.getText());
		double kI = Double.parseDouble(kIWidg.getText());
		double kD = Double.parseDouble(kDWidg.getText());
		driveLoop.getDrivePID().setGains(kP, kI, kD);
		
		//Reset the robot
		driveLoop.getRobot().setXY(startXY);
		driveLoop.getRobot().setHeadingDegrees(startHeading);
		
		cg = new CommandList(new DriveDistance(driveLoop, distance, tolerance, maxSpd));

		//Reset the command group
		w.addCommandGroup(cg);
	}
}
