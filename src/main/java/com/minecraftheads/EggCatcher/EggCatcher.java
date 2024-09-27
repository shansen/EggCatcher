package com.minecraftheads.EggCatcher;

import com.minecraftheads.EggCatcher.listeners.EggCatcherEntityListener;
import com.minecraftheads.EggCatcher.listeners.EggCatcherPlayerListener;
import com.minecraftheads.EggCatcher.listeners.EggCatcherSpawnListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcher extends JavaPlugin {
    public static List<Egg> eggs = new ArrayList();
    public static Economy economy = null;

    public EggCatcher() {
    }

    public void onDisable() {
    }

    public void onEnable() {
        this.CheckConfigurationFile();
        PluginManager pm = this.getServer().getPluginManager();
        EggCatcherPlayerListener playerListener = new EggCatcherPlayerListener();
        EggCatcherEntityListener entityListener = new EggCatcherEntityListener(this);
        EggCatcherSpawnListener spawnListener = new EggCatcherSpawnListener(this);
        pm.registerEvents(playerListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(spawnListener, this);
        if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = (Economy)economyProvider.getProvider();
            }
        }

    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0D);
        if (configVersion < 6.2D) {
            this.MigrateConfigFile();
            this.saveConfig();
            this.reloadConfig();
        } else {
            if (configVersion == 6.3D) {
                this.saveConfig();
            } else {
                this.saveResource("config.yml", true);
                this.reloadConfig();
            }

        }
    }

    public boolean MigrateConfigFile() {
        String[] keysWithBoolValue = new String[]{"UsePermissions", "UseCatchChance", "LooseEggOnFail", "UseVaultCost", "UseItemCost", "UseHealthPercentage", "ExplosionEffect", "SmokeEffect", "NonPlayerCatching", "PreventCatchingBabyAnimals", "PreventCatchingTamedAnimals", "PreventCatchingShearedSheeps", "SpawnChickenOnSuccess", "SpawnChickenOnFail", "DeleteVillagerInventoryOnCatch", "LogEggCaptures", "setPersistence"};
        String[] keysWithStringValue = new String[]{"VaultTargetBankAccount"};
        String[] entitiesInConfig = new String[]{"Axolotl", "Bat", "Bee", "Blaze", "Cat", "Camel", "CaveSpider", "Chicken", "Cod", "Cow", "Creeper", "Dolphin", "Donkey", "Drowned", "ElderGuardian", "Enderman", "Endermite", "Evoker", "Fox", "Ghast", "GlowSquid", "Goat", "Guardian", "Hoglin", "Horse", "Husk", "Llama", "MagmaCube", "Mule", "MushroomCow", "Ocelot", "Panda", "Parrot", "Phantom", "Pig", "Piglin", "PiglinBrute", "PigZombie", "Pillager", "PolarBear", "Pufferfish", "Rabbit", "Ravager", "Salmon", "Sheep", "Shulker", "Silverfish", "Skeleton", "SkeletonHorse", "Slime", "Sniffer", "Spider", "Squid", "Stray", "Strider", "TraderLlama", "TropicalFish", "Turtle", "Vex", "Villager", "Vindicator", "WanderingTrader", "Witch", "WitherSkeleton", "Wolf", "Zoglin", "Zombie", "ZombieHorse", "ZombieVillager", "ZombifiedPiglin"};
        FileConfiguration config = this.getConfig();
        HashMap<String, HashMap> entityList = new HashMap();
        String[] var6 = keysWithBoolValue;
        int var7 = keysWithBoolValue.length;

        int var8;
        String s;
        for(var8 = 0; var8 < var7; ++var8) {
            s = var6[var8];
            config.set(s, config.getBoolean(s, true));
        }

        var6 = keysWithStringValue;
        var7 = keysWithStringValue.length;

        for(var8 = 0; var8 < var7; ++var8) {
            s = var6[var8];
            config.set(s, config.getString(s, ""));
        }

        var6 = entitiesInConfig;
        var7 = entitiesInConfig.length;

        for(var8 = 0; var8 < var7; ++var8) {
            s = var6[var8];
            HashMap<String, Object> entity = new HashMap();
            HashMap<String, Object> ItemCost = new HashMap();
            ItemCost.put("ItemName", config.getString("ItemCost.ItemId", "gold_nugget"));
            ItemCost.put("Amount", config.getDouble("ItemCost.Amount." + s, 0.0D));
            entity.put("ItemCost", ItemCost);
            entity.put("VaultCost", config.getDouble("VaultCost." + s, 0.0D));
            entity.put("CatchChance", config.getDouble("CatchChance." + s, 0.0D));
            entity.put("HealthPercentage", config.getDouble("HealthPercentage." + s, 0.0D));
            entityList.put(s, entity);
        }

        config.set("Entity", entityList);
        config.set("ConfigVersion", 6.3D);
        config.set("VaultCost", (Object)null);
        config.set("HealthPercentage", (Object)null);
        config.set("CatchChance", (Object)null);
        config.set("ItemCost", (Object)null);
        return true;
    }
}
