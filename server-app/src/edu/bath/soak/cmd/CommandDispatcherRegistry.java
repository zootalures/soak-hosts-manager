package edu.bath.soak.cmd;

import java.util.Collection;

import edu.bath.soak.undo.UndoNotSupportedException;

/**
 * Interface for central command dispatch
 * 
 * @see CommandDispatcherRegistryImpl
 * @author cspocc
 * 
 */
public interface CommandDispatcherRegistry {

	public void registerDispatcher(CommandProcessor dispatcher);

	public void registerExpander(CommandExpander dispatcher);

	/**
	 * Translates the given UI command into an executable command by expanding
	 * it
	 * 
	 * @param command
	 * @return
	 * @throws CmdException
	 *             if the expansion failed (i.e. in the process of validation)
	 */
	public BaseCompositeCommand expandCommand(UICommand command);

	/**
	 * Allows a command expander to append the expansion of another UI command
	 * to the current execution,
	 * 
	 * The expansion of command is should be resolved and added to the command
	 * components in baseCommand
	 * 
	 * N.B. is is the responsibility of command expanders to ensure that the
	 * expansion process is loop free
	 * 
	 * @param command
	 * @param baseCommand
	 */
	public void expandSubCommand(UICommand command,
			BaseCompositeCommand baseCommand);

	/**
	 * Implements the specified executable command
	 * 
	 * @param cmd
	 */
	public void implementBaseCommand(BaseCompositeCommand cmd);

	public void expandUndoSubCommand(ExecutableCommand command,
			BaseCompositeCommand baseCommand) throws UndoNotSupportedException;

	public void implementSubCommand(BaseCompositeCommand base,
			ExecutableCommand cmd);

	/**
	 * Performs a full command expansion and executes the result,
	 * 
	 * @param command
	 * @return
	 */
	public BaseCompositeCommand expandAndImplementCommand(UICommand command);

	public void setUpCommandDefaults(UICommand commmand);

	public void setCommandExpanders(Collection<CommandExpander> expanders);

	public void setCommandProcessors(Collection<CommandProcessor> executors);

}
