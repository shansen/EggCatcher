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
            this.saveConfig();
            this.reloadConfig();
            return;
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

        for (String value : keysWithBoolValue) {
            config.set(value, config.getBoolean(value, true));

        }
        for (String s : keysWithStringValue) {
            config.set(s, config.getString(s, ""));
        }

        for (String s : entitiesInConfig) {
            HashMap<String, Object> entity = new HashMap<String, Object>();
            HashMap<String, Object> ItemCost = new HashMap<String, Object>();

            // generate ItemCost
            ItemCost.put("ItemName", config.getString("ItemCost.ItemId", "gold_nugget"));
            ItemCost.put("Amount", config.getDouble("ItemCost.Amount." + s, 0.0));
            entity.put("ItemCost", ItemCost);

            //  generate VaultCost
            entity.put("VaultCost", config.getDouble("VaultCost." + s, 0.0));

            // generate CatchChance
            entity.put("CatchChance", config.getDouble("CatchChance." + s, 0.0));

            // generate HealthPercentage
            entity.put("HealthPercentage", config.getDouble("HealthPercentage." + s, 0.0));


            entityList.put(s, entity);
        }
        config.set("Entity", entityList);
        config.set("ConfigVersion", 6.0);
        config.set("VaultCost", null);
        config.set("HealthPercentage", null);
        config.set("CatchChance", null);
        config.set("ItemCost", null);

        return true;
    }
}
