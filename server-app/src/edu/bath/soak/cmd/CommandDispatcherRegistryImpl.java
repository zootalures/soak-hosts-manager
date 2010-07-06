package edu.bath.soak.cmd;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.bath.soak.undo.UndoManager;
import edu.bath.soak.undo.UndoNotSupportedException;
import edu.bath.soak.util.OrderedComparator;

/**
 * Implementation of the {@link CommandDispatcherRegistry} interface
 * 
 * The command dispatcher singleton bean, this bean is used as an interface for
 * expanding, implementing and undoing "command" objects.
 * 
 * The registry stores a collection of {@link CommandExpander} objects which
 * translate {@link UICommand} objects into {@link ExecutableCommand} objects
 * and a collection of {@link CommandProcessor} objects which in turn implement
 * those {@link ExecutableCommand} objects.
 * 
 * The anatomy of a soak command is as follows:
 * 
 * 
 * To implement a {@link UICommand} command:
 * <ol>
 * <li> An upper tier bean (e.g. a web controller) creates a bean derived from
 * {@link UICommand} and sets any required properties.</li>
 * 
 * <li> The upper tier bean calls {@link #expandAndImplementCommand(UICommand)}</li>
 * <li>The command dispatcher registry creates a {@link BaseCompositeCommand}
 * for the implementation of the user command</li>
 * <li> The dispatcher passes the {@link UICommand} through each of the
 * registered {@link CommandExpander} via
 * {@link CommandExpander#expandCmd(UICommand, BaseCompositeCommand)} beans
 * which handle the appropriate type of {@link UICommand}.</li>
 * <li> Each {@link CommandExpander} can add zero or more
 * {@link ExecutableCommand} objects into the {@link BaseCompositeCommand}
 * object, these repesent the actual implementation of the related
 * {@link UICommand}.</li>
 * <li> {@link CommandExpander} may fail in the expansion phase (With
 * {@link CmdException} exceptions</li>
 * <li> Once the {@link BaseCompositeCommand} is fully expanded, each
 * {@link ExecutableCommand} sub-command is passed to the
 * {@link CommandProcessor} object (via
 * {@link CommandProcessor#implementCmd(BaseCompositeCommand, ExecutableCommand)}
 * which is registered to handle that type of command</li>
 * <li> the relevant {@link CommandProcessor} performs the necesary changes to
 * implement that command</li>
 * </ol>
 * 
 * @author cspocc
 * 
 */
@Transactional
public class CommandDispatcherRegistryImpl implements CommandDispatcherRegistry {
	Logger log = Logger.getLogger(CommandDispatcherRegistry.class);
	SortedSet<CommandProcessor> commandProcessors = new TreeSet<CommandProcessor>(
			new OrderedComparator());
	SortedSet<CommandExpander> commandExpanders = new TreeSet<CommandExpander>(
			new OrderedComparator());

	UndoManager undoManager;

	public String generateCommandId() {
		return UUID.randomUUID().toString();
	}

	public int getOrder() {
		return 0;
	}

	public void setUpCommandDefaults(UICommand command) {
		for (CommandExpander cmdExpander : commandExpanders) {
			if (cmdExpander.canExpand(command.getClass())) {
				cmdExpander.setupCommand(command);
			}
		}
	}

	/**
	 * Iterates through the registered command expanders and applies any side
	 * effects of the command to the execution context.
	 * 
	 * @param command
	 *            the Base UI command to expand
	 * @param baseCommand
	 *            the composite command to expand command into
	 * @throws CmdException
	 *             if one of the expanders throws an exception while expanding
	 *             the command
	 * 
	 */
	public void expandSubCommand(UICommand command,
			BaseCompositeCommand baseCommand) throws CmdException {
		int numExpanders = 0;
		for (CommandExpander commandExpander : commandExpanders) {
			if (commandExpander.canExpand(command.getClass())) {
				log.trace("Found command expansion for command " + command
						+ " in expander " + commandExpander);
				commandExpander.expandCmd(command, baseCommand);
				numExpanders++;
			}
		}
		if (numExpanders == 0) {
			throw new CmdException(
					"command "
							+ command.getCommandDescription()
							+ " was not expanded by any listeners, this is probably wrong");
		}

	}

