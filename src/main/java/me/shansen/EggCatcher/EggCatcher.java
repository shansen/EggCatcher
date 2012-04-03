/*
EggCatcher
Copyright (C) 2012  me@shansen.me

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.shansen.EggCatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;
import me.shansen.EggCatcher.listeners.EggCatcherPlayerListener;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcher extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	public static List<Egg> eggs = new ArrayList<Egg>();
	public static Economy economy = null;

	public void onDisable() {
		log.info(this.getDescription().getName() + " v"
				+ this.getDescription().getVersion() + " is disabled!");
	}

	public void onEnable() {
		this.CheckConfigurationFile();
		PluginManager pm = this.getServer().getPluginManager();
		log.info(this.getDescription().getName() + " v"
				+ this.getDescription().getVersion() + " is enabled!");

		final EggCatcherPlayerListener playerListener = new EggCatcherPlayerListener();
		final EggCatcherEntityListener entityListener = new EggCatcherEntityListener(
				this);

		pm.registerEvents(playerListener, this);
		pm.registerEvents(entityListener, this);

		if (getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> economyProvider = getServer()
					.getServicesManager().getRegistration(Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
		}
	}

	public void CheckConfigurationFile() {
		double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
		if (configVersion == 1.18) {
			//
			this.saveConfig();
		} else if (configVersion == 1.17) {
			this.getConfig().set("ConfigVersion", 1.18);
			this.saveConfig();
		} else if (configVersion == 1.16) {
			this.getConfig().set("ConfigVersion", 1.17);
			this.saveConfig();
		} else {
			this.saveResource("config.yml", true);
			this.reloadConfig();
		}
	}
}