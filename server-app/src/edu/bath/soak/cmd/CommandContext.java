package edu.bath.soak.cmd;

/**
 * Thread-local context information relating to the current command
 * 
 * Allows called object to get information about the command which is currently
 * being executed
 * 
 * @author cspocc
 * 
 */
public class CommandContext {
	BaseCompositeCommand currentBaseCommand;
	ExecutableCommand currentExecutableCommand;

	public CommandContext(BaseCompositeCommand bcc,
			ExecutableCommand executableCommand) {
		currentBaseCommand = bcc;
	}

	public BaseCompositeCommand getCurrentBaseCommand() {
		return currentBaseCommand;
	}

	public void setCurrentBaseCommand(BaseCompositeCommand currentCommand) {
		this.currentBaseCommand = currentCommand;
	}

	public ExecutableCommand getCurrentExecutableCommand() {
		return currentExecutableCommand;
	}

	public void setCurrentExecutableCommand(
			ExecutableCommand currentExecutableCommand) {
		this.currentExecutableCommand = currentExecutableCommand;
	}

}
