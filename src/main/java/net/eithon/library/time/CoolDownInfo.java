package net.eithon.library.time;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

class CoolDownInfo {
	private long _coolDownPeriodInSeconds;
	private int _allowedIncidents;
	private Queue<LocalDateTime> _incidentQueue;
	
	CoolDownInfo(long coolDownPeriodInSeconds, int allowedIncidents) {
		this._coolDownPeriodInSeconds = coolDownPeriodInSeconds;
		this._allowedIncidents = allowedIncidents;
		this._incidentQueue = new LinkedBlockingQueue<LocalDateTime>();
	}
	
	boolean hasIncidents() {
		removeOldIncidents();
		return this._incidentQueue.size() > 0;
	}
	
	boolean addIncidentIfAllowed() {
		if (hasTooManyIncidents()) return false;
		this._incidentQueue.add(LocalDateTime.now());
		return true;
	}
	
	void addIncident() {
		removeOldIncidents();
		if (hasTooManyIncidents()) this._incidentQueue.poll();
		this._incidentQueue.add(LocalDateTime.now());
	}
	
	boolean hasTooManyIncidents() {
		removeOldIncidents();
		return this._incidentQueue.size() >= this._allowedIncidents;
	}
	
	long secondsLeft() {
		if (!hasTooManyIncidents()) return 0;
		LocalDateTime incidentTime = this._incidentQueue.peek();
		long secondsSinceOldestIncident = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - incidentTime.toEpochSecond(ZoneOffset.UTC);
		return this._coolDownPeriodInSeconds - secondsSinceOldestIncident;
	}

	private void removeOldIncidents() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime oldestAllowed = now.minusSeconds(this._coolDownPeriodInSeconds);
		LocalDateTime incidentTime = this._incidentQueue.peek();
		while (incidentTime != null && incidentTime.isBefore(oldestAllowed)) { 
			this._incidentQueue.poll();
			incidentTime = this._incidentQueue.peek();
		}
	}
}
