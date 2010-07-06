package edu.bath.soak.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.bath.soak.undo.UndoManagerImpl;

/**
 * Representation of an expanded command, including all sub-commands.
 * 
 * This object includes a copy of the user interface command ( {@link UICommand})
 * in {@link #baseChange} and each of the underlying implementation ({@link ExecutableCommand})
 * commands associated with implementing this command in {@link #commands}.
 * 
 * 
 * This object is created by the {@link CommandDispatcherRegistryImpl} and
 * passed through one or more {@link CommandExpander} objects.
 * 
 * Once a command is constructed it can be previewed, and subsequently
 * implemented through the {@link CommandDispatcherRegistryImpl} which calls a
 * {@link CommandProcessor} for each base command.
 * 
 * @author cspocc
 * 
 */
@XmlType()
@XmlAccessorType(value = XmlAccessType.PROPERTY)
@XmlRootElement()
public class BaseCompositeCommand implements Serializable {
	List<ExecutableCommand> commands = new ArrayList<ExecutableCommand>();
	String commandId;

	public BaseCompositeCommand() {
	}

	public BaseCompositeCommand(UICommand baseChange) {
		this.baseChange = baseChange;
	}

	UICommand baseChange;

	public void setCommands(List<ExecutableCommand> commands) {
		this.commands = commands;
	}

	/**
	 * The User interface command which is being expanded
	 * 
	 * @return
	 */
	@XmlAnyElement(lax = true)
	public UICommand getBaseChange() {
		return baseChange;
	}

	public void setBaseChange(UICommand baseCommand) {
		this.baseChange = baseCommand;
	}

	/**
	 * A list of commands "caused" by the expansion of the command in
	 * {@link #baseChange}
	 * 
	 * @return
	 */
	@XmlElementWrapper
	@XmlElementRef
	public List<ExecutableCommand> getCommands() {
		return commands;
	}

	public List<ExecutableCommand> getAggregateChanges() {
		return getCommands();
	}

	/**
	 * A globally unique command identifier which is set by the
	 * {@link CommandDispatcherRegistryImpl} when this command is expanded This
	 * can be used by other beans (such as the {@link UndoManagerImpl} to refer
	 * back to this command and its properties.
	 * 
	 * @return
	 */
	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public void appendCommand(ExecutableCommand command) {
		getAggregateChanges().add(command);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((baseChange == null) ? 0 : baseChange.hashCode());
		result = prime * result
				+ ((commandId == null) ? 0 : commandId.hashCode());
		result = prime * result
				+ ((commands == null) ? 0 : commands.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BaseCompositeCommand other = (BaseCompositeCommand) obj;
		if (baseChange == null) {
			if (other.baseChange != null)
				return false;
		} else if (!baseChange.equals(other.baseChange))
			return false;
		if (commandId == null) {
			if (other.commandId != null)
				return false;
		} else if (!commandId.equals(other.commandId))
			return false;
		if (commands == null) {
			if (other.commands != null)
				return false;
		} else if (!commands.equals(other.commands))
			return false;
		return true;
	}
}
