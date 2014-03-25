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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import de.maxgb.minecraft.second_screen.util.Logger;

public class SocketListener implements Runnable {

	private int port;
	private String hostname;
	private InetAddress inetAddress;
	private boolean running;
	private Thread thread;
	private ServerSocket socket;
	private List<SocketHandler> socketList;
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

		// Registering to Eventbus
		FMLCommonHandler.instance().bus().register(this);

		Logger.i(TAG, "Starting Listener Thread on " + inetAddress.toString()
				+ ":" + port);
		thread = new Thread(this, "SecondScreen - SocketListener");
		thread.start();
	}

	private void closeAll() {
		Logger.i(TAG,"Stopping all handlers");
		synchronized (socketList) {
			for (SocketHandler handler : socketList) {
				try {
					handler.stopping();
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
			SecondScreenMod.connected = true;
			return true;
		} catch (Exception e) {
			Logger.e(TAG, "Failed to create socket", e);
			SecondScreenMod.connected = false;
			if (e.getMessage().contains("JVM_Bind")) {

				SecondScreenMod.error = "Port already in use";
			}
			return false;
		}
	}

	@Override
	public void run() {
		FMLLog.log(Level.DEBUG, "Starting SocketListener");
		if (!init()) {
			Logger.e(TAG, "Failed to start SocketListener");
		}
		try {
			while (running) {
				try {
					SocketHandler handler = new SocketHandler(socket.accept());
					socketList.add(handler);

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
				
				
			} catch (Exception e) {
				Logger.e(TAG, "Error while closing socket",e);
			}
			try{
				closeAll();
			}
			catch(Exception e){
				Logger.e(TAG, "Error while closing handlers",e);
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

	@SubscribeEvent
	public void tick(ServerTickEvent e) {
		synchronized (socketList) {
			for (int i = 0; i < socketList.size(); i++) {
				socketList.get(i).tick();
				if (socketList.get(i).remove) {
					socketList.remove(i);
					i--;
				}
			}
		}
	}

}
