package net.eithon.library.title;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import io.puharesource.mc.titlemanager.api.TabTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import net.eithon.library.extensions.EithonPlugin;
import net.eithon.library.plugin.PluginMisc;
import net.eithon.library.time.ICountDownListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

// Uses TitleManager, https://github.com/Puharesource/TitleManager
public class Title {
	private static Title singleton;
	private boolean _hasTitleManager;

	private Title() {
		this._hasTitleManager = PluginMisc.isPluginEnabled("TitleManager");
	}

	public static Title get() {
		if (singleton == null) singleton = new Title();
		return singleton;
	}

	private boolean canUseFloatingText() {
		if (!this._hasTitleManager) {
			this._hasTitleManager = PluginMisc.isPluginEnabled("TitleManager");			
		}
		return this._hasTitleManager;
	}

	public void sendFloatingText(Player player, String title, int fadeIn, int stay, int fadeOut) {
		if (canUseFloatingText()) {
			new TitleObject(title, TitleType.TITLE)
			.setFadeIn(fadeIn)
			.setStay(stay)
			.setFadeOut(fadeOut)
			.send(player);
		} else {
			player.sendMessage(title);
		}
	}

	public void sendFloatingText(Player player, String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (canUseFloatingText()) {
			TitleObject titleObject = new TitleObject(title, subTitle)
			.setFadeIn(fadeIn)
			.setStay(stay)
			.setFadeOut(fadeOut);
			if (player == null) titleObject.broadcast();
			else titleObject.send(player);
		} else {
			String message = String.format("%s\n%s", title, subTitle);
			if (player == null) Bukkit.getServer().broadcastMessage(message);
			else player.sendMessage(message);
		}
	}

	public void sendActionbarMessage(Player player, String message) {
		if (canUseFloatingText()) {
			ActionbarTitleObject titleObject = new ActionbarTitleObject(message);
			if (player == null) titleObject.broadcast();
			else titleObject.send(player);
		} else {
			if (player == null) Bukkit.getServer().broadcastMessage(message);
			else player.sendMessage(message);
		}
	}

	public void sendHeaderAndFooter(Player player, String header, String footer) {
		if (canUseFloatingText()) {
			TabTitleObject titleObject = new TabTitleObject(header, footer);
			if (player == null) titleObject.broadcast();
			else titleObject.send(player);
		} else {
			String message = String.format("%s\n%s", header, footer);
			if (player == null) Bukkit.getServer().broadcastMessage(message);
			else player.sendMessage(message);
		}
	}
	
	public void CountDown(EithonPlugin plugin, Player player, long countFrom, ICountDownListener listener) {
		new net.eithon.library.time.CountDown(plugin, countFrom, 1000, new ICountDownListener() {
			public boolean isCancelled(long remainingIntervals) {
				sendActionbarMessage(player, String.format("%d", remainingIntervals));
				return listener.isCancelled(remainingIntervals);
			}
			public void afterCancelTask() {
				sendActionbarMessage(player, "Cancelled");
				listener.afterCancelTask();
			}
			public void afterDoneTask() {
				listener.afterDoneTask();
			}
		}).start(plugin.getServer().getScheduler());
	}
}
