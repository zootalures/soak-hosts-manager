/**
 * Controllers for storing and recovering commands for undo operations
 * 
 * Undo is supported with two seperate systems:
 * 
 * each "undoable" manager is expected via its data access layer to support
 * storing relevant state to support command reversal.
 * 
 * The UndoManager itself stores an image (serialized as XML) of each expanded
 * {@link BaseCompositeCommand} after it is executed via the
 * {@link CommandDispatcherRegistryimpl}
 * 
 * When a command is undone via the {@link UndoCmd} the {@link UndoManagerImpl} 
 * object expands the undo by calling back to {@link CommandProcessor#expandUndo(edu.bath.soak.cmd.ExecutableCommand, edu.bath.soak.cmd.BaseCompositeCommand)} processors may optionally raise {@link UndoNotSupportedException}s at this point to indcate that a command can't be undone. 
 * 
 * Expanders 
 * 
 */
package edu.bath.soak.undo;

import edu.bath.soak.undo.UndoNotSupportedException;
import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.cmd.BaseCompositeCommand;

