package net.eithon.library.facades;

import net.eithon.library.extensions.EithonPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class VaultFacade {
	private Economy _vaultEconomy;
	private EithonPlugin _eithonPlugin;
	
	public VaultFacade(EithonPlugin plugin) {
		this._eithonPlugin = plugin;
		try {
			this._vaultEconomy = Bukkit.getServicesManager().load(Economy.class);
		}
		catch (NoClassDefFoundError e) {
			plugin.getEithonLogger().warning("EithonLibrary could not connect to the Vault plugin when enabling the %s plugin.", plugin.getName());
		}
	}
	
	public boolean isConnected() {
		return this._vaultEconomy != null;
	}

	private boolean isConnectedOrLog() {
		if (isConnected()) return true;
		this._eithonPlugin.getEithonLogger().warning("EithonLibrary is not connected to the Vault plugin. VaultFacade fails.");
		return false;
	}

	public boolean withdraw(Player player, double amount) {
		if (!isConnectedOrLog()) return false;
		if (!this._vaultEconomy.has(player, amount)) return false;
		EconomyResponse response = this._vaultEconomy.withdrawPlayer(player, amount);
		return response.transactionSuccess();
	}

	public boolean deposit(Player player, double amount) {
		EconomyResponse response = this._vaultEconomy.depositPlayer(player, amount);
		return response.transactionSuccess();
	}
}
