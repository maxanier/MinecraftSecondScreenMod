package de.maxgb.minecraft.second_screen;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;
import de.maxgb.minecraft.second_screen.util.Logger;

public class SocketListener implements Runnable {

	private int port;
	private String hostname;
	private InetAddress inetAddress;
	private boolean running;
	private Thread thread;
	private ServerSocket socket;
	public List<SocketHandler> socketList;
	private final String TAG = "SocketListener";

	public SocketListener() {
		port = SecondScreenMod.port;
		hostname = SecondScreenMod.hostname;

		if (hostname.length() > 0) {
			try {
				inetAddress = InetAddress.getByName(hostname);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		if (port == 0) {

			port = MinecraftServer.getServer().getPort();
		}

		socketList = Collections
				.synchronizedList(new ArrayList<SocketHandler>());
		Logger.i(TAG, "Starting Listener Thread on " + inetAddress.toString()
				+ ":" + port);
		thread = new Thread(this, "SecondScreen - SocketListener");
		thread.start();
	}

	private void closeAll() {
		FMLLog.log(Level.DEBUG, "Closing all SocketHandler");
		synchronized (socketList) {
			for (SocketHandler handler : socketList) {
				try {
					handler.close();
				} catch (Exception e) {
				}
			}
			socketList.clear();
		}
	}

	private boolean init() {
		try {
			socket = new ServerSocket(port, 0, inetAddress);
			running = true;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void run() {
		FMLLog.log(Level.DEBUG, "Starting SocketListener");
		if (!init()) {
			FMLLog.log(Level.ERROR, "Failed to start SocketListener");
		}
		try {
			while (running) {
				try {
					synchronized (socketList) {
						socketList
								.add(new SocketHandler(socket.accept(), this));
					}

				} catch (SocketException e) {
					stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				closeAll();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		FMLLog.log(Level.DEBUG, "Stopping SocketListener");
		running = false;
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeAll();
		socket = null;
		System.gc();

	}

}
