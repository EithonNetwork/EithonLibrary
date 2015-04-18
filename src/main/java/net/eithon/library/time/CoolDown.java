package net.eithon.library.time;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;

import net.eithon.library.core.PlayerCollection;

import org.bukkit.entity.Player;

public class CoolDown {
	private static HashMap<String, CoolDown> _coolDowns = new HashMap<String, CoolDown>();
	private PlayerCollection<LocalDateTime> _playerCoolDownEnds;
	private int _defaultCoolDownPeriodInSeconds;
	
	public CoolDown(String name, int defaultCoolDownPeriodInSeconds) {
		_coolDowns.put(name, this);
		this._playerCoolDownEnds = new PlayerCollection<LocalDateTime>();
		this._defaultCoolDownPeriodInSeconds = defaultCoolDownPeriodInSeconds;
	}
	
	public static CoolDown getCoolDown(String name) {
		return _coolDowns.get(name);
	}
	
	public void addPlayer(Player player) {
		addPlayer(player, this._defaultCoolDownPeriodInSeconds);
	}
	
	public void addPlayer(Player player, int coolDownPeriodInSeconds) {
		this._playerCoolDownEnds.put(player, LocalDateTime.now().plusSeconds(coolDownPeriodInSeconds));
	}
	
	public boolean isInCoolDownPeriod(Player player) {
		return secondsLeft(player) > 0;
	}
	
	public int secondsLeft(Player player) {
		LocalDateTime endTime = this._playerCoolDownEnds.get(player);
		if (endTime == null) return 0;
		long secondsLeft = endTime.toEpochSecond(ZoneOffset.UTC)-LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
		if (secondsLeft > 0) return (int) secondsLeft;
		this._playerCoolDownEnds.remove(player);
		return 0;
	}
}


