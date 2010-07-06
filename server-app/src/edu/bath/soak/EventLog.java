package edu.bath.soak;

import org.apache.log4j.Logger;

public class EventLog {
	static Logger log = Logger.getLogger(EventLog.class);

	public static Logger log() {
		return log;
	}
}
