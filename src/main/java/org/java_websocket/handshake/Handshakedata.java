package org.java_websocket.handshake;

import java.util.Iterator;

public interface Handshakedata {
	public byte[] getContent();

	public String getFieldValue(String name);

	public boolean hasFieldValue(String name);

	public Iterator<String> iterateHttpFields();
}
