package me.shansen.EggCatcher.listeners;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EggCatcherPlayerClickListener implements org.bukkit.event.Listener
{
  @SuppressWarnings("deprecation")
@EventHandler
  public void onClick(PlayerInteractEvent e)
  {
    if (e.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)
      return;
    if (e.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG) {
      ItemStack i = e.getPlayer().getItemInHand();
      int z = i.getDurability();
      if (i.getItemMeta() == null)
        return;
      if (i.getItemMeta().getLore() == null)
        return;
      e.setCancelled(true);
      EntityType type = EntityType.fromId(z);
      if (type != null) {
        LivingEntity entity = (LivingEntity)e.getClickedBlock().getLocation().getWorld().spawnEntity(e.getClickedBlock().getRelative(org.bukkit.block.BlockFace.UP).getLocation(), type);
        if ((e.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE) && 
          (e.getPlayer().getItemInHand() != null))
          if (e.getPlayer().getItemInHand().getAmount() == 1) {
            e.getPlayer().setItemInHand(null);
          } else {
            ItemStack iz = e.getPlayer().getItemInHand();
            iz.setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
            e.getPlayer().setItemInHand(iz);
          }
        if (i.getItemMeta().getDisplayName() != null)
        {
          entity.setCustomName(i.getItemMeta().getDisplayName());
          entity.setCustomNameVisible(true);
        }
        for (String s : i.getItemMeta().getLore()) {
          if (s.contains("Health:")) {
            String l = s.replace("§9", "");
            l = l.replace("§f", "");
            l = l.split(": ")[1];
            entity.setMaxHealth(Double.parseDouble(l.split("/")[1]));
            entity.setHealth(Double.parseDouble(l.split("/")[0]));
          }
          if (s.contains("Variant:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Variant: ", "");
            if ((entity instanceof Ocelot)) {
              ((Ocelot)entity).setCatType(org.bukkit.entity.Ocelot.Type.valueOf(l));
            }
            if ((entity instanceof Horse)) {
              ((Horse)entity).setVariant(org.bukkit.entity.Horse.Variant.valueOf(l));
            }
            if ((entity instanceof Skeleton)) {
              ((Skeleton)entity).setSkeletonType(Skeleton.SkeletonType.valueOf(l));
            }
            if ((entity instanceof Guardian)){
              ((Guardian)entity).setElder(true);	
            }
          }
          if (s.contains("Age:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Age: ", "");
            if ((entity instanceof Ageable)) {
              ((Ageable)entity).setAge(Integer.parseInt(l));
            }
          }
          if (s.contains("Baby:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Baby: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).setBaby(Boolean.parseBoolean(l));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).setBaby(Boolean.parseBoolean(l));
            }
          }
          if (s.contains("Villager:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Villager: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).setVillager(Boolean.parseBoolean(l));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).setVillager(Boolean.parseBoolean(l));
            }
          }
          if (s.contains("Hand:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Hand: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).getEquipment().setItemInHand(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).getEquipment().setItemInHand(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Skeleton)) {
              ((Skeleton)entity).getEquipment().setItemInHand(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
          }
          if (s.contains("Head:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Head: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).getEquipment().setHelmet(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).getEquipment().setHelmet(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Skeleton)) {
              ((Skeleton)entity).getEquipment().setHelmet(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
          }
          if (s.contains("Chest:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Chest: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).getEquipment().setChestplate(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).getEquipment().setChestplate(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Skeleton)) {
              ((Skeleton)entity).getEquipment().setChestplate(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
          }
          if (s.contains("Legs:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Legs: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).getEquipment().setLeggings(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).getEquipment().setLeggings(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Skeleton)) {
              ((Skeleton)entity).getEquipment().setLeggings(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
          }
          if (s.contains("Boots:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Boots: ", "");
            if ((entity instanceof PigZombie)) {
              ((PigZombie)entity).getEquipment().setBoots(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Zombie)) {
              ((Zombie)entity).getEquipment().setBoots(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
            if ((entity instanceof Skeleton)) {
              ((Skeleton)entity).getEquipment().setBoots(new ItemStack(Material.getMaterial(l.toUpperCase())));
            }
          }
          if (s.contains("Tamed:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Tamed: ", "");
            if ((entity instanceof Tameable)) {
              ((Tameable)entity).setTamed(true);
              if (Bukkit.getPlayerExact(l) != null) {
                ((Tameable)entity).setOwner(Bukkit.getPlayerExact(l));
              } else {
                ((Tameable)entity).setOwner(Bukkit.getOfflinePlayer(l));
              }
            }
          }
          if (s.contains("Color:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Color: ", "");
            if ((entity instanceof Horse)) {
              ((Horse)entity).setColor(org.bukkit.entity.Horse.Color.valueOf(l));
            }
            if ((entity instanceof Sheep)) {
              ((Sheep)entity).setColor(DyeColor.valueOf(l));
            }
            if ((entity instanceof Wolf)) {
              ((Wolf)entity).setCollarColor(DyeColor.valueOf(l));
            }
          }
          if (s.contains("Style:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Style: ", "");
            if ((entity instanceof Horse)) {
              ((Horse)entity).setStyle(Horse.Style.valueOf(l));
            }
          }
          if (s.contains("Armor:")) {
            String l = s.replaceAll("§9", "");
            l = l.replaceAll("§f", "");
            l = l.replaceAll("Armor: ", "");
            if ((entity instanceof Horse)) {
              ((Horse)entity).getInventory().setArmor(new ItemStack(Material.getMaterial(l)));
            }
          }
        }
      }
    }
  }
}
