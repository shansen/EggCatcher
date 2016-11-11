package me.shansen.EggCatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;
import me.shansen.EggCatcher.listeners.EggCatcherPlayerListener;
import me.shansen.EggCatcher.mcstats.Metrics;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Egg;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcher
extends JavaPlugin {
    public static List<Egg> eggs = new ArrayList<Egg>();
//GTN removed  public static Economy economy = null;
//GTN added
  public final Logger logger;
  public boolean econSupport;
  public static Economy economy;

    public void onDisable() {
    }

    public void onEnable() {
//GTN removed        RegisteredServiceProvider economyProvider;
        this.CheckConfigurationFile();
        PluginManager pm = this.getServer().getPluginManager();
        EggCatcherPlayerListener playerListener = new EggCatcherPlayerListener();
        EggCatcherEntityListener entityListener = new EggCatcherEntityListener(this);
        pm.registerEvents((Listener)playerListener, (Plugin)this);
        pm.registerEvents((Listener)entityListener, (Plugin)this);
//GTN removed        if (this.getServer().getPluginManager().getPlugin("Vault") != null && (economyProvider = this.getServer().getServicesManager().getRegistration((Class)Economy.class)) != null) {
//GTN removed           economy = (Economy)economyProvider.getProvider();
//GTN removed       }
//GTN added
        if (!this.setupEconomy()) {
            this.logger.warning(String.format("[%s] Vault not found! Economy support disabled!", this.getDescription().getName()));
            this.econSupport = false;
        }
        else {
            this.logger.info(String.format("[%s] Vault found! Economy support enabled!", this.getDescription().getName()));
        }
//GTN end of addition
        try {
            Metrics metrics = new Metrics((Plugin)this);
            metrics.start();
        }
        catch (IOException metrics) {
            // empty catch block
        }
    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion == 2.7) {
        	this.saveConfig();
        } else if (configVersion == 2.6) {
            this.getConfig().set("CatchChance.IronGolem", (Object)100.0);
            this.getConfig().set("VaultCost.IronGolem", (Object)100.0);
            this.getConfig().set("ItemCost.Amount.IronGolem", (Object)5);
            this.getConfig().set("HealthPercentage.IronGolem", (Object)100.0);
            this.getConfig().set("ConfigVersion", (Object)2.7);
            this.saveConfig();
        } else if (configVersion == 2.5) {
            this.getConfig().set("CatchChance.PolarBear", (Object)100.0);
            this.getConfig().set("VaultCost.PolarBear", (Object)0);
            this.getConfig().set("ItemCost.Amount.PolarBear", (Object)0);
            this.getConfig().set("HealthPercentage.PolarBear", (Object)100.0);
            this.getConfig().set("ConfigVersion", (Object)2.6);
            this.saveConfig();
        } else if (configVersion == 2.2) {
            this.getConfig().set("DeleteVillagerInventoryOnCatch", (Object)false);
            this.getConfig().set("ConfigVersion", (Object)2.5);
            this.saveConfig();
        } else if (configVersion == 2.0) {
            this.getConfig().set("CatchChance.Endermite", (Object)100.0);
            this.getConfig().set("VaultCost.Endermite", (Object)0);
            this.getConfig().set("ItemCost.Amount.Endermite", (Object)0);
            this.getConfig().set("HealthPercentage.Endermite", (Object)100.0);
            this.getConfig().set("CatchChance.Guardian", (Object)100.0);
            this.getConfig().set("VaultCost.Guardian", (Object)0);
            this.getConfig().set("ItemCost.Amount.Guardian", (Object)0);
            this.getConfig().set("HealthPercentage.Guardian", (Object)100.0);
            this.getConfig().set("CatchChance.Rabbit", (Object)100.0);
            this.getConfig().set("VaultCost.Rabbit", (Object)0);
            this.getConfig().set("ItemCost.Amount.Rabbit", (Object)0);
            this.getConfig().set("HealthPercentage.Rabbit", (Object)100.0);
            this.getConfig().set("ConfigVersion", (Object)2.2);
            this.saveConfig();
        } else {
            this.saveResource("config.yml", true);
            this.reloadConfig();
        }
    }

//GTN added
    private boolean setupEconomy() {
        final RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            EggCatcher.economy = (Economy)economyProvider.getProvider();
        }
        return EggCatcher.economy != null;
    }
//GTN added
    public EggCatcher() {
        this.logger = Logger.getLogger("Minecraft");
        this.econSupport = true;
    }

}

