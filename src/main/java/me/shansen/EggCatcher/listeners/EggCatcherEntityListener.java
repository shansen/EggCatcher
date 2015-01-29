package me.shansen.EggCatcher.listeners;

import java.text.DecimalFormat;
import java.util.ArrayList;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggType;
import me.shansen.EggCatcher.events.EggCaptureEvent;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcherEntityListener
  implements Listener
{
	private final boolean usePermissions;
    private final boolean useCatchChance;
    private final boolean useHealthPercentage;
    private final boolean looseEggOnFail;
    private final boolean useVaultCost;
    private final boolean useItemCost;
    private final boolean explosionEffect;
    private final boolean smokeEffect;
    private final boolean nonPlayerCatching;
    private final boolean preventCatchingBabyAnimals;
    private final boolean preventCatchingTamedAnimals;
    private final boolean preventCatchingShearedSheeps;
    private final String catchChanceSuccessMessage;
    private final String catchChanceFailMessage;
    private final String healthPercentageFailMessage;
    private final String vaultTargetBankAccount;
    private final boolean spawnChickenOnFail;
    private final boolean spawnChickenOnSuccess;
    FileConfiguration config;
    JavaPlugin plugin;
  
  public EggCatcherEntityListener(JavaPlugin plugin)
  {
	  this.config = plugin.getConfig();
      this.plugin = plugin;
      this.usePermissions = this.config.getBoolean("UsePermissions", true);
      this.useCatchChance = this.config.getBoolean("UseCatchChance", true);
      this.useHealthPercentage = this.config.getBoolean("UseHealthPercentage", false);
      this.looseEggOnFail = this.config.getBoolean("LooseEggOnFail", true);
      this.useVaultCost = this.config.getBoolean("UseVaultCost", false);
      this.useItemCost = this.config.getBoolean("UseItemCost", false);
      this.explosionEffect = this.config.getBoolean("ExplosionEffect", true);
      this.smokeEffect = this.config.getBoolean("SmokeEffect", false);
      this.nonPlayerCatching = this.config.getBoolean("NonPlayerCatching", true);
      this.catchChanceSuccessMessage = this.config.getString("Messages.CatchChanceSuccess");
      this.catchChanceFailMessage = this.config.getString("Messages.CatchChanceFail");
      this.healthPercentageFailMessage = this.config.getString("Messages.HealthPercentageFail");
      this.preventCatchingBabyAnimals = this.config.getBoolean("PreventCatchingBabyAnimals", true);
      this.preventCatchingTamedAnimals = this.config.getBoolean("PreventCatchingTamedAnimals", true);
      this.preventCatchingShearedSheeps = this.config.getBoolean("PreventCatchingShearedSheeps", true);
      this.spawnChickenOnFail = this.config.getBoolean("SpawnChickenOnFail", true);
      this.spawnChickenOnSuccess = this.config.getBoolean("SpawnChickenOnSuccess", false);
      this.vaultTargetBankAccount = this.config.getString("VaultTargetBankAccount", "");
  }
  
  @EventHandler
  public void onEntityHealthEgg(EntityDamageEvent event) {
    EntityDamageByEntityEvent damageEvent = null;
    if (!(event instanceof EntityDamageByEntityEvent)) {
      return;
    }
    
    damageEvent = (EntityDamageByEntityEvent)event;
    
    if ((damageEvent.getDamager() instanceof Player)) {
      if (((Player)damageEvent.getDamager()).getItemInHand().getType() != Material.EGG)
        return;
      event.setDamage(0.0D);
    }
    else {}
  }
  
  @SuppressWarnings("deprecation")
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
     public void onEntityStruckByEgg(EntityDamageEvent event)
     {
	     Player player = null;
	     Entity entity = event.getEntity();
	     EggType eggType = null;
	     double vaultCost = 0.0;
	     EntityDamageByEntityEvent damageEvent = null;
	     if (!(event instanceof EntityDamageByEntityEvent)) {
             return;
         }
	     damageEvent = (EntityDamageByEntityEvent) event;
	  	 if(!(damageEvent.getDamager() instanceof Player))
	  	 {
	  		 return;
	  	 }
	  	 player = (Player)damageEvent.getDamager();
	  	 if(player.getItemInHand().getType() != Material.EGG)
	  		 return;
	  	eggType = EggType.getEggType(entity);
	  	if (eggType == null) {
            return;
        }
	  	takeEgg(player);
        if (this.preventCatchingBabyAnimals) {
            if (entity instanceof Ageable) {
                if (!((Ageable) entity).isAdult()) {
                    return;
                }
            }
        }

        if (this.preventCatchingTamedAnimals) {
            if (entity instanceof Tameable) {
                if (((Tameable) entity).isTamed()) {
                    return;
                }
            }
        }

        if (this.preventCatchingShearedSheeps) {
            if (entity instanceof Sheep) {
                if (((Sheep) entity).isSheared()) {
                    return;
                }
            }
        }
       if (this.usePermissions) {
                if (!player.hasPermission("eggcatcher.catch." + eggType.getFriendlyName().toLowerCase())) {
                    player.sendMessage(config.getString("Messages.PermissionFail"));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    return;
                }
            }

            if (this.useHealthPercentage) {
                double healthPercentage = config.getDouble("HealthPercentage." + eggType.getFriendlyName());
                double currentHealth = ((LivingEntity) entity).getHealth() * 100.0 / ((LivingEntity) entity)
                        .getMaxHealth();
                if (healthPercentage < currentHealth) {
                    if (this.healthPercentageFailMessage.length() > 0) {
                        player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                    }
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    return;
                }
            }

            if (this.useCatchChance) {
                double catchChance = config.getDouble("CatchChance." + eggType.getFriendlyName());
                if (Math.random() * 100 <= catchChance) {
                    if (this.catchChanceSuccessMessage.length() > 0) {
                        player.sendMessage(catchChanceSuccessMessage);
                    }
                } else {
                    if (this.catchChanceFailMessage.length() > 0) {
                        player.sendMessage(this.catchChanceFailMessage);
                    }
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    return;
                }
            }
            
            boolean freeCatch = player.hasPermission("eggcatcher.free");

            if (this.useVaultCost && !freeCatch) {
                vaultCost = config.getDouble("VaultCost." + eggType.getFriendlyName());
                if (!EggCatcher.economy.has(player.getName(), vaultCost)) {
                    player.sendMessage(String.format(config.getString("Messages.VaultFail"), vaultCost));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    return;
                } else {
                    EggCatcher.economy.withdrawPlayer(player.getName(), vaultCost);

                    if (!this.vaultTargetBankAccount.isEmpty()) {
                        EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                    }
                    player.sendMessage(String.format(config.getString("Messages.VaultSuccess"), vaultCost));
                }
            }

            if (this.useItemCost && !freeCatch) {
                int itemId = config.getInt("ItemCost.ItemId", 266);
                int itemData = config.getInt("ItemCost.ItemData", 0);
                int itemAmount = config.getInt("ItemCost.Amount." + eggType.getFriendlyName(), 0);
                ItemStack itemStack = new ItemStack(itemId, itemAmount, (short) itemData);
                if (player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    player.sendMessage(String.format(config.getString("Messages.ItemCostSuccess"),
                            String.valueOf(itemAmount)));
                    player.getInventory().removeItem(itemStack);
                } else {
                    player.sendMessage(String.format(config.getString("Messages.ItemCostFail"),
                            String.valueOf(itemAmount)));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    }
                    return;
                }
            }
   entity.getWorld().dropItem(entity.getLocation(), makeEgg(eggType, entity));
   entity = killCatchedCreature(entity);
   if (this.explosionEffect) {
       entity.getWorld().createExplosion(entity.getLocation(), 0);
   }
   if (this.smokeEffect) {
       entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
   } 
  }
  
  @SuppressWarnings("deprecation")
 	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
     public void onEntityHitByEgg(EntityDamageEvent event) {
         EntityDamageByEntityEvent damageEvent = null;
         Egg egg = null;
         EggType eggType = null;
         double vaultCost = 0.0;
         Entity entity = event.getEntity();

         if (!(event instanceof EntityDamageByEntityEvent)) {
             return;
         }

         damageEvent = (EntityDamageByEntityEvent) event;

         if (!((damageEvent.getDamager() instanceof Egg))) {
             return;
         }
         if((damageEvent.getDamager() instanceof Egg))
         {
        	 egg = (Egg) damageEvent.getDamager();
         }
         eggType = EggType.getEggType(entity);

         if (eggType == null) {
             return;
         }

         if (!this.spawnChickenOnFail) {
             EggCatcher.eggs.add(egg);
         }

         if (this.preventCatchingBabyAnimals) {
             if (entity instanceof Ageable) {
                 if (!((Ageable) entity).isAdult()) {
                     return;
                 }
             }
         }

         if (this.preventCatchingTamedAnimals) {
             if (entity instanceof Tameable) {
                 if (((Tameable) entity).isTamed()) {
                     return;
                 }
             }
         }

         if (this.preventCatchingShearedSheeps) {
             if (entity instanceof Sheep) {
                 if (((Sheep) entity).isSheared()) {
                     return;
                 }
             }
         }


         EggCaptureEvent eggCaptureEvent = new EggCaptureEvent(entity, egg);
         this.plugin.getServer().getPluginManager().callEvent(eggCaptureEvent);
         if (eggCaptureEvent.isCancelled()) {
             return;
         }

         if (egg.getShooter() instanceof Player) {
             Player player = null;
        	 if(egg.getShooter() instanceof Player){
        		 player = (Player) egg.getShooter();
             }
             if (this.usePermissions) {
                 if (!player.hasPermission("eggcatcher.catch." + eggType.getFriendlyName().toLowerCase())) {
                     player.sendMessage(config.getString("Messages.PermissionFail"));
                     if (!this.looseEggOnFail) {
                         player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                         EggCatcher.eggs.add(egg);
                     }
                     return;
                 }
             }

             if (this.useHealthPercentage) {
                 double healthPercentage = config.getDouble("HealthPercentage." + eggType.getFriendlyName());
                 double currentHealth = ((LivingEntity) entity).getHealth() * 100.0 / ((LivingEntity) entity)
                         .getMaxHealth();
                 if (healthPercentage < currentHealth) {
                     if (this.healthPercentageFailMessage.length() > 0) {
                         player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                     }
                     if (!this.looseEggOnFail) {
                         player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                         EggCatcher.eggs.add(egg);
                     }
                     return;
                 }
             }

             if (this.useCatchChance) {
                 double catchChance = config.getDouble("CatchChance." + eggType.getFriendlyName());
                 if (Math.random() * 100 <= catchChance) {
                     if (this.catchChanceSuccessMessage.length() > 0) {
                         player.sendMessage(catchChanceSuccessMessage);
                     }
                 } else {
                     if (this.catchChanceFailMessage.length() > 0) {
                         player.sendMessage(this.catchChanceFailMessage);
                     }
                     if (!this.looseEggOnFail) {
                         player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                         EggCatcher.eggs.add(egg);
                     }
                     return;
                 }
             }
             
             boolean freeCatch = player.hasPermission("eggcatcher.free");

             if (this.useVaultCost && !freeCatch) {
                 vaultCost = config.getDouble("VaultCost." + eggType.getFriendlyName());
                 if (!EggCatcher.economy.has(player.getName(), vaultCost)) {
                     player.sendMessage(String.format(config.getString("Messages.VaultFail"), vaultCost));
                     if (!this.looseEggOnFail) {
                         player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                         EggCatcher.eggs.add(egg);
                     }
                     return;
                 } else {
                     EggCatcher.economy.withdrawPlayer(player.getName(), vaultCost);

                     if (!this.vaultTargetBankAccount.isEmpty()) {
                         EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                     }

                     player.sendMessage(String.format(config.getString("Messages.VaultSuccess"), vaultCost));
                 }
             }

             if (this.useItemCost && !freeCatch) {
                 int itemId = config.getInt("ItemCost.ItemId", 266);
                 int itemData = config.getInt("ItemCost.ItemData", 0);
                 int itemAmount = config.getInt("ItemCost.Amount." + eggType.getFriendlyName(), 0);
                 ItemStack itemStack = new ItemStack(itemId, itemAmount, (short) itemData);
                 if (player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                     player.sendMessage(String.format(config.getString("Messages.ItemCostSuccess"),
                             String.valueOf(itemAmount)));
                     player.getInventory().removeItem(itemStack);
                 } else {
                     player.sendMessage(String.format(config.getString("Messages.ItemCostFail"),
                             String.valueOf(itemAmount)));
                     if (!this.looseEggOnFail) {
                         player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                         EggCatcher.eggs.add(egg);
                     }
                     return;
                 }
             }
         } else {
             // Dispenser
             if (!this.nonPlayerCatching) {
                 return;
             }
             if (this.useCatchChance) {
                 double catchChance = config.getDouble("CatchChance." + eggType.getFriendlyName());
                 if (Math.random() * 100 > catchChance) {
                     return;
                 }
             }
         }
    if (this.explosionEffect) {
        entity.getWorld().createExplosion(entity.getLocation(), 0);
    }
    if (this.smokeEffect) {
        entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
    }
    entity.getWorld().dropItem(entity.getLocation(), makeEgg(eggType, entity));
    entity = killCatchedCreature(entity);
    
    if (!this.spawnChickenOnSuccess) {
        if (!EggCatcher.eggs.contains(egg)) {
            EggCatcher.eggs.add(egg);
        	}
    	}
  	}
  
    //******* Let's Seperate the Method for Creating the Eggs
  
    @SuppressWarnings("deprecation")
	public ItemStack makeEgg(EggType eggType, Entity entity)
    {
	ItemStack eggStack = new ItemStack(383, 1, eggType.getCreatureId());
    String customName = ((LivingEntity)entity).getCustomName();
    ItemMeta meta = eggStack.getItemMeta();
    ArrayList<String> lore = new ArrayList<String>();
    if (customName != null) {
    	 // Entity had custom name
        meta = eggStack.getItemMeta();
        meta.setDisplayName(customName);
        eggStack.setItemMeta(meta);
    }
    
    if(entity instanceof Pig) {
        if(((Pig)entity).hasSaddle()) {
            entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
        }
    }

    if ((entity instanceof LivingEntity)) {
      DecimalFormat twoDForm = new DecimalFormat("#.###");
      lore.add(ChatColor.BLUE + "Health: " + ChatColor.WHITE + Double.valueOf(twoDForm.format(((LivingEntity)entity).getHealth())) + "/" + Double.valueOf(twoDForm.format(((LivingEntity)entity).getMaxHealth())));
    }
    if ((entity instanceof Ocelot))
    {
      lore.add(ChatColor.BLUE + "Variant: " + ChatColor.WHITE + ((Ocelot)entity).getCatType().name());
    }
    if ((entity instanceof Creeper) && ((Creeper) entity).isPowered())
    {
      lore.add(ChatColor.BLUE + "Variant: " + ChatColor.WHITE + "Charged");
    }
    if ((entity instanceof Ageable))
    {
      lore.add(ChatColor.BLUE + "Age: " + ChatColor.WHITE + ((Ageable)entity).getAge());
    }
    
    if ((entity instanceof Zombie)) {
      lore.add(ChatColor.BLUE + "Baby: " + ChatColor.WHITE + ((Zombie)entity).isBaby());
      lore.add(ChatColor.BLUE + "Villager: " + ChatColor.WHITE + ((Zombie)entity).isVillager());
      if (((Zombie)entity).getEquipment().getItemInHand().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Hand: " + ChatColor.WHITE + ((Zombie)entity).getEquipment().getItemInHand().getType().toString());
      if (((Zombie)entity).getEquipment().getHelmet().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Head: " + ChatColor.WHITE + ((Zombie)entity).getEquipment().getHelmet().getType().toString());
      if (((Zombie)entity).getEquipment().getChestplate().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Chest: " + ChatColor.WHITE + ((Zombie)entity).getEquipment().getChestplate().getType().toString());
      if (((Zombie)entity).getEquipment().getLeggings().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Legs: " + ChatColor.WHITE + ((Zombie)entity).getEquipment().getLeggings().getType().toString());
      if (((Zombie)entity).getEquipment().getBoots().getType() != Material.AIR) {
        lore.add(ChatColor.BLUE + "Boots: " + ChatColor.WHITE + ((Zombie)entity).getEquipment().getBoots().getType().toString());
      }
    }
    else if ((entity instanceof PigZombie))
    {
      lore.add(ChatColor.BLUE + "Baby: " + ChatColor.WHITE + ((PigZombie)entity).isBaby());
      lore.add(ChatColor.BLUE + "Villager: " + ChatColor.WHITE + ((PigZombie)entity).isVillager());
      if (((PigZombie)entity).getEquipment().getItemInHand().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Hand: " + ChatColor.WHITE + ((PigZombie)entity).getEquipment().getItemInHand().getType().toString());
      if (((PigZombie)entity).getEquipment().getHelmet().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Head: " + ChatColor.WHITE + ((PigZombie)entity).getEquipment().getHelmet().getType().toString());
      if (((PigZombie)entity).getEquipment().getChestplate().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Chest: " + ChatColor.WHITE + ((PigZombie)entity).getEquipment().getChestplate().getType().toString());
      if (((PigZombie)entity).getEquipment().getLeggings().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Legs: " + ChatColor.WHITE + ((PigZombie)entity).getEquipment().getLeggings().getType().toString());
      if (((PigZombie)entity).getEquipment().getBoots().getType() != Material.AIR) {
        lore.add(ChatColor.BLUE + "Boots: " + ChatColor.WHITE + ((PigZombie)entity).getEquipment().getBoots().getType().toString());
      }
    }
    else if ((entity instanceof Skeleton)) {
      lore.add(ChatColor.BLUE + "Variant: " + ChatColor.WHITE + ((Skeleton)entity).getSkeletonType().name());
      if (((Skeleton)entity).getEquipment().getItemInHand().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Hand: " + ChatColor.WHITE + ((Skeleton)entity).getEquipment().getItemInHand().getType().toString());
      if (((Skeleton)entity).getEquipment().getHelmet().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Head: " + ChatColor.WHITE + ((Skeleton)entity).getEquipment().getHelmet().getType().toString());
      if (((Skeleton)entity).getEquipment().getChestplate().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Chest: " + ChatColor.WHITE + ((Skeleton)entity).getEquipment().getChestplate().getType().toString());
      if (((Skeleton)entity).getEquipment().getLeggings().getType() != Material.AIR)
        lore.add(ChatColor.BLUE + "Legs: " + ChatColor.WHITE + ((Skeleton)entity).getEquipment().getLeggings().getType().toString());
      if (((Skeleton)entity).getEquipment().getBoots().getType() != Material.AIR) {
        lore.add(ChatColor.BLUE + "Boots: " + ChatColor.WHITE + ((Skeleton)entity).getEquipment().getBoots().getType().toString());
      }
    }
    
    if ((entity instanceof Sheep)) {
      lore.add(ChatColor.BLUE + "Color: " + ChatColor.WHITE + ((Sheep)entity).getColor().name());
    }
    
    if ((entity instanceof Wolf)) {
      lore.add(ChatColor.BLUE + "Color: " + ChatColor.WHITE + ((Wolf)entity).getCollarColor().name());
    }
    
    if((entity instanceof Guardian && ((Guardian)entity).isElder())) {
    	meta = eggStack.getItemMeta();
        meta.setDisplayName("Spawn Elder Guardian");
        eggStack.setItemMeta(meta);
    	lore.add(ChatColor.BLUE + "Variant: " + ChatColor.WHITE + "Elder");
    }

    if((entity instanceof Rabbit)){
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.BLACK) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.BLACK_AND_WHITE) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.BROWN) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.GOLD) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.SALT_AND_PEPPER) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    	if (((Rabbit)entity).getRabbitType() == Rabbit.Type.WHITE) {
    		lore.add(ChatColor.BLUE + "Type: " + ChatColor.WHITE + ((Rabbit)entity).getRabbitType().toString());
    	}
    }
    if ((entity instanceof Horse))
    {
      if (((Horse)entity).getVariant() == Horse.Variant.HORSE) {
        lore.add(ChatColor.BLUE + "Color: " + ChatColor.WHITE + ((Horse)entity).getColor().name());
        lore.add(ChatColor.BLUE + "Style: " + ChatColor.WHITE + ((Horse)entity).getStyle().name());
      }
      lore.add(ChatColor.BLUE + "Variant: " + ChatColor.WHITE + ((Horse)entity).getVariant().name());
      if (((((Horse)entity).getVariant() == Horse.Variant.MULE) || (((Horse)entity).getVariant() == Horse.Variant.DONKEY)) && 
        (((Horse)entity).isCarryingChest())) {
        lore.add(ChatColor.BLUE + "Chested");
      }
      if ((((Horse)entity).getInventory() != null) && (((Horse)entity).getInventory().getArmor() != null))
        lore.add(ChatColor.BLUE + "Armor: " + ChatColor.WHITE + ((Horse)entity).getInventory().getArmor().getType().toString());
      if (((Horse)entity).getInventory().getSaddle() != null) {
        entity.getWorld().dropItem(entity.getLocation(), new ItemStack(329, 1));
      }
      if (((Horse)entity).getInventory() != null) {
        HorseInventory items = ((Horse)entity).getInventory();
        if (((Horse)entity).getInventory().getSaddle() != null)
          items.remove(((Horse)entity).getInventory().getSaddle());
        if (((Horse)entity).getInventory().getArmor() != null)
          items.remove(((Horse)entity).getInventory().getArmor());
        for (ItemStack i : items) {
          if (i != null)
            entity.getWorld().dropItem(entity.getLocation(), i);
        }
      }
    }
    if (((entity instanceof Tameable)) && 
      (((Tameable)entity).isTamed())) {
      if (((Tameable)entity).getOwner() != null) {
        lore.add(ChatColor.BLUE + "Tamed: " + ChatColor.WHITE + ((Tameable)entity).getOwner().getName());
      } else {
        lore.add(ChatColor.BLUE + "Tamed");
      }
    }
    meta.setLore(lore);
    eggStack.setItemMeta(meta);
    return eggStack;
  }
  
  @EventHandler
  public void onEntityInteract(PlayerInteractEntityEvent e) {
    if ((e.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG) && (e.getPlayer().getItemInHand().getItemMeta().getLore() != null)) {
      e.setCancelled(true);
    }
  }
  
  //Take the egg from the player when Striking the entity with the egg

  public void takeEgg(Player player){
	  ItemStack takeEgg = new ItemStack(Material.EGG, (player.getItemInHand().getAmount() - 1));  
	  if(player.getGameMode() != GameMode.CREATIVE || player.getGameMode() != GameMode.SPECTATOR){
		  if(takeEgg.getAmount() > 0){
			  player.getInventory().setItemInHand(takeEgg);
		  }else{
			  player.getInventory().setItemInHand(new ItemStack(Material.AIR));
		  }
	  }
  }
  
  //Let's Kill these creatures properly to avoid bad plugin interactions
  
  public Entity killCatchedCreature(Entity entity)
  {
	  if(entity instanceof Damageable)
	  {
		  Damageable e = (Damageable)entity;
		  entity.remove();
		  e.setHealth(0);
		  entity = (Entity)e;
	  }
	  return entity;
  }
  
  // *************   Stop Creatures killed by EggCatcher from Dropping Normal Drops       *****************
  
  @EventHandler(priority = EventPriority.HIGH)
  public void stopCaptureDrops(EntityDeathEvent event)
  {
	  Entity entity = event.getEntity();
	  LivingEntity caught = null;
	  Player player = null;
	  ItemStack hand = null;
	  if((entity instanceof LivingEntity && !(entity instanceof Player)))
	  {
		  caught = (LivingEntity)entity;
	  }else{
		  return;
	  }
	  if((caught.getKiller() != null) && (caught.getKiller() instanceof Player) || caught.getKiller() instanceof Egg)
	  {
		  if(caught.getKiller() instanceof Player){
			  player = caught.getKiller();
			  hand = player.getItemInHand();
			  if(!(hand.getType() == Material.EGG))
			  {
				  return;
			  }
		  }
		  event.getDrops().clear();
	  }
  }
}
