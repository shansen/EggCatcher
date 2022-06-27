package com.minecraftheads.EggCatcher.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcherSpawnListener implements Listener {
    private final boolean setPersistence;
    FileConfiguration config;
    JavaPlugin plugin;

    public EggCatcherSpawnListener(JavaPlugin plugin) {
        this.config = plugin.getConfig();
        this.plugin = plugin;
        this.setPersistence = this.config.getBoolean("setPersistence", true);
    }

    @EventHandler(
            ignoreCancelled = false,
            priority = EventPriority.MONITOR
    )
    public void onEntitySpawnedByEgg(CreatureSpawnEvent event) {
        if (this.setPersistence) {
            if (!(event instanceof CreatureSpawnEvent)) {
                return;
            }

            if (event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
                event.getEntity().setRemoveWhenFarAway(false);
            }
        }

    }
}
