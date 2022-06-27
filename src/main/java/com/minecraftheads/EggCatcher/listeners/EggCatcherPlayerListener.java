package com.minecraftheads.EggCatcher.listeners;

import com.minecraftheads.EggCatcher.EggCatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class EggCatcherPlayerListener implements Listener {
    public EggCatcherPlayerListener() {
    }

    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        if (EggCatcher.eggs.contains(event.getEgg())) {
            event.setHatching(false);
            EggCatcher.eggs.remove(event.getEgg());
        }

    }
}