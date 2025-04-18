package ru.myx.ae3.vfs.s4.net;

enum ProtocolCommand {
	/** Basic ack
	 * 
	 * Format: empty */
	ACK_FALSE {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Basic ack
	 * 
	 * Format: empty. */
	ACK_TRUE {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Continuation welcome
	 * 
	 * Format: empty. */
	CONTINUE {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Threated as ACK_TRUE on GET commands */
	DATA_LAST_8K {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Threated as ACK_TRUE on GET commands */
	DATA_LAST_SMALLER {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Threated as ACK_TRUE on GET commands */
	DATA_NEXT_8K {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for data.
	 * 
	 * Format: 384bits of GUID. */
	FIRST_RETRIEVE_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for data.
	 * 
	 * Format: 184bits of GUID. */
	FIRST_RETRIEVE_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for data.
	 * 
	 * Format: 384bits of GUID. */
	FIRST_RETRIEVE_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/**
	 * 
	 */
	FIRST_STORE_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/**
	 * 
	 */
	FIRST_STORE_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/**
	 * 
	 */
	FIRST_STORE_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** First hello message
	 * 
	 * Format: version major byte, version minor byte. 0x0002 for current protocol version. */
	HELLO {
		
		@Override
		public boolean isAuthRequired() {
			
			return false;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return false;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for reference check.
	 * 
	 * Format: 384bits of GUID. */
	PROXY_CHECK_REFERENCE_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for reference check.
	 * 
	 * Format: 184bits of GUID. */
	PROXY_CHECK_REFERENCE_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for reference check.
	 * 
	 * Format: 384bits of GUID. */
	PROXY_CHECK_REFERENCE_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Informational: creation (no update possible) of CRC384 object.
	 * 
	 * Format: 384bits of GUID */
	PROXY_INVALIDATE_CHANGED_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Informational: creation or update of GUID184 object.
	 * 
	 * Format: 184bits of GUID */
	PROXY_INVALIDATE_CHANGED_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Informational: creation or update of GUID384 object.
	 * 
	 * Format: 384bits of GUID */
	PROXY_INVALIDATE_CHANGED_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** No response required */
	PROXY_INVALIDATE_DELETED_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** No response required */
	PROXY_INVALIDATE_DELETED_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** No response required */
	PROXY_INVALIDATE_DELETED_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for data.
	 * 
	 * Format: 384bits of GUID. */
	PROXY_RETRIEVE_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for data.
	 * 
	 * Format: 184bits of GUID. */
	PROXY_RETRIEVE_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for data.
	 * 
	 * Format: 384bits of GUID. */
	PROXY_RETRIEVE_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/**
	 * 
	 */
	PROXY_STORE_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/**
	 * 
	 */
	PROXY_STORE_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/**
	 * 
	 */
	PROXY_STORE_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return false;
		}
	},
	/** Request for reference check.
	 * 
	 * Format: 384bits of GUID. */
	SECTOR_CHECK_REFERENCE_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** Request for reference check.
	 * 
	 * Format: 184bits of GUID. */
	SECTOR_CHECK_REFERENCE_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** Request for reference check.
	 * 
	 * Format: 384bits of GUID. */
	SECTOR_CHECK_REFERENCE_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** sector coverage movement */
	SECTOR_COVERAGE_MOVEMENT {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** Informational: creation (no update possible) of CRC384 object.
	 * 
	 * Format: 384bits of GUID */
	SECTOR_INVALIDATE_CHANGED_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** Informational: creation or update of GUID184 object.
	 * 
	 * Format: 184bits of GUID */
	SECTOR_INVALIDATE_CHANGED_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** Informational: creation or update of GUID384 object.
	 * 
	 * Format: 384bits of GUID */
	SECTOR_INVALIDATE_CHANGED_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/**
	 * 
	 */
	SECTOR_INVALIDATE_DELETED_CRC384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/**
	 * 
	 */
	SECTOR_INVALIDATE_DELETED_GUID184 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/**
	 * 
	 */
	SECTOR_INVALIDATE_DELETED_GUID384 {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** sector point movement */
	SECTOR_POINT_MOVEMENT {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** sector state critical */
	SECTOR_STATE_CRITICAL {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return true;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},
	/** sector state normal */
	SECTOR_STATE_NORMAL {
		
		@Override
		public boolean isAuthRequired() {
			
			return true;
		}
		
		@Override
		public boolean isClusterRequired() {
			
			return true;
		}
		
		@Override
		public boolean isResponseRequired() {
			
			return false;
		}
		
		@Override
		public boolean isSectorRequired() {
			
			return true;
		}
	},;
	
	abstract boolean isAuthRequired();
	
	abstract boolean isClusterRequired();
	
	abstract boolean isResponseRequired();
	
	abstract boolean isSectorRequired();
}
