package com.minecraftheads.EggCatcher;

import com.minecraftheads.EggCatcher.listeners.EggCatcherEntityListener;
import com.minecraftheads.EggCatcher.listeners.EggCatcherPlayerListener;
import com.minecraftheads.EggCatcher.listeners.EggCatcherSpawnListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
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
        final EggCatcherSpawnListener spawnListener = new EggCatcherSpawnListener(this);

        pm.registerEvents(playerListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(spawnListener, this);

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration
                    (Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
        }

    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion < 6.0) {
            MigrateConfigFile();
        }
        if (configVersion == 6.0) {
            this.saveConfig();
        } else {
            this.saveResource("config.yml", true);
            this.reloadConfig();
        }
    }

    // This is a ugly piece if shit but i want to make the transition as smooth as possible
    public boolean MigrateConfigFile() {
        String[] keysWithBoolValue = {"UsePermissions", "UseCatchChance", "LooseEggOnFail", "UseVaultCost", "UseItemCost", "UseHealthPercentage", "ExplosionEffect", "SmokeEffect", "NonPlayerCatching", "PreventCatchingBabyAnimals", "PreventCatchingTamedAnimals", "PreventCatchingShearedSheeps", "SpawnChickenOnSuccess", "SpawnChickenOnFail", "DeleteVillagerInventoryOnCatch", "LogEggCaptures", "setPersistence"};
        String[] keysWithStringValue = {"VaultTargetBankAccount"};
        String[] entitiesInConfig = {"Axolotl", "Bat", "Bee", "Blaze", "Cat", "CaveSpider", "Chicken", "Cod", "Cow", "Creeper", "Dolphin", "Donkey", "Drowned", "ElderGuardian", "Enderman", "Endermite", "Evoker", "Fox", "Ghast", "GlowSquid", "Goat", "Guardian", "Hoglin", "Horse", "Husk", "Llama", "MagmaCube", "Mule", "MushroomCow", "Ocelot", "Panda", "Parrot", "Phantom", "Pig", "Piglin", "PiglinBrute", "PigZombie", "Pillager", "PolarBear", "Pufferfish", "Rabbit", "Ravager", "Salmon", "Sheep", "Shulker", "Silverfish", "Skeleton", "SkeletonHorse", "Slime", "Spider", "Squid", "Stray", "Strider", "TraderLlama", "TropicalFish", "Turtle", "Vex", "Villager", "Vindicator", "WanderingTrader", "Witch", "WitherSkeleton", "Wolf", "Zoglin", "Zombie", "ZombieHorse", "ZombieVillager", "ZombifiedPiglin"};


        FileConfiguration config = this.getConfig();

        HashMap <String, HashMap> entityList = new HashMap<>();

        for (int i = 0; i < keysWithBoolValue.length; i++) {
            config.set(keysWithBoolValue[i], config.getBoolean(keysWithBoolValue[i], true));

        }
        for (int i = 0; i < keysWithStringValue.length; i++) {
            config.set(keysWithStringValue[i], config.getString(keysWithStringValue[i], ""));
        }

        for (int i = 0; i < entitiesInConfig.length; i++) {
            HashMap<String, HashMap> entity = new HashMap<>();
            HashMap<String, Double> HealthPercentage = new HashMap<String, Double>();
            HashMap<String, Double> CatchChance = new HashMap<>();
            HashMap<String, Double> VaultCost = new HashMap<>();
            HashMap<String, HashMap> ItemCost = new HashMap<>();
            HashMap<String, Double> ItemAmount = new HashMap<>();
            HashMap<String, String> ItemName = new HashMap<>();

            // generate ItemCost
            ItemName.put("ItemName", config.getString("ItemCost.ItemId", "gold_nugget"));
            ItemAmount.put("Amount", config.getDouble("ItemCost.Amount." + entitiesInConfig[i], 0.0));
            ItemCost.put("ItemCost", ItemAmount);
            ItemCost.put("ItemCost", ItemName);

            //  generate VaultCost
            VaultCost.put("VaultCost", config.getDouble("VaultCost." + entitiesInConfig[i], 0.0));

            // generate CatchChance
            CatchChance.put("CatchChance", config.getDouble("CatchChance." + entitiesInConfig[i], 0.0));

            // generate HealthPercentage
            HealthPercentage.put("HealthPercentage", config.getDouble("HealthPercentage." + entitiesInConfig[i], 0.0));

            // Build new Config Hash for entity
            entity.put(entitiesInConfig[i], ItemCost);
            entity.put(entitiesInConfig[i], VaultCost);
            entity.put(entitiesInConfig[i], HealthPercentage);
            entity.put(entitiesInConfig[i], CatchChance);

            entityList.put(entitiesInConfig[i], entity);
        }
        config.set("Entity", entityList);
        config.set("Version", 6.0);
        config.set("VaultCost", null);
        config.set("HealthPercentage", null);
        config.set("CatchChance", null);
        config.set("ItemCost", null);

        return true;
    }
}
