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
import java.io.IOException;
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
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        }
    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion == 2.5) {
            //
            this.saveConfig();
        } else if (configVersion == 2.2) {
            this.getConfig().set("DeleteVillagerInventoryOnCatch", false);

            this.getConfig().set("ConfigVersion", 2.5);
            this.saveConfig();
        } else if (configVersion == 2.0) {
            this.getConfig().set("CatchChance.Endermite", 100.0);
            this.getConfig().set("VaultCost.Endermite", 0);
            this.getConfig().set("ItemCost.Amount.Endermite", 0);
            this.getConfig().set("HealthPercentage.Endermite", 100.0);

            this.getConfig().set("CatchChance.Guardian", 100.0);
            this.getConfig().set("VaultCost.Guardian", 0);
            this.getConfig().set("ItemCost.Amount.Guardian", 0);
            this.getConfig().set("HealthPercentage.Guardian", 100.0);

            this.getConfig().set("CatchChance.Rabbit", 100.0);
            this.getConfig().set("VaultCost.Rabbit", 0);
            this.getConfig().set("ItemCost.Amount.Rabbit", 0);
            this.getConfig().set("HealthPercentage.Rabbit", 100.0);

            this.getConfig().set("ConfigVersion", 2.2);
            this.saveConfig();
        } else {
            this.saveResource("config.yml", true);
            this.reloadConfig();
        }
    }
}