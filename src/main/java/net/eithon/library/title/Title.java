package net.eithon.library.title;

import io.puharesource.mc.titlemanager.api.TitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject.TitleType;
import net.eithon.library.plugin.PluginMisc;

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
			new TitleObject(title, subTitle)
			.setFadeIn(fadeIn)
			.setStay(stay)
			.setFadeOut(fadeOut)
			.send(player);
		} else {
			player.sendMessage(String.format("%s\n%s", title, subTitle));
		}
	}
}
