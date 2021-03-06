package de.maxgb.minecraft.second_screen;

import de.maxgb.minecraft.second_screen.util.ForceUpdateEvent;
import de.maxgb.minecraft.second_screen.util.Logger;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Manages the websocket and all handlers.
 * All communication runs through this class
 * @author Max
 *
 */
public class WebSocketListener {

	private class MSSWebSocketServer extends WebSocketServer {
		private final static String TAG = "MSSWebSocketServer";

		public MSSWebSocketServer(InetSocketAddress address) {
			super(address);

		}

		@Override
		public void onClose(WebSocket conn, int code, String reason, boolean remote) {
			WebSocketHandler h = handlers.get(conn.getRemoteSocketAddress());
			if (h != null) {
				synchronized (handlers) {
					h.close();
					handlers.remove(conn.getRemoteSocketAddress());
					Logger.i(TAG, "Closed Handler/Connection for " + conn.getRemoteSocketAddress().toString());
				}
			} else {
				Logger.i(TAG, "Handler for " + conn.getRemoteSocketAddress().toString() + " was already removed");
			}
		}

		@Override
		public void onError(WebSocket conn, Exception ex) {
			Logger.e(TAG, "Received Error", ex);

			error = ex.getMessage();
			running = false;

		}

		@Override
		public void onMessage(WebSocket conn, String message) {
			synchronized (handlers) {
				Logger.i(TAG, "Received msg for " + conn.getRemoteSocketAddress().toString() + ": " + message);// TODO
																												// remove
				WebSocketHandler h = handlers.get(conn.getRemoteSocketAddress());
				if (h != null) {
					h.onMessage(message);
				} else {
					Logger.w(TAG, "Could not find handler for " + conn.getRemoteSocketAddress().toString());
					// onOpen(conn,null);//??
				}
			}

		}

		@Override
		public void onOpen(WebSocket conn, ClientHandshake handshake) {
			Logger.i(TAG, "New connection: " + conn.getRemoteSocketAddress().toString());
			handlers.put(conn.getRemoteSocketAddress(), new WebSocketHandler(conn));

		}
		
		/**
		 * Does super{@link #stop(int)}, but adds a debug log call
		 */
		@Override
		public void stop(int timeout) throws InterruptedException{
			Logger.d(TAG, "WebsocketServer received stop call with timeout "+timeout);
			super.stop(timeout);
		}

	}

	private final static String TAG = "WebSocketListener";

	public static int getNewHandlerID() {
		return ++handlerCount;
	}

	private HashMap<InetSocketAddress, WebSocketHandler> handlers;
	private MSSWebSocketServer socketServer;

	private Thread serverThread;
	
	private boolean running;
	private String error="Unknown";

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
		int port = SecondScreenMod.instance.port;
		String hostname = SecondScreenMod.instance.hostname;

		if (port == 0) {

            port = FMLCommonHandler.instance().getMinecraftServerInstance().getServerPort();
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
		if(Configs.debug_mode){
			WebSocketImpl.DEBUG=true;
		}
		socketServer = create();
		Logger.i(TAG, "Starting WebSocketListener on " + socketServer.getAddress().toString());

		if (socketServer != null) {
			serverThread = new Thread(socketServer);
			running = true;
			serverThread.start();

		} else {
			Logger.e(TAG, "Socket server null");
		}
	}
	
	public boolean isRunning(){
		return running;
	}
	
	public String getError(){
		if(running){
			return "";
		}
		return error;
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
			Iterator<WebSocketHandler> iterator=handlers.values().iterator();
			while (iterator.hasNext()) {
				WebSocketHandler h=iterator.next();
				if (h.isGettingRemoved()) {
					Logger.d(TAG, "Handler "+h+" indicates it wants to be removed");
					handlers.remove(h.address);

				} else {
					h.tick();
				}
			}
		}
	}

}
