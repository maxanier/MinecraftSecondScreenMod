package de.maxgb.minecraft.second_screen.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraftforge.common.DimensionManager;
import de.maxgb.minecraft.second_screen.util.Logger;

/**
 * Class which should manage all file related operations
 * @author Max
 *
 */
public class DataStorageDriver {
	private final static String TAG = "DataStorageDriver";

	public static File getSaveDir() {
		return new File(DimensionManager.getCurrentSaveRootDirectory(), "secondscreen");
	}

	/**
	 * Reads lines from given file
	 * @param file
	 * @return lines, null if exception
	 */
	private static ArrayList<String> readFromFile(File file) {

		if (!file.exists()) {
			Logger.i(TAG, "File: " + file.getPath() + " does not exist");
			return null;
		}
		ArrayList<String> lines = new ArrayList<String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
			return lines;

		} catch (FileNotFoundException e) {
			Logger.w(TAG, "File: " + file.getAbsolutePath() + " not found");
			return null;
		} catch (IOException e) {
			Logger.e(TAG, "Failed to read from file: " + file.getAbsolutePath(), e);
			return null;
		}
	}

	/**
	 * Reads from a file in the world folder
	 * @param filename
	 * @return lines of file, null if error
	 */
	public static ArrayList<String> readFromWorldFile(String filename) {
		File f = new File(getSaveDir(), filename);

		return readFromFile(f);

	}


	private static void writeToFile(File file, ArrayList<String> lines) {
		try {
			getSaveDir().mkdirs();
			file.createNewFile();
			file.setReadable(true);
			file.setWritable(true);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			for (String line : lines) {
				writer.append(line);
				writer.newLine();
			}
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
			Logger.e(TAG, "Failed writing the String Array to the file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * Writes to a file in the world folder
	 * @param filename
	 * @param lines
	 */
	public static void writeToWorldFile(String filename, ArrayList<String> lines) {
		File f = new File(getSaveDir(), filename);
		writeToFile(f, lines);

	}
}
