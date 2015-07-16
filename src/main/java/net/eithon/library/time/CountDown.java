package net.eithon.library.time;

import net.eithon.library.extensions.EithonPlugin;

import org.bukkit.scheduler.BukkitScheduler;

public class CountDown {
	private long _totalTimeMilliSeconds;
	private long _intervalMilliSeconds;
	private EithonPlugin _eithonPlugin;
	private long _stopTimeMilliSeconds;
	long _counter;
	ICountDownListener _callBack;

	public CountDown(EithonPlugin plugin, long countFrom, long intervalInMilliseconds, ICountDownListener callBack) {
		this._eithonPlugin = plugin;
		this._counter = countFrom;
		this._intervalMilliSeconds = intervalInMilliseconds;
		this._totalTimeMilliSeconds = countFrom*intervalInMilliseconds;
		this._callBack = callBack;
	}

	public void start(BukkitScheduler scheduler) {
		long start = System.currentTimeMillis();
		this._stopTimeMilliSeconds = start + this._totalTimeMilliSeconds;
		tick(scheduler);
	}

	private long nextSleepInMilliSeconds()
	{
		long remainingMilliSeconds = this._stopTimeMilliSeconds - System.currentTimeMillis();
		if (remainingMilliSeconds <= 0) {
			this._counter = 0;
			return 0;
		}
		long nextSleepInMilliSeconds = remainingMilliSeconds % this._intervalMilliSeconds;
		this._counter = remainingMilliSeconds / this._intervalMilliSeconds;
		if ((nextSleepInMilliSeconds < 100) && (this._counter > 0)) {
			nextSleepInMilliSeconds = this._intervalMilliSeconds + nextSleepInMilliSeconds;
			this._counter--;
		}
		return nextSleepInMilliSeconds;
	}

	void tick(BukkitScheduler scheduler) {
		while (true) {
			if (this._callBack.isCancelled(this._counter)) {
				this._callBack.afterCancelTask();
				return;
			}
			if (this._counter == 0) {
				this._callBack.afterDoneTask();
				return;
			}

			if (scheduler == null) {
				try {
					Thread.sleep(nextSleepInMilliSeconds());
				} catch (InterruptedException e) {
					this._callBack.afterCancelTask();
					return;
				}
			} else {
				long nextSleepInTicks = Math.round(nextSleepInMilliSeconds()/50.0);	
				scheduler.scheduleSyncDelayedTask(this._eithonPlugin, new Runnable() {
					public void run() {
						tick(scheduler);
					}
				}, nextSleepInTicks);
				return;
			}
		}
	}
}