	/**
	 * Iterates through the registered command expanders and applies any side
	 * effects of the command to the execution context
	 * 
	 * @param command
	 *            the command to undo
	 * @param baseCommand
	 *            the resulting base composite command to expand the undo of the
	 *            given command into
	 * 
	 * @throws UndoNotSupportedException
	 *             if the command processor which handles commands of type
	 *             command cannot undo this command
	 */
	public void expandUndoSubCommand(ExecutableCommand command,
			BaseCompositeCommand baseCommand) throws UndoNotSupportedException {

		for (CommandProcessor commandProcessor : commandProcessors) {
			if (commandProcessor.supportsCmdType(command.getClass())) {
				log.trace("Found controller for undo of  " + command + " in "
						+ commandProcessor);
				commandProcessor.expandUndo(command, baseCommand);
				return;
			}
		}
		throw new UndoNotSupportedException(command,
				"Can't find a controller which can undo this command");

	}

	/**
	 * Expands the given command into the execution context.
	 * 
	 * This takes a given {@link UICommand} object, and passes it through the
	 * configured list of {@link CommandExpander} objects
	 * 
	 * Each expander may convert the UICommand into zero or more
	 * {@link ExecutableCommand} objects
	 * 
	 * @param command
	 *            the command to expand
	 * @return A new {@link BaseCompositeCommand} containing the expansion of
	 *         command
	 * @throws CmdException
	 *             if an error occured while expanding the command
	 */
	public BaseCompositeCommand expandCommand(UICommand command) {
		BaseCompositeCommand composite = new BaseCompositeCommand(command);
		composite.setCommandId(generateCommandId());
		expandSubCommand(command, composite);
		return composite;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void implementSubCommand(BaseCompositeCommand base,
			ExecutableCommand change) {
		try {
			CommandContextHolder.setCommandContext(new CommandContext(base,
					change));

			// TODO Auto-generated method stub
			log.trace("Implementing sub command " + change);

			for (CommandProcessor commandProcessor : commandProcessors) {
				if (commandProcessor.supportsCmdType(change.getClass())) {
					commandProcessor.implementCmd(base, change);
					log.trace("Command " + change + " completed by  "
							+ commandProcessor);

					return;
				}
			}
			throw new RuntimeException(
					"Can't find processor for command of type "
							+ change.getClass());
		} finally {
			CommandContextHolder.clearCommandContext();
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public void implementBaseCommand(BaseCompositeCommand change)
			throws CmdException {
		Assert.notNull(change);

		log.trace("Implementing base command " + change);

		for (ExecutableCommand cmd : ((BaseCompositeCommand) change)
				.getCommands()) {
			implementSubCommand(change, cmd);
		}

		if (undoManager != null)
			undoManager.saveUndoState(change);
		log.trace("Base command " + change + " complete");
		return;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
	public BaseCompositeCommand expandAndImplementCommand(UICommand command) {
		BaseCompositeCommand cmd = expandCommand(command);
		implementBaseCommand(cmd);
		return cmd;
	}

	public void registerDispatcher(CommandProcessor dispatcher) {
		commandProcessors.add(dispatcher);
	}

	public void registerExpander(CommandExpander dispatcher) {
		commandExpanders.add(dispatcher);
	}

	public void setCommandProcessors(Collection<CommandProcessor> executors) {
		commandProcessors.clear();
		commandProcessors.addAll(executors);

	}

	public void setCommandExpanders(Collection<CommandExpander> expanders) {
		commandExpanders.clear();
		commandExpanders.addAll(expanders);
	}

	public void setUndoManager(edu.bath.soak.undo.UndoManager undoManager) {
		Assert.notNull(undoManager);
		this.undoManager = undoManager;
	}
}
