package ru.myx.ae3.vfs.s4.net;

/** Contains state of one of active queries on connection.
 * 
 * @author myx */
class ConnectionSlot {
	
	ProtocolCommand command;
	
	PeerConnection connection;
	
	long created;
	
	final int index;
	
	long sent;
	
	long timeout;
	
	ConnectionSlot(final int index) {
		this.index = index;
	}
}
