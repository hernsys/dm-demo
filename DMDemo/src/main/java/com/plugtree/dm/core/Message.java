package com.plugtree.dm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Message to be shown, includes a Severity Level and Message msg.
 * 
 * @author Ezequiel Grande
 * 
 */
public class Message {
	private Level level;
	private String msg;

	public enum Level {
		INFO, DEBUG, WARN, ERROR
	};

	public Message(Level level, String msg) {
		this.level = level;
		this.msg = msg;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static Message newInfoMessage(String msg) {
		return new Message(Level.INFO, msg);
	}

	public static Message newDebugMessage(String msg) {
		return new Message(Level.DEBUG, msg);
	}

	public static Message newWarningMessage(String msg) {
		return new Message(Level.WARN, msg);
	}

	public static Message newErrorMessage(String msg) {
		return new Message(Level.ERROR, msg);
	}

	public String toString() {
		return "[" + level + "] " + msg;
	}
}
