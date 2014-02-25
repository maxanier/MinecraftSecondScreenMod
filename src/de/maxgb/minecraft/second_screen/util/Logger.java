package de.maxgb.minecraft.second_screen.util;

import org.apache.logging.log4j.Level;

public class Logger {
	private static org.apache.logging.log4j.Logger log;

	public static void d(String tag, String msg) {
		log(Level.DEBUG, "[" + tag + "]" + msg);
	}

	public static void e(String tag, String msg) {
		log(Level.ERROR, "[" + tag + "]" + msg);
	}

	public static void e(String tag, String msg, Throwable t) {
		log(Level.ERROR,
				"[" + tag + "]" + msg + "\nMessage: " + t.getLocalizedMessage());
	}

	public static void i(String tag, String msg) {
		log(Level.INFO, "[" + tag + "]" + msg);
	}

	public static void init(org.apache.logging.log4j.Logger logger) {
		log = logger;

	}

	private static void log(Level level, String msg) {
		log.log(level, msg);
	}

	public static void w(String tag, String msg) {
		log(Level.WARN, "[" + tag + "]" + msg);
	}
}
