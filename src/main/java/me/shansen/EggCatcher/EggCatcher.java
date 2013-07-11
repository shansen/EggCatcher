/*
EggCatcher
Copyright (C) 2012, 2013  me@shansen.me

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

import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;
import me.shansen.EggCatcher.listeners.EggCatcherPlayerListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Egg;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EggCatcher extends JavaPlugin {
    public static List<Egg> eggs = new ArrayList<Egg>();
    public static Economy economy = null;

    public void onDisable() {
    }

    public void onEnable() {
        this.CheckConfigurationFile();

        if (this.getConfig().getBoolean("CheckForUpdates")) {
            this.CheckUpdate();
        }

        PluginManager pm = this.getServer().getPluginManager();

        final EggCatcherPlayerListener playerListener = new EggCatcherPlayerListener();
        final EggCatcherEntityListener entityListener = new EggCatcherEntityListener(this);

        pm.registerEvents(playerListener, this);
        pm.registerEvents(entityListener, this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration
                    (Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        }
    }

    public void CheckUpdate() {
        try {
            URL url = new URL("http://dev.bukkit.org/server-mods/eggcatcher/files.rss");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            NodeList nodes = document.getElementsByTagName("item");

            String latestVersion = ((Element) nodes.item(0)).getElementsByTagName("title").item(0).getTextContent()
                    .replace("v", "");
            String link = ((Element) nodes.item(0)).getElementsByTagName("link").item(0).getTextContent();
            if (!this.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
                this.getLogger().info(String.format("There's a new version available (%s). Get it from %s",
                        latestVersion, link));
            }

        } catch (Exception e) {
        }

    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion != 2.00) {
            this.saveResource("config.yml", true);
            this.reloadConfig();
        }
    }
}