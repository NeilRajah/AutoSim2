/**
 * Painter
 * @author Neil Balaskandarajah
 * Created on: 10/11/2019
 * Holds all drawing methods for any graphics
 */
package graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;

import graphics.widgets.Circle;
import main.AutoSim;
import model.Point;
import model.Pose;

public class Painter {
	//Painting constants
	public static int ROBOT_LENGTH = 0; //length of robot in pixels
	public static int ROBOT_WIDTH = 0; //width of robot in pixels
	public static final int CORNER_RAD = AutoSim.PPI * 6; //corner radius
	public static final int CIRCLE_RAD = 8; //radius of circle
	
	//Fonts
	public static final String SF_UI_FONT = "src/sf-ui-display-light.ttf";
	public static final String OXYGEN_FONT = "/Oxygen-Regular.ttf";
	
	//Colors
	public static final Color BEZ_BTN_LIGHT = new Color(120, 130, 140);
	public static final Color BEZ_BTN_DARK = new Color(110, 120, 130);
	
	/**
	 * Draw a string to the screen
	 * @param g2 Used for drawing
	 * @param str String to draw
	 * @param x X position to draw to
	 * @param y Y position to draw to
	 */
	public static void drawFlippedString(Graphics2D g2, String str, int x, int y) {
		g2.scale(1.0, -1.0);
		g2.drawString(str, x, -y);
		g2.scale(1.0, -1.0);
	} //end drawString
	
	/**
	 * Draw a robot pose to the screen
	 * @param g2 Object for drawing
	 * @param p Pose to draw
	 */
	public static void drawPose(Graphics2D g2, Pose p) {
		//translate to center of robot, robot to its heading
		g2.translate(p.getPoint().getY()*AutoSim.PPI, p.getPoint().getX()*AutoSim.PPI);
		g2.rotate(p.getHeading());
		
		//draw body of robot
		g2.setColor(p.getColor());
		g2.fillRoundRect(-ROBOT_LENGTH/2, -ROBOT_WIDTH/2, ROBOT_LENGTH, ROBOT_WIDTH, CORNER_RAD, CORNER_RAD);
		
		//draw back-end indicator (straight box so only round edges at front)
		g2.fillRoundRect(-ROBOT_LENGTH/2, -ROBOT_WIDTH/2, CORNER_RAD, ROBOT_WIDTH, CORNER_RAD/4, CORNER_RAD/4);
	} //end drawPose
	
	/**
	 * Draw the grid to the environment
	 * @param g2 Object for drawing
	 * @param width Width of the environment
	 * @param height Height of the environment
	 * @param step Increments for the grid lines
	 */
	public static void drawGrid(Graphics2D g2, int width, int height, int step) {
		//draw vertical lines
		for (int x = step; x < width; x += step) {
			g2.drawLine(x, 0, x, height);
		} //loop
		
		//draw horizontal lines
		for (int y = step; y < height; y += step) {
			g2.drawLine(0, y, width, y);
		} //loop
	} //end drawGrid
	
	/**
	 * Draw a point in real space
	 * @param g2 Used for drawing
	 * @param p Point to draw
	 * @param rad Radius of point
	 */
	public static void drawPoint(Graphics2D g2, Point p, int rad) {
		rad *= AutoSim.PPI; //scale to pixels
		int x = (int) (p.getX() * AutoSim.PPI);
		int y = (int) (p.getY() * AutoSim.PPI);
		
		g2.fillOval((int) (y - rad/2.0), (int) (x - rad/2.0), rad, rad);
	} //end drawPoint
	
	/**
	 * Draw a point with a standard radius
	 * @param g2 Used for drawing
	 * @param p Point in real space
	 */
	public static void drawPoint(Graphics2D g2, Point p) {
		drawPoint(g2, p, 8);
	} //end drawPoint
	
	/**
	 * Draw a circle with a standard radius
	 * @param g2 Used for drawing 
	 * @param c Circle to draw
	 */
	public static void drawCircle(Graphics2D g2, Circle c) {
		g2.setColor(c.getColor());
		drawPoint(g2, c, CIRCLE_RAD);
	} //end drawCircle
	
	/**
	 * Draw a line between two points in real space
	 * @param g2 Used for drawing
	 * @param start Start point in inches
	 * @param end End point in inches
	 */
	public static void drawLine(Graphics2D g2, Point start, Point end) {
		int x1 = (int) (start.getX() * AutoSim.PPI);
		int y1 = (int) (start.getY() * AutoSim.PPI);
		int x2 = (int) (end.getX() * AutoSim.PPI);
		int y2 = (int) (end.getY() * AutoSim.PPI);
		
		g2.drawLine(y1, x1, y2, x2);
	} //end drawLine
	
	/**
	 * Set the font of a Graphics2D object
	 * @param g2 Used for drawing
	 * @param filename Directory of the font file
	 * @param fontSize Value to scale pixels per inch by
	 */
	public static Font createFont(String filename, int fontSize) {
		Font f = new Font(Font.SANS_SERIF, Font.PLAIN, fontSize); //default font
		
		try {
			f = Font.createFont(Font.TRUETYPE_FONT, new File(filename));
			f = f.deriveFont((float) fontSize);
		} catch (Exception e) {} //try-catch
		
		return f; //return default font if not able to load from file
	} //end createFont
	
	/**
	 * Set the transparency of the Graphics2D object
	 * @param g2 Used for drawing
	 * @param transparency Transparency factor from 0 to 1 inclusive
	 */
	public static void setTransparency(Graphics2D g2, double transparency) {
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) transparency));
	} //end setTransparency
	
	/**
	 * Draw a empty circle in real space
	 * @param g2 Used for drawing
	 * @param p Point to draw
	 * @param rad Radius of point
	 */
	public static void drawEmptyCircle(Graphics2D g2, Point p, int rad) {
		rad *= AutoSim.PPI;
		int x = (int) (p.getX() * AutoSim.PPI);
		int y = (int) (p.getY() * AutoSim.PPI);
		
		g2.drawOval((int) (y - rad/2.0), (int) (x - rad/2.0), rad, rad);
	} //end drawPoint
} //end Painter 
