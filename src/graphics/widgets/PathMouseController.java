/**
 * PathMouseController
 * Author: Neil Balaskandarajah
 * Created on: 09/04/2020
 * Mouse controller for creating a path
 */

package graphics.widgets;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.function.BiConsumer;

import util.Util;
import graphics.Environment;
import graphics.GraphicBezierPath;
import graphics.Painter;
import graphics.components.BoxButton;
import graphics.components.BoxButton.BUTTON_STATE;
import main.AutoSim;
import model.FieldPositioning;
import model.Point;

public class PathMouseController implements MouseListener, MouseMotionListener, MouseWheelListener {
	//Attributes
	private BezierPathCreator bpc; //path creator component
	private GraphicBezierPath path; //path with points
	private int currentCircIndex; //index for the current circle
	private boolean dragging; //whether a circle is being dragged
	
	/**
	 * Create a mouse controller with a method to update circles and the curve
	 * @param bpc BezierPathCreator component with path
	 */
	public PathMouseController(BezierPathCreator bpc) {
		//set attributes
		this.bpc = bpc;
		this.path = this.bpc.getCurve();
		this.currentCircIndex = 0;
		this.dragging = false;
	} //end constructor

	/**
	 * Update the circles based on mouse position
	 * @param m Cursor information
	 */
	public void mouseMoved(MouseEvent m) {
		Point ms = getMouseCoordsInches(m);
		Circle[] circles = path.getCircles();
		
		if (!dragging) {
			//only search through points if over a circle
			for (int i = 0; i < circles.length; i++) {
				Circle c = circles[i]; //current circle
				//if mouse is within a circle
				boolean over = FieldPositioning.isWithinBounds(c, ms, Painter.CIRCLE_RAD / (double) AutoSim.PPI); 
				currentCircIndex = over ? i : -1;
				
				if (over) { //set to hover
					Environment.getInstance().setCursor(new Cursor(Cursor.HAND_CURSOR)); //hand cursor
					bpc.requestCircleUpdate(currentCircIndex, BUTTON_STATE.HOVER);
					bpc.requestButtonUpdate(currentCircIndex, BUTTON_STATE.HOVER);

					break; //no need to loop through rest of points
					
				} else { //set to default
					Environment.getInstance().setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //regular cursor
					bpc.requestCircleUpdate(i, BUTTON_STATE.DEFAULT);
					bpc.requestButtonUpdate(i, BUTTON_STATE.DEFAULT);
				} //if
			} //loop
		} //if
	} //end mouseMoved

	/**
	 * Update the current circle on press
	 * @param m Cursor information
	 */
	public void mousePressed(MouseEvent m) {
		//if over a circle
		if (currentCircIndex != -1) {
			bpc.requestCircleUpdate(currentCircIndex, BUTTON_STATE.LOCK);
			bpc.requestButtonUpdate(currentCircIndex, BUTTON_STATE.LOCK);
		} //if
	} //end mousePressed

	/**
	 * Update the current circle on release
	 * @param m Cursor information
	 */
	public void mouseReleased(MouseEvent m) {
		//if over a circle
		if (currentCircIndex != -1 && !path.getCircles()[currentCircIndex].isLocked()) {
			bpc.requestCircleUpdate(currentCircIndex, BUTTON_STATE.HOVER);
			bpc.requestButtonUpdate(currentCircIndex, BUTTON_STATE.HOVER);
		} //if
		
		dragging = false; //can't be dragging if mouse isn't pressed
	} //end mouseReleased
	
	/**
	 * Move the current point to where the mouse is dragged
	 * @param m Cursor information
	 */
	public void mouseDragged(MouseEvent m) {
		if (currentCircIndex != -1) {
			dragging = true;
			bpc.setCircle(currentCircIndex, getMouseCoordsInches(m));
		} //if
	} //end mouseDragged
	
	/**
	 * Get the mouse coordinates in real space
	 * @param m MouseEvent with cursor information
	 * @return (x,y) mouse location in inches
	 */
	private Point getMouseCoordsInches(MouseEvent m) {
		return new Point(m.getY() / (double) AutoSim.PPI, m.getX() / (double) AutoSim.PPI);
	} //end getMouseCoordsInches
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent m) {
		
	}

	@Override
	public void mouseClicked(MouseEvent m) {
		// TODO Auto-generated method stub

	}
	
	/*
	 * Unimplemented
	 */
	public void mouseEntered(MouseEvent m) {}

	/*
	 * Unimplemented
	 */
	public void mouseExited(MouseEvent m) {}
} //end class
