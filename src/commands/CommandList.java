/**
 * CommandList
 * Author: Neil Balaskandarajah
 * Created on: 20/01/2020
 * A list of Commands to be added to a CommandGroup
 */
package commands;

public class CommandList extends CommandGroup {

	/**
	 * Add a variable number of commands to a CommandGroup
	 * @param commands - list of commands to add
	 */
	public CommandList(Command ... commands) {
		for (Command c : commands) {
			this.add(c);
		} //loop
	} //end constructor
} //end class
