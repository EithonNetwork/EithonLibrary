package net.eithon.library.time;

public abstract interface ICountDownListener {
	public abstract boolean isCancelled(long remainingIntervals);
	public abstract void afterCancelTask();
	public abstract void afterDoneTask();
}
