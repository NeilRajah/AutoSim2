/**
 * Component
 * @author Neil Balaskandarajah
 * Created on: 08/11/2019
 * Interface for all graphical components
 */
package graphics;

import javax.swing.JComponent;

public abstract class Component extends JComponent {

	public abstract void update(double[] values);
} //end Component
