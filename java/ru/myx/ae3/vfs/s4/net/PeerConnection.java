package ru.myx.ae3.vfs.s4.net;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.know.Guid;
import ru.myx.util.FifoQueueBuffered;

class PeerConnection {
	
	private static final int SLOT_COUNT = 256;
	
	private static final int SLOT_MASK = PeerConnection.SLOT_COUNT - 1;
	
	private final FifoQueueBuffered<ConnectionTask> queue;
	
	private final ConnectionSlot[] slotsActive;
	
	private final ConnectionSlot[] slotsVacant;
	
	private int slotsVacantHead = 0;
	
	private int slotsVacantSize = 0;
	
	private int slotsVacantTail = 0;
	
	PeerConnection() {
		this.slotsActive = new ConnectionSlot[PeerConnection.SLOT_COUNT];
		this.slotsVacant = new ConnectionSlot[PeerConnection.SLOT_COUNT];
		for (int i = this.slotsActive.length - 1; i >= 0; --i) {
			this.vacantSlot(new ConnectionSlot(i));
		}
		this.queue = new FifoQueueBuffered<>();
	}
	
	Future<TransferCopier> enqueueReadValue(final Guid guid) {
		
		if (guid.isInline()) {
			return null;
		}
		return new Future<>() {
			
			@Override
			public boolean cancel(final boolean mayInterruptIfRunning) {
				
				throw new UnsupportedOperationException();
			}
			
			@Override
			public TransferCopier get() throws InterruptedException, ExecutionException {
				
				throw new UnsupportedOperationException();
			}
			
			@Override
			public TransferCopier get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
				
				throw new UnsupportedOperationException();
			}
			
			@Override
			public boolean isCancelled() {
				
				throw new UnsupportedOperationException();
			}
			
			@Override
			public boolean isDone() {
				
				throw new UnsupportedOperationException();
			}
			//
		};
	}
	
	private ConnectionSlot vacantSlot() {
		
		if (this.slotsVacantSize == 0) {
			return null;
		}
		final int index = this.slotsVacantHead++ & PeerConnection.SLOT_MASK;
		try {
			return this.slotsVacant[index];
		} finally {
			this.slotsVacant[index] = null;
			this.slotsVacantSize--;
		}
	}
	
	private void vacantSlot(final ConnectionSlot slot) {
		
		this.slotsVacant[this.slotsVacantTail++ & PeerConnection.SLOT_MASK] = slot;
		this.slotsVacantSize++;
	}
	
	int wireRead(final byte[] buffer, final int offset, final int length) {
		
		return 0;
	}
	
	int wireWrite(final byte[] buffer, final int offset, final int length) {
		
		return 0;
	}
}
