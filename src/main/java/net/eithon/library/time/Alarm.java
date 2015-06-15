package net.eithon.library.time;

import java.time.LocalDateTime;
import java.util.UUID;

class Alarm {
	private UUID _id;
	private LocalDateTime _when;
	private Runnable _task;
	private boolean _hasBeenSetOff;
	private String _name;

	Alarm(String name, LocalDateTime when, Runnable task)
	{
		this._id = UUID.randomUUID();
		this._when = when;
		this._task = task;
		this._name = name;
		this._hasBeenSetOff = false;
	}
	
	LocalDateTime getTime()
	{
		return this._when;
	}
	
	String getName() { return this._name; }
	UUID getId() { return this._id; }
	boolean isSame(UUID id) { return this._id == id; }
	boolean hasBeenSetOff() { return this._hasBeenSetOff; }
	
	void reset(LocalDateTime when) {
		synchronized(this) {
			this._when = when;
			this._hasBeenSetOff = false;
		}
	}

	boolean maybeSetOff() 
	{
		synchronized(this) {
			if (this._hasBeenSetOff) return false;
			if (this._when.isBefore(LocalDateTime.now())) {
				this._hasBeenSetOff = true;
				this._task.run();
				return true;
			}
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format("\"%s\" %s (%s) ", getName(), this._hasBeenSetOff?"off":"on", this._when.toString());
	}
}
