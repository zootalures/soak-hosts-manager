package edu.bath.soak.cmd;

/**
 * Thread local storage for {@link CommandContext} objects.
 * 
 * This class mediates access to the current command context when commands are
 * being executed, allowing low-level objects to get access to the command which
 * is currently being run via the static methods in this class.
 * 
 * 
 * Command contexts are thread local, and we assume that only one command is
 * executed at a given time.
 * 
 * Contexts are set by
 * {@link CommandDispatcherRegistry#implementSubCommand(BaseCompositeCommand, ExecutableCommand)}
 * 
 * @author cspocc
 * 
 */
public class CommandContextHolder {
	static ThreadLocal<CommandContext> commandContextHolder = new ThreadLocal<CommandContext>();

	public static CommandContext getCurrentCommandContextHolder() {
		return commandContextHolder.get();
	}

	public static void setCommandContext(CommandContext ctx) {
		commandContextHolder.set(ctx);
	}

	public static void clearCommandContext() {
		commandContextHolder.set(null);
	}

	/**
	 * returns the current base command from the command context if present,
	 * 
	 * @return the current base compostite command or null if called from
	 *         outside a command context
	 */
	public static BaseCompositeCommand currentBaseCommand() {
		CommandContext cc = commandContextHolder.get();
		if (cc != null) {
			return cc.getCurrentBaseCommand();
		}
		return null;
	}

	/**
	 * Returns the current {@link ExecutableCommand} command which is being
	 * implemented
	 * 
	 * @return the current command being iumplemented or null if called from
	 *         outside a command context.
	 */
	public static ExecutableCommand currentExecutableCommand() {
		CommandContext cc = commandContextHolder.get();
		if (cc != null) {
			return cc.getCurrentExecutableCommand();
		}
		return null;
	}

	public static String currentCommandId() {
		CommandContext cc = commandContextHolder.get();
		if (cc != null) {
			if (cc.getCurrentBaseCommand() != null) {
				return cc.getCurrentBaseCommand().getCommandId();
			}
		}
		return null;
	}

}
