package me.shansen.EggCatcher.listeners;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggType;
import me.shansen.EggCatcher.events.EggCaptureEvent;
import me.shansen.nbt.NbtReflection;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcherEntityListener
implements Listener {
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
    private final boolean deleteVillagerInventoryOnCatch;
    FileConfiguration config;
    JavaPlugin plugin;

    public EggCatcherEntityListener(JavaPlugin plugin) {
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
        this.deleteVillagerInventoryOnCatch = this.config.getBoolean("DeleteVillagerInventoryOnCatch", false);
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onEntityHitByEgg(EntityDamageEvent event) {
    	//GTN added Booleans
    	Boolean playerHasFunds = false;
    	Boolean playerHasItems = false;
        EntityDamageByEntityEvent damageEvent = null;
        Egg egg = null;
        EggType eggType = null;
        double vaultCost = 0.0;
        Entity entity = event.getEntity();
        if (!(event instanceof EntityDamageByEntityEvent)) {
            return;
        }
        damageEvent = (EntityDamageByEntityEvent)event;
        if (!(damageEvent.getDamager() instanceof Egg)) {
            return;
        }
        egg = (Egg)damageEvent.getDamager();
        eggType = EggType.getEggType(entity);
        if (eggType == null) {
            return;
        }
        if (!this.spawnChickenOnFail) {
            EggCatcher.eggs.add(egg);
        }
        if (this.preventCatchingBabyAnimals && entity instanceof Ageable && !((Ageable)entity).isAdult()) {
            return;
        }
        if (this.preventCatchingTamedAnimals && entity instanceof Tameable && ((Tameable)entity).isTamed()) {
            return;
        }
        if (this.preventCatchingShearedSheeps && entity instanceof Sheep && ((Sheep)entity).isSheared()) {
            return;
        }
        EggCaptureEvent eggCaptureEvent = new EggCaptureEvent(entity, egg);
        this.plugin.getServer().getPluginManager().callEvent((Event)eggCaptureEvent);
        if (eggCaptureEvent.isCancelled()) {
            return;
        }
        if (egg.getShooter() instanceof Player) {
            double currentHealth;
            double healthPercentage;
            Player player = (Player)egg.getShooter();
            if (this.usePermissions && !player.hasPermission("eggcatcher.catch." + eggType.getFriendlyName().toLowerCase())) {
                player.sendMessage(this.config.getString("Messages.PermissionFail"));
                if (this.looseEggOnFail) return;
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                EggCatcher.eggs.add(egg);
                return;
            }
            if (this.useHealthPercentage && (healthPercentage = this.config.getDouble("HealthPercentage." + eggType.getFriendlyName())) < (currentHealth = ((LivingEntity)entity).getHealth() * 100.0 / ((LivingEntity)entity).getMaxHealth())) {
                if (this.healthPercentageFailMessage.length() > 0) {
                    player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                }
                if (this.looseEggOnFail) return;
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                EggCatcher.eggs.add(egg);
                return;
            }
            if (this.useCatchChance) {
                double catchChance = this.config.getDouble("CatchChance." + eggType.getFriendlyName());
                if (Math.random() * 100.0 <= catchChance) {
                    if (this.catchChanceSuccessMessage.length() > 0) {
                        player.sendMessage(this.catchChanceSuccessMessage);
                    }
                } else {
                    if (this.catchChanceFailMessage.length() > 0) {
                        player.sendMessage(this.catchChanceFailMessage);
                    }
                    if (this.looseEggOnFail) return;
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                    EggCatcher.eggs.add(egg);
                    return;
                }
            }
            boolean freeCatch = player.hasPermission("eggcatcher.free");
//GTN The structure of the plugin makes it so that
//GTN The catch cost for Vault and Item are seperate
//GTN so the charge must be either/or NOT both!
            
//GTN Let me restructure this as a solution!
//GTN so we have 'useVaultCost', 'useItemCost', 'freeCatch'
//GTN proposed structure
//GTN Use a Boolean of playerHasFunds
//GTN Use a Boolean of playerHasItems
//GTN process as normal but delay the PAYMENT
//GTN Use three PAYMENT methods of 'VaultOnly' or 'ItemOnly' or 'Both'
//GTN *** check if playerHasFunds
            if (this.useVaultCost && !freeCatch) {
            	//GTN testing  player.sendMessage(String.format("You have %s", EggCatcher.economy.format(EggCatcher.economy.getBalance(player))));//.getBalance(player.getName()))));
                vaultCost = this.config.getDouble("VaultCost." + eggType.getFriendlyName());
                //GTN was if (!EggCatcher.economy.has(player.getName(), vaultCost)) {
                //GTN replaced with
                if (EggCatcher.economy.getBalance(player) < vaultCost) {
                    player.sendMessage(String.format(this.config.getString("Messages.VaultFail"), vaultCost));
                    if (this.looseEggOnFail) return;
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                    EggCatcher.eggs.add(egg);
                    return;
                }
//GTN Move this to take payment
                playerHasFunds = true;
                //GTN was EggCatcher.economy.withdrawPlayer(player.getName(), vaultCost);
                //GTN replaced with
                //EggCatcher.economy.withdrawPlayer(player, vaultCost);
                //if (!this.vaultTargetBankAccount.isEmpty()) {
                //    EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                //}
                //player.sendMessage(String.format(this.config.getString("Messages.VaultSuccess"), vaultCost));
            }
//GTN *** check if playerHasItems as MATERIAL
            if (this.useItemCost && !freeCatch) {
            	//GTN Replaced static value of 'Gold_Ingot' (266) with configured ItemId.MATERIAL
            	//GTN was int itemId = this.config.getInt("ItemCost.ItemId", 266);
            	//GTN replaced with
            	
                String itemId = this.config.getString("ItemCost.ItemId");
                int itemData = this.config.getInt("ItemCost.ItemData", 0);
                int itemAmount = this.config.getInt("ItemCost.Amount." + eggType.getFriendlyName(), 0);
                
                //GTN was ItemStack itemStack = new ItemStack(itemId, itemAmount, (short)itemData);
                //GTN replaced with
                ItemStack itemStack = new ItemStack(Material.getMaterial(itemId), itemAmount, (short)itemData);
//GTN the problem is that Vault has already taken money but what happens if there are not enough items?
                if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    player.sendMessage(String.format(this.config.getString("Messages.ItemCostFail"), String.valueOf(itemAmount)));
                    if (this.looseEggOnFail) return;
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                    EggCatcher.eggs.add(egg);
                    return;
                }
//GTN Move this to take payment
                playerHasItems = true;
                //player.sendMessage(String.format(this.config.getString("Messages.ItemCostSuccess"), String.valueOf(itemAmount)));
                //player.getInventory().removeItem(new ItemStack[]{itemStack});
            }
//GTN **** TAKE PAYMENT HERE
            if (!freeCatch) {
            	//vaultCost only
            	if (this.useVaultCost && playerHasFunds && !this.useItemCost) {
            		EggCatcher.economy.withdrawPlayer(player, vaultCost);
            		if (!this.vaultTargetBankAccount.isEmpty()) {
            			EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
            		}
            		player.sendMessage(String.format(this.config.getString("Messages.VaultSuccess"), vaultCost));
            	}
            	//itemCost only
            	if (this.useItemCost && playerHasItems && !this.useVaultCost) {
            		String itemId = this.config.getString("ItemCost.ItemId");
            		int itemData = this.config.getInt("ItemCost.ItemData", 0);
            		int itemAmount = this.config.getInt("ItemCost.Amount." + eggType.getFriendlyName(), 0);
            		ItemStack itemStack = new ItemStack(Material.getMaterial(itemId), itemAmount, (short)itemData);
            		player.sendMessage(String.format(this.config.getString("Messages.ItemCostSuccess"), String.valueOf(itemAmount)));
                	player.getInventory().removeItem(new ItemStack[]{itemStack});
            	}
            	//Both vaultCost and itemCost
            	if (this.useVaultCost && playerHasFunds && this.useItemCost && playerHasItems) {
            		EggCatcher.economy.withdrawPlayer(player, vaultCost);
            		if (!this.vaultTargetBankAccount.isEmpty()) {
            			EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
            		}
            		player.sendMessage(String.format(this.config.getString("Messages.VaultSuccess"), vaultCost));
            		String itemId = this.config.getString("ItemCost.ItemId");
            		int itemData = this.config.getInt("ItemCost.ItemData", 0);
            		int itemAmount = this.config.getInt("ItemCost.Amount." + eggType.getFriendlyName(), 0);
            		ItemStack itemStack = new ItemStack(Material.getMaterial(itemId), itemAmount, (short)itemData);
            		player.sendMessage(String.format(this.config.getString("Messages.ItemCostSuccess"), String.valueOf(itemAmount)));
                	player.getInventory().removeItem(new ItemStack[]{itemStack});
            	}
            }
            
//GTN *** END OF TAKE PAYMENT
        } else {
            if (!this.nonPlayerCatching) {
                return;
            }
            if (this.useCatchChance) {
                double catchChance = this.config.getDouble("CatchChance." + eggType.getFriendlyName());
                if (Math.random() * 100.0 > catchChance) {
                    return;
                }
            }
        }
        entity.remove();
        if (this.explosionEffect) {
            entity.getWorld().createExplosion(entity.getLocation(), 0.0f);
        }
        if (this.smokeEffect) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
        }
        ItemStack eggStack = new ItemStack(Material.MONSTER_EGG, 1, eggType.getCreatureId());
        //GTN was eggStack = NbtReflection.setNewEntityTag(eggStack, entity.getType().name());
        //GTN replaced with
        eggStack = NbtReflection.setNewEntityTag(eggStack, entity.getType().getName());
        String customName = ((LivingEntity)entity).getCustomName();
        if (customName != null) {
            ItemMeta meta = eggStack.getItemMeta();
            meta.setDisplayName(customName);
            eggStack.setItemMeta(meta);
        }
        if (entity instanceof Pig && ((Pig)entity).hasSaddle()) {
            entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
        }
        if (entity instanceof Horse && ((Horse)entity).isCarryingChest()) {
            entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CHEST));
        }
        if (entity instanceof Villager && !this.deleteVillagerInventoryOnCatch || !(entity instanceof Villager) && entity instanceof InventoryHolder) {
            ItemStack[] items;
            for (ItemStack itemStack : items = ((InventoryHolder)entity).getInventory().getContents()) {
                if (itemStack == null) continue;
                entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
            }
        }
        entity.getWorld().dropItem(entity.getLocation(), eggStack);
        if (this.spawnChickenOnSuccess) return;
        if (EggCatcher.eggs.contains((Object)egg)) return;
        EggCatcher.eggs.add(egg);
    }
}

