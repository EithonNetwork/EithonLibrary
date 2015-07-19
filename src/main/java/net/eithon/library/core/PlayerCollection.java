	package net.eithon.library.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import net.eithon.library.extensions.EithonPlayer;

import org.bukkit.entity.Player;

public class PlayerCollection<T> implements Iterable<T>, Serializable {
	private static final long serialVersionUID = 1L;
	protected HashMap<UUID, T> playerInfo = null;

	public PlayerCollection() {
		this.playerInfo = new HashMap<UUID, T>();
	}

	public void put(EithonPlayer eithonPlayer, T info) {
		UUID id = eithonPlayer.getUniqueId();
		put(id, info);
	}
	public void put(Player player, T info) {
		UUID id = player.getUniqueId();
		put(id, info);
	}
	
	public void put(UUID playerId, T info) {
		this.playerInfo.put(playerId, info);
	}
	
	public T get(EithonPlayer eithonPlayer) {
		UUID id = eithonPlayer.getUniqueId();
		return get(id);
	}
	
	public T get(Player player) {
		UUID id = player.getUniqueId();
		return get(id);
	}
	
	public T get(UUID playerId) {
		return this.playerInfo.get(playerId);
	}
	
	public boolean hasInformation(Player player) {
		UUID id = player.getUniqueId();
		return hasInformation(id);
	}
	
	public boolean hasInformation(UUID playerId) {
		return this.playerInfo.containsKey(playerId);
	}
	
	public void remove(EithonPlayer player) {
		UUID id = player.getUniqueId();
		remove(id);
	}
	
	public void remove(Player player) {
		UUID id = player.getUniqueId();
		remove(id);
	}
	
	public void remove(UUID playerId) {
		this.playerInfo.remove(playerId);
	}
	
	public Set<UUID> getPlayers() {
		return this.playerInfo.keySet();
	}
	
	public Iterator<T> iterator() {
		return this.playerInfo.values().iterator();
	}

	public int size() {	return this.playerInfo.size(); }

	public Object[] toArray() { return this.playerInfo.values().toArray(); }

	public Collection<T> values() { return this.playerInfo.values(); }
	
	public List<T> sort(int maxItems, Predicate<T> removeIfPredicate, Comparator<T> comparator) {
		List<T> items = new ArrayList<T>(this.playerInfo.values());
		if (removeIfPredicate != null) items.removeIf(removeIfPredicate);
		items.sort(comparator);
		if (maxItems > 0) items = items.subList(0,  maxItems-1);
		return items;
	}

	public List<T> sort(int maxItems, Comparator<T> comparator) {
		return sort(maxItems, null, comparator);
	}
}