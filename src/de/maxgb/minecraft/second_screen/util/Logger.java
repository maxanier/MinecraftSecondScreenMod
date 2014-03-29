package de.maxgb.minecraft.second_screen.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;

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
		String stacktrace = "";
		PrintStream p;
		try {
			p = new PrintStream(stacktrace);
			t.printStackTrace(p);
		} catch (FileNotFoundException e1) {
			stacktrace = t.getMessage();
		}
		log(Level.ERROR, "[" + tag + "]" + msg + "\nStacktrace: " + stacktrace);
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
