package edu.bath.soak.undo;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CommandDispatcherRegistry;

/**
 * Interface to the underlying Undo subsystem which is used to notify the system
 * when a command has been executed and its state needs saving
 * 
 * This interface is used by the {@link CommandDispatcherRegistry}
 * 
 * @author cspocc
 * 
 */
public interface UndoManager {
	public void saveUndoState(BaseCompositeCommand command);
}
