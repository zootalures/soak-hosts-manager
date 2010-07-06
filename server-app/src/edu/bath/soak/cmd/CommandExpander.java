package edu.bath.soak.cmd;

import org.springframework.core.Ordered;

/**
 * Interface for controllers which expand commands
 * 
 * @author cspocc
 * 
 * @see CommandProcessor#implementCmd(BaseCompositeCommand, AbstractCmd)
 * 
 * @param <CmdType>
 *            The type of commands expanded by this controller
 */
public interface CommandExpander<CmdType extends UICommand> extends Ordered {
	/**
	 * Allows an expander to pre-populate a command with any additional flags
	 * (see {@link UICommand#getContextFlags())}
	 * 
	 * @param cmd
	 */
	public void setupCommand(CmdType cmd);

	/**
	 * Expands a command into the given command context.
	 * 
	 * @param cmd
	 * @param result
	 * @throws CmdException
	 *             if a command could not be expanded
	 */
	public void expandCmd(CmdType cmd, BaseCompositeCommand result)
			throws CmdException;

	public boolean canExpand(Class clazz);
}
