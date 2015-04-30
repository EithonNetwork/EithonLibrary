package net.eithon.library.move;

import org.bukkit.event.player.PlayerMoveEvent;

public interface IBlockMoverFollower {
	void moveEventHandler(PlayerMoveEvent event);
	String getName();
}
