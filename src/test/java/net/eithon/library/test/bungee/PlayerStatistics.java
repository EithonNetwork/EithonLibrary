package net.eithon.library.test.bungee;

import java.time.LocalDateTime;
import java.util.UUID;

import net.eithon.library.core.IUuidAndName;
import net.eithon.library.extensions.EithonPlayer;
import net.eithon.library.json.JsonObjectDelta;
import net.eithon.library.plugin.Logger;
import net.eithon.library.plugin.Logger.DebugPrintLevel;
import net.eithon.library.time.TimeMisc;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class PlayerStatistics extends JsonObjectDelta<PlayerStatistics> implements IUuidAndName {
	private static Logger eithonLogger;

	// Saved variables
	private EithonPlayer _eithonPlayer;
	private long _blocksBroken;
	private long _blocksCreated;
	private long _chatActivities;
	private LocalDateTime _lastChatActivity;
	private long _consecutiveDays;
	private LocalDateTime _lastConsecutiveDay;
	private TimeStatistics _timeInfo;

	private LocalDateTime _lastAliveTime;
	private boolean _hasBeenUpdated;
	private String _afkDescription;

	public static void initialize(Logger logger) {
		eithonLogger = logger;
	}

	public PlayerStatistics(Player player)
	{
		this(new EithonPlayer(player));
	}

	public PlayerStatistics(EithonPlayer eithonPlayer)
	{
		this();
		this._eithonPlayer = eithonPlayer;
	}

	PlayerStatistics() {
		this._blocksBroken = 0;
		this._blocksCreated = 0;
		this._chatActivities = 0;
		this._lastChatActivity = null;
		resetConsecutiveDays();
		this._timeInfo = new TimeStatistics();
		this._hasBeenUpdated = false;
		this._afkDescription = null;
		this._lastAliveTime = LocalDateTime.now();
	}

	void resetConsecutiveDays() {
		this._consecutiveDays = 0;
		this._lastConsecutiveDay = null;
	}

	public boolean isOnline() {
		return this._eithonPlayer.isOnline();
	}

	@Override
	public PlayerStatistics factory() { return new PlayerStatistics(); }

	@Override
	public PlayerStatistics fromJson(Object json) {
		JSONObject jsonObject = (JSONObject) json;
		this._eithonPlayer = EithonPlayer.getFromJson(jsonObject.get("player"));
		this._timeInfo = TimeStatistics.getFromJson(jsonObject.get("timeInfo"));
		this._chatActivities = (long)jsonObject.get("chatActivities");
		this._lastChatActivity = TimeMisc.toLocalDateTime(jsonObject.get("lastChatActivity"));
		this._blocksCreated = (long)jsonObject.get("blocksCreated");
		this._blocksBroken = (long)jsonObject.get("blocksBroken");
		Object days = jsonObject.get("consecutiveDays");
		if (days == null) this._consecutiveDays = 0;
		else this._consecutiveDays = (long)days;
		this._lastConsecutiveDay = TimeMisc.toLocalDateTime(jsonObject.get("lastConsecutiveDay"));
		return this;
	}
	
	public static PlayerStatistics getFromJson(Object json) { return new PlayerStatistics().fromJson(json); }

	@Override
	public JSONObject toJson() {
		return (JSONObject) toJsonDelta(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object toJsonDelta(boolean saveAll) {
		eithonLogger.debug(DebugPrintLevel.VERBOSE, "PlayerStatistics.toJsonDelta: Enter for player %s", this.getName());
		if (!saveAll && !this._hasBeenUpdated) {
			eithonLogger.debug(DebugPrintLevel.VERBOSE, "PlayerStatistics.toJsonDelta: Player %s has not been updated", this.getName());
			eithonLogger.debug(DebugPrintLevel.VERBOSE, "PlayerStatistics.toJsonDelta: Leave");
			return null;
		}
		
		JSONObject json = new JSONObject();
		json.put("player", this._eithonPlayer.toJson());
		json.put("timeInfo", this._timeInfo.toJson());
		json.put("chatActivities", this._chatActivities);
		json.put("lastChatActivity", TimeMisc.fromLocalDateTime(this._lastChatActivity));
		json.put("blocksCreated", this._blocksCreated);
		json.put("blocksBroken", this._blocksBroken);
		json.put("consecutiveDays", this._consecutiveDays);
		json.put("lastConsecutiveDay", TimeMisc.fromLocalDateTime(this._lastConsecutiveDay));
		this._hasBeenUpdated = false;
		eithonLogger.debug(DebugPrintLevel.VERBOSE, "PlayerStatistics.toJsonDelta: Player %s result: %s", this.getName(), json.toString());
		eithonLogger.debug(DebugPrintLevel.VERBOSE, "PlayerStatistics.toJsonDelta: Leave");
		return json;
	}

	public String getName() {
		if (this._eithonPlayer == null) return null;
		return this._eithonPlayer.getName(); }

	public UUID getUniqueId() { 
		if (this._eithonPlayer == null) return null;
		return this._eithonPlayer.getUniqueId(); 
	}

	public long getTotalTimeInSeconds() { return this._timeInfo.getTotalPlayTimeInSeconds(); }

	public boolean isAfk() {
		return isOnline() && (this._afkDescription != null);
	}

	public boolean isActive() {
		return isOnline() && !isAfk();
	}

	public long getBlocksCreated() { return this._blocksCreated; }

	public LocalDateTime getLastConsecutiveDay() {
		if (lastConsecutiveDayWasTooLongAgo()) {
			resetConsecutiveDays();
		}
		return this._lastConsecutiveDay; 
	}

	public long getConsecutiveDays() {
		if (lastConsecutiveDayWasTooLongAgo()) {
			resetConsecutiveDays();
		}
		return this._consecutiveDays; 
	}
	
	public EithonPlayer getEithonPlayer() { return this._eithonPlayer; }

	boolean lastConsecutiveDayWasTooLongAgo() {
		return !lastConsecutiveDayWasToday() && !lastConsecutiveDayWasYesterday();
	}

	private boolean lastConsecutiveDayWasToday() {
		final LocalDateTime today = this._timeInfo.getToday();
		return lastConsecutiveDayWasThisDay(today);
	}

	private boolean lastConsecutiveDayWasYesterday() {
		final LocalDateTime yesterday = this._timeInfo.getToday().minusDays(1);
		return lastConsecutiveDayWasThisDay(yesterday);
	}

	private boolean lastConsecutiveDayWasThisDay(LocalDateTime day) {
		return TimeStatistics.isSameDay(day, this._lastConsecutiveDay);
	}

	public long getChats() { return this._chatActivities; }

	public LocalDateTime getAfkTime() { return this._lastAliveTime; }

	public Object getAfkDescription() { return this._afkDescription; }
}
