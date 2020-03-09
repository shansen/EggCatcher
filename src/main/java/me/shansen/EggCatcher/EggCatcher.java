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
import org.bstats.bukkit.Metrics;
import java.util.ArrayList;
import java.util.List;

public class EggCatcher extends JavaPlugin {
    public static List<Egg> eggs = new ArrayList<Egg>();
    public static Economy economy = null;

    public void onDisable() {
    }

    public void onEnable() {
        this.CheckConfigurationFile();

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

        Metrics metrics = new Metrics(this, 894);
    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion == 5.0) {
            this.saveConfig();
        } else {
            this.saveResource("config.yml", true);
            this.reloadConfig();
        }
    }
}
