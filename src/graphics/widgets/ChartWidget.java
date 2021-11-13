/**
 * ChartWidget
 * Author: Neil Balaskandarajah
 * Created on: 04/08/2020
 * Widget containing a chart
 */

package graphics.widgets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;

import graphics.Environment;
import main.AutoSim;
import util.Util.ROBOT_KEY;

public class ChartWidget extends Widget {
	//Attributes
	private XYChart chart; //chart that plots data
	private List<Double> timeData; //list of all times data was sent
	private List<Double> outputData; //list of all data to output
	private String name; //name of the data series
	private DoubleFunction<Double> keyOperation; //operation to perform on the key before saving to chart
	private double initialY; //starting y value

	/**
	 * Create a chart widget
	 * @param chart Chart to display
	 * @param key Key to get from the WidgetHub
	 * @param name Name of the chart
	 */
	public ChartWidget(XYChart chart, ROBOT_KEY key, String name) {
		super(new XChartPanel<XYChart>(chart));
		
		//Key to use to identify robot data to get
		this.keyArray = new ROBOT_KEY[] {key};
		
		//Set the attributes
		this.chart = chart;
		this.name = name;
		this.timeData = new ArrayList<Double>();
		this.timeData.add(0.0);
		this.outputData = new ArrayList<Double>();
		this.initialY = 0.0;
		this.outputData.add(initialY);
		
		//Configure the chart
		this.chart.addSeries(name, timeData, outputData);
		this.chart.setTitle(name);
		this.chart.getStyler().setLegendVisible(false);
		this.chart.getStyler().setMarkerSize(AutoSim.PPI * 2);
	}
	
	/**
	 * Reset the data in the chart
	 */
	public void reset() {
		timeData.clear();
		timeData.add(0.0);
		outputData.clear();
		outputData.add(initialY);
	}
	
	/**
	 * Update the widget
	 * @param values Values recieved from the WidgetHub
	 */
	public void update(double[] values) {
		this.timeData.add(Environment.getTime());
		this.outputData.add(keyOperation != null ? keyOperation.apply(values[0]) : values[0]);
		chart.updateXYSeries(name, timeData, outputData, null);
		this.getComponent().repaint();
	}
	
	/**
	 * Set the color of the chart
	 * @param c Chart color
	 */
	public void setColor(Color c) {
		chart.getStyler().setSeriesColors(new Color[] {c});
	}
	
	/**
	 * Set the function to perform on the output data before entering it into the chart
	 * @param keyOp Takes a double, returns a double
	 */
	public void setKeyOperation(DoubleFunction<Double> keyOp) {
		this.keyOperation = keyOp;
	}
	
	/**
	 * Set the initial Y value of the chart
	 * @param initY The first Y value in the chart
	 */
	public void setInitialY(double initY) {
		this.initialY = initY;
		outputData.clear();
		outputData.add(initialY);
		chart.updateXYSeries(name, timeData, outputData, null);
	}
}
