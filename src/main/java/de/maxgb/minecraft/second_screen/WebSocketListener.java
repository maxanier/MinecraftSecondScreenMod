package de.maxgb.minecraft.second_screen;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import net.minecraft.server.MinecraftServer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import de.maxgb.minecraft.second_screen.util.ForceUpdateEvent;
import de.maxgb.minecraft.second_screen.util.Logger;

public class WebSocketListener {

	private class MSSWebSocketServer extends WebSocketServer {
		private final static String TAG = "MSSWebSocketServer";

		public MSSWebSocketServer(InetSocketAddress address) {
			super(address);
		
		}

		@Override
		public void onClose(WebSocket conn, int code, String reason,
				boolean remote) {

			WebSocketHandler h = handlers.get(conn.getRemoteSocketAddress());
			if (h != null) {
				synchronized (handlers) {
					h.close();
					handlers.remove(conn.getRemoteSocketAddress());
					Logger.i(TAG, "Closed Handler/Connection for "
							+ conn.getRemoteSocketAddress().toString());
				}
			} else {
				Logger.i(TAG, "Handler for "
						+ conn.getRemoteSocketAddress().toString()
						+ " was already removed");
			}
		}

		@Override
		public void onError(WebSocket conn, Exception ex) {
			Logger.e(TAG, "Received Error", ex);

			SecondScreenMod.error = ex.getMessage();
			SecondScreenMod.connected = false;
			

		}

		@Override
		public void onMessage(WebSocket conn, String message) {
			synchronized (handlers) {
				Logger.i(TAG, "Received msg for "
						+ conn.getRemoteSocketAddress().toString() + ": "
						+ message);// TODO remove
				WebSocketHandler h = handlers
						.get(conn.getRemoteSocketAddress());
				if (h != null) {
					h.onMessage(message);
				} else {
					Logger.w(TAG, "Could not find handler for "
							+ conn.getRemoteSocketAddress().toString());
					// onOpen(conn,null);//??
				}
			}

		}

		@Override
		public void onOpen(WebSocket conn, ClientHandshake handshake) {
			Logger.i(TAG, "New connection: "
					+ conn.getRemoteSocketAddress().toString());
			handlers.put(conn.getRemoteSocketAddress(), new WebSocketHandler(
					conn));

		}

	}
	private final static String TAG = "WebSocketListener";
	public static int getNewHandlerID() {
		return ++handlerCount;
	}
	private HashMap<InetSocketAddress, WebSocketHandler> handlers;
	private MSSWebSocketServer socketServer;

	private Thread serverThread;

	private static int handlerCount = 0;

	public WebSocketListener() {
		handlers = new HashMap<InetSocketAddress, WebSocketHandler>();
		FMLCommonHandler.instance().bus().register(this);
	}

	private void closeAll() {
		synchronized (handlers) {
			Logger.d(TAG, "Closing all handlers");
			for (WebSocketHandler h : handlers.values()) {
				h.close();
			}
			handlers.clear();
		}
	}

	private MSSWebSocketServer create() {
		int port = SecondScreenMod.port;
		String hostname = SecondScreenMod.hostname;

		if (port == 0) {

			port = MinecraftServer.getServer().getPort();
		}

		return new MSSWebSocketServer(new InetSocketAddress(hostname, port));
	}

	@SubscribeEvent
	public void forceUpdate(ForceUpdateEvent e) {
		Logger.i(TAG, "Forcing update for " + e.listener.toString());
		synchronized (handlers) {
			for (WebSocketHandler h : handlers.values()) {
				h.forceUpdate(e);
			}
		}
	}

	public void start() {

		socketServer = create();
		Logger.i(TAG, "Starting WebSocketListener on "
				+ socketServer.getAddress().toString());

		if (socketServer != null) {
			serverThread = new Thread(socketServer);
			SecondScreenMod.connected = true;
			serverThread.start();

		} else {
			Logger.e(TAG, "Socket server null");
		}
	}

	public void stop() {
		closeAll();
		try {
			socketServer.stop();
			
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socketServer = null;

		Logger.i(TAG, "Stopped server");
	}

	@SubscribeEvent
	public void tick(ServerTickEvent e) {
		synchronized (handlers) {
			for (WebSocketHandler h : handlers.values()) {
				if (h.remove) {
					handlers.remove(h.address);

				} else {
					h.tick();
				}
			}
		}
	}

}
