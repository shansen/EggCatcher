package me.shansen.EggCatcher.listeners;

import me.shansen.EggCatcher.EggCatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EggCatcherPlayerListener
implements Listener {
    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        if (EggCatcher.eggs.contains((Object)event.getEgg())) {
            event.setHatching(false);
            EggCatcher.eggs.remove((Object)event.getEgg());
        }
    }

    //GTN SpawnMobFromEgg Event
  	@EventHandler
  	public void spawnMobFromEgg(PlayerInteractEvent event) {
  		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
  			Player player = event.getPlayer();
  			if (player.getInventory().getItemInMainHand().getType() == Material.MONSTER_EGG) {
  				ItemStack spawnEggItem = player.getInventory().getItemInMainHand();
//GTN remove unneeded Entity entity = null;
  				EntityType spawnEggEntity = EggNBT.getSpawnEggEntityType(spawnEggItem);
//GTN debug  				player.sendMessage("EggType EntityType - " + EggNBT.getSpawnEggEntityType(eggItem));
//GTN rewrite  				if (spawnEggEntity.toString() == "IRON_GOLEM") {
  				if (!(spawnEggEntity == null)) {
//GTN rewrite  			Location loc = ((PlayerInteractEvent)event).getClickedBlock().getRelative(((PlayerInteractEvent)event).getBlockFace()).getLocation();
  					Location loc = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
//GTN remove unneeded	entity = player.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);
//GTN rewrite   		player.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);	
  					player.getWorld().spawnEntity(loc, spawnEggEntity);
  					event.setCancelled(true);
  	  				int spawnEggAmount = spawnEggItem.getAmount();
  	  				if (spawnEggAmount == 1) {
  	  					player.getInventory().setItemInMainHand(null);
  	  				} else if (spawnEggAmount > 1) {
  	  				spawnEggItem.setAmount(spawnEggAmount - 1);
  	  					player.getInventory().setItemInMainHand(spawnEggItem);
  	  				}
  				}
  			}
  		}
  	}

}

