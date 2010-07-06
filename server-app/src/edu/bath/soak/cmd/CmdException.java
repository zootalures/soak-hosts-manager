package edu.bath.soak.cmd;

/**
 * Generic exception throw by a command processor
 * 
 * Typically thes execeptions should be treated as terminal
 * 
 * @author cspocc
 * 
 */
public class CmdException extends RuntimeException {

	private static final long serialVersionUID = 3882794539315645041L;

	public CmdException() {
		super();
	}

	public CmdException(String msg) {
		super(msg);
	}

	public CmdException(Throwable msg) {
		super(msg);
	}

	public CmdException(String msg, Throwable t) {
		super(msg, t);
	}

}