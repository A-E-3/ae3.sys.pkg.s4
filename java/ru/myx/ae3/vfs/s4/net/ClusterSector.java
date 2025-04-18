package ru.myx.ae3.vfs.s4.net;

class ClusterSector {
	
	PeerConnection connection;
	
	double coveredPoint;
	
	boolean opponentCritical;
	
	double opponentPoint;
	
	ClusterPoint parentPoint;
	
	final boolean isCritical() {
		
		return this.opponentCritical || this.coveredPoint < this.opponentPoint;
	}
}
