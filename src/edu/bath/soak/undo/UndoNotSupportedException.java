package edu.bath.soak.undo;

import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.cmd.ExecutableCommand;

/*******************************************************************************
 * Indicates that a given commamdn cannot be undone, should be thrown in the
 * command undo expansion process
 * 
 * @see CommandProcessor#expandUndo(ExecutableCommand,
 *      edu.bath.soak.cmd.BaseCompositeCommand)
 * 
 * @author cspocc
 * 
 */
public class UndoNotSupportedException extends UnsupportedOperationException {
	ExecutableCommand command;

	public UndoNotSupportedException(ExecutableCommand command,
			CmdException cause) {
		super(
				"Command could not be undone because of other changes in the system",
				cause);
		this.command = command;
	}

	public UndoNotSupportedException(ExecutableCommand command) {
		super("Undoing of command is not supportedf");
		this.command = command;
	}

	public UndoNotSupportedException(ExecutableCommand command, String message) {
		super(message);
		this.command = command;
	}

	public ExecutableCommand getCommand() {
		return command;
	}

	public void setCommand(ExecutableCommand command) {
		this.command = command;
	}

}
