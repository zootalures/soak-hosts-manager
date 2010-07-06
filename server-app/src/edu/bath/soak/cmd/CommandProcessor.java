package edu.bath.soak.cmd;

import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.Transactional;

import edu.bath.soak.undo.UndoNotSupportedException;

/**
 * Abstract base interface for all classes which implement changes to particular
 * subsystems.
 * 
 * Once a set of changes are determined, they can be validated and then
 * implemented
 * 
 * @author cspocc
 * 
 * @param <CmdType>
 */
public interface CommandProcessor<CmdType extends ExecutableCommand> extends
		Ordered {

	/**
	 * Validate a given change. This is called as a last-chance error checking
	 * procedure before changes are implemented
	 * 
	 * @param baseCommand
	 *            TODO
	 * @param change
	 * 
	 * @throws CmdException
	 */
	@Transactional
	public void implementCmd(BaseCompositeCommand baseCommand, CmdType change)
			throws CmdException;

	/**
	 * Expands the inverse of a command into the given command context.
	 * 
	 * In the case that a processor's commands are not undoable, or that one of
	 * the commands in the context is not undoable, the processor should throw
	 * an {@link UndoNotSupportedException}
	 * 
	 * @param cmd
	 * @param result
	 * @throws CmdException
	 *             if a command could not be expanded
	 * @throws UndoNotSupportedException
	 *             if this undo operation could not proceed (should not be
	 *             treated as a runtime error by caller)
	 */
	public void expandUndo(CmdType cmd, BaseCompositeCommand result)
			throws CmdException, UndoNotSupportedException;

	public boolean supportsCmdType(Class type);

}
