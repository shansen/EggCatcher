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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.mcstats.Metrics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class EggCatcher extends JavaPlugin {
	Logger log = Logger.getLogger("Minecraft");

	public static List<Egg> eggs = new ArrayList<Egg>();
	public static Economy economy = null;

	public void onDisable() {
		log.info(this.getDescription().getName() + " v"
				+ this.getDescription().getVersion() + " is disabled!");
	}

	public void onEnable() {
        this.CheckUpdate();
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
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {}
	}

    public void CheckUpdate() {
        try {
            URL url = new URL("http://dev.bukkit.org/server-mods/eggcatcher/files.rss");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            NodeList nodes = document.getElementsByTagName("item");

            String latestVersion = ((Element)nodes.item(0)).getElementsByTagName("title").item(0).getTextContent().replace("v","");
            String link = ((Element)nodes.item(0)).getElementsByTagName("link").item(0).getTextContent();
            if(!this.getDescription().getVersion().equalsIgnoreCase(latestVersion)){
               this.getLogger().info(String.format("There's a new version available (%s). Get it from %s", latestVersion, link));
            }

        } catch (Exception e) { }

    }
	public void CheckConfigurationFile() {
		double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion == 1.25) {
            //
            this.saveConfig();
        }
		else if (configVersion == 1.22) {
            this.getConfig().set("UseHealthPercentage", false);
            this.getConfig().set("HealthPercentage.Pig", 100.0);
            this.getConfig().set("HealthPercentage.Sheep", 100.0);
            this.getConfig().set("HealthPercentage.MushroomCow", 100.0);
            this.getConfig().set("HealthPercentage.Cow", 100.0);
            this.getConfig().set("HealthPercentage.Chicken", 100.0);
            this.getConfig().set("HealthPercentage.Squid", 100.0);
            this.getConfig().set("HealthPercentage.Wolf", 100.0);
            this.getConfig().set("HealthPercentage.Creeper", 100.0);
            this.getConfig().set("HealthPercentage.Skeleton", 100.0);
            this.getConfig().set("HealthPercentage.CaveSpider", 100.0);
            this.getConfig().set("HealthPercentage.Spider", 100.0);
            this.getConfig().set("HealthPercentage.PigZombie", 100.0);
            this.getConfig().set("HealthPercentage.Zombie", 100.0);
            this.getConfig().set("HealthPercentage.MagmaCube", 100.0);
            this.getConfig().set("HealthPercentage.Slime", 100.0);
            this.getConfig().set("HealthPercentage.Ghast", 100.0);
            this.getConfig().set("HealthPercentage.Enderman", 100.0);
            this.getConfig().set("HealthPercentage.Silverfish", 100.0);
            this.getConfig().set("HealthPercentage.Blaze", 100.0);
            this.getConfig().set("HealthPercentage.Villager", 100.0);
            this.getConfig().set("HealthPercentage.Ocelot", 100.0);
            this.getConfig().set("HealthPercentage.Witch", 100.0);
            this.getConfig().set("HealthPercentage.Bat", 100.0);
            this.getConfig().set("Messages.HealthPercentageFail", "The mob has more than %s percent health left and cannot be caught!");
            this.getConfig().set("ConfigVersion", 1.25);
			this.saveConfig();
		}else if (configVersion == 1.21) {
			this.getConfig().set("VaultTargetBankAccount", "");
			this.getConfig().set("ConfigVersion", 1.22);
			this.saveConfig();
		} else if (configVersion == 1.18) {
			this.getConfig().set("ConfigVersion", 1.21);
			this.getConfig().set("CatchChance.Bat", 100.0);
			this.getConfig().set("CatchChance.Witch", 100.0);
			this.getConfig().set("VaultCost.Bat", 0.0);
			this.getConfig().set("VaultCost.Witch", 0.0);
			this.getConfig().set("ItemCost.Amount.Bat", 0);
			this.getConfig().set("ItemCost.Amount.Witch", 0);
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