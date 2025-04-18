package ru.myx.ae3.vfs.s4.driver;

import ru.myx.ae3.Engine;
import ru.myx.ae3.act.Act;
import ru.myx.ae3.base.BaseFutureAbstract;
import ru.myx.ae3.common.FutureValue;
import ru.myx.ae3.common.WaitTimeoutException;
import ru.myx.ae3.e4.act.Task;
import ru.myx.ae3.reflect.ReflectionHidden;
import ru.myx.ae3.reflect.ReflectionIgnore;

/** @author myx
 *
 * @param <V> */
@ReflectionIgnore
public abstract class TaskCommon<V> //
		extends
			BaseFutureAbstract<V>
		implements
			Task<V> {

	private volatile Throwable error = null;

	private volatile boolean loadDone = false;

	private volatile TaskCommon<V> loadResult = null;

	private volatile V result = null;

	@Override
	public final Throwable baseError() {

		if (this.loadResult == this) {
			if (this.error != null) {
				return FutureValue.throwTaskFailedError(this.error, this);
			}
			return null;
		}
		return this.getReference().baseError();
	}

	@Override
	public final V baseValue() {

		if (this.loadResult == this) {
			if (this.error != null) {
				throw FutureValue.throwTaskFailedError(this.error, this);
			}
			return this.result;
		}

		return this.getReference().baseValue();
	}

	@Override
	public final boolean isDone() {

		final TaskCommon<V> loadResult = this.loadResult;
		if (loadResult == this) {
			return this.loadDone;
		}
		return loadResult != null && loadResult.isDone();
	}

	@Override
	public boolean isFailed() {

		final TaskCommon<V> loadResult = this.loadResult;
		if (loadResult == this) {
			return this.loadDone && this.error != null;
		}
		return loadResult != null && loadResult.isFailed();
	}

	/** @param result */
	@ReflectionHidden
	public final void setError(final Error result) {

		assert result != null : "Error shouldn't be null";
		this.error = result;
		this.loadResult = this;
		this.loadDone = true;
		/** TODO: is there a point? Why not just inline? */
		Act.launchNotifyAll(this);
	}

	/** @param result */
	@ReflectionHidden
	public final void setResult(final V result) {

		this.result = result;
		this.loadResult = this;
		this.loadDone = true;
		/** TODO: is there a point? Why not just inline? */
		Act.launchNotifyAll(this);
	}

	@Override
	public String toString() {

		if (this.loadDone) {
			return String.valueOf(this.baseValue());
		}
		return "TASK-UNFINISHED(" + this.getClass().getSimpleName() + ")";
	}

	/** @param context
	 * @throws Exception */
	protected abstract void execute(final S4WorkerContext context) throws Exception;

	/** @return */
	@SuppressWarnings("static-method")
	protected long getTaskTimeout() {

		return 60_000L;
	}

	/** @param depends */
	protected final void setDuplicateOf(final TaskCommon<V> depends) {

		assert depends != null : "Dependency shouldn't be null";
		this.loadResult = depends;
		this.loadDone = true;
		synchronized (this) {
			this.notifyAll();
		}
	}

	final TaskCommon<V> getReference() {

		if (this.loadDone) {
			return this.loadResult;
		}
		synchronized (this) {
			if (!this.loadDone) {
				try {
					/** I AM NOT PARANOID.
					 *
					 *
					 * Taken from Javadoc for 'wait' method:
					 *
					 * A thread can also wake up without being notified, interrupted, or timing out,
					 * a so-called spurious wakeup. While this will rarely occur in practice,
					 * applications must guard against it by testing for the condition that should
					 * have caused the thread to be awakened, and continuing to wait if the
					 * condition is not satisfied. In other words, waits should always occur in
					 * loops, like this one:
					 *
					 * synchronized (obj) { while (<condition does not hold>) obj.wait(timeout); ...
					 * // Perform action appropriate to condition }
					 *
					 * (For more information on this topic, see Section 3.2.3 in Doug Lea's
					 * "Concurrent Programming in Java (Second Edition)" (Addison-Wesley, 2000), or
					 * Item 50 in Joshua Bloch's "Effective Java Programming Language Guide"
					 * (Addison-Wesley, 2001). */
					for (//
							long left = this.getTaskTimeout(), expires = Engine.fastTime() + left; //
							left > 0; //
							left = expires - Engine.fastTime()) {
						//
						this.wait(left);
						if (this.loadDone) {
							return this.loadResult;
						}
					}
				} catch (final InterruptedException e) {
					return null;
				}
			}
		}
		if (this.loadDone) {
			return this.loadResult;
		}
		if (this.loadResult == null) {
			final WaitTimeoutException timeout = new WaitTimeoutException(//
					"Wait timeout (hash=" + System.identityHashCode(this) + ", info=" + this.toString() + ")!"//
			);
			this.error = timeout;
			this.loadResult = this;
			this.loadDone = true;
			return this;
		}
		if (this.loadResult == this) {
			return this;
		}
		this.loadResult = this.loadResult.getReference();
		this.loadDone = true;
		return this.loadResult;
	}
}
