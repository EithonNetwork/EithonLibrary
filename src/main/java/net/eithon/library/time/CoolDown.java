package net.eithon.library.time;

import java.util.HashMap;

import net.eithon.library.core.PlayerCollection;

import org.bukkit.entity.Player;

public class CoolDown {
	private static HashMap<String, CoolDown> _coolDowns = new HashMap<String, CoolDown>();
	private PlayerCollection<CoolDownInfo> _players;
	private long _defaultCoolDownPeriodInSeconds;
	private int _defaultAllowedNumberOfTimes;
	
	public CoolDown(String name, long defaultCoolDownPeriodInSeconds, int defaultAllowedNumberOfTimes) {
		_coolDowns.put(name, this);
		this._players = new PlayerCollection<CoolDownInfo>();
		this._defaultCoolDownPeriodInSeconds = defaultCoolDownPeriodInSeconds;
		this._defaultAllowedNumberOfTimes = defaultAllowedNumberOfTimes;
	}
	
	public CoolDown(String name, long defaultCoolDownPeriodInSeconds) {
		this(name, defaultCoolDownPeriodInSeconds, 1);
	}
	
	public static CoolDown getCoolDown(String name) {
		return _coolDowns.get(name);
	}
	
	public boolean addIncidentOrFalse(Player player) {
		CoolDownInfo coolDownInfo = this._players.get(player);
		if (coolDownInfo == null) {
			coolDownInfo = new CoolDownInfo(this._defaultCoolDownPeriodInSeconds, this._defaultAllowedNumberOfTimes);
			this._players.put(player, coolDownInfo);
		}
		return coolDownInfo.addIncidentIfAllowed();
	}
	
	public void addIncident(Player player) {
		CoolDownInfo coolDownInfo = this._players.get(player);
		if (coolDownInfo == null) {
			coolDownInfo = new CoolDownInfo(this._defaultCoolDownPeriodInSeconds, this._defaultAllowedNumberOfTimes);
			this._players.put(player, coolDownInfo);
		}
		coolDownInfo.addIncident();
	}
	
	public boolean isInCoolDownPeriod(Player player) {
		return secondsLeft(player) > 0;
	}
	
	public long secondsLeft(Player player) {
		CoolDownInfo coolDownInfo = this._players.get(player);
		if (coolDownInfo == null) return 0;
		long secondsLeft = coolDownInfo.secondsLeft();
		if (!coolDownInfo.hasIncidents()) this._players.remove(player);
		return secondsLeft;
	}
	
	public void removePlayer(Player player) {
		_coolDowns.remove(player);
	}
	
	@Deprecated
	public void addPlayer(Player player) {
		addPlayer(player, this._defaultCoolDownPeriodInSeconds, this._defaultAllowedNumberOfTimes);
	}
	
	@Deprecated
	public void addPlayer(Player player, long coolDownPeriodInSeconds) {
		addPlayer(player, coolDownPeriodInSeconds, 1);
	}
	
	@Deprecated
	public void addPlayer(Player player, long coolDownPeriodInSeconds, int allowedNumberOfTimes) {
		CoolDownInfo coolDownInfo = this._players.get(player);
		if (coolDownInfo == null) {
			coolDownInfo = new CoolDownInfo(coolDownPeriodInSeconds, allowedNumberOfTimes);
			this._players.put(player, coolDownInfo);
		}
		coolDownInfo.addIncidentIfAllowed();
	}
}


