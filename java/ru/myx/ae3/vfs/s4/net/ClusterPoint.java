package ru.myx.ae3.vfs.s4.net;

class ClusterPoint {
	
	ClusterSector left;
	
	double point;
	
	ClusterSector right;
	
	final boolean isCritical() {
		
		return this.left.isCritical() || this.right.isCritical();
	}
}
