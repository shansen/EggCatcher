package com.minecraftheads.EggCatcher.listeners;

import com.minecraftheads.EggCatcher.EggCatcher;
import com.minecraftheads.EggCatcher.EggCatcherLogger;
import com.minecraftheads.EggCatcher.EggType;
import com.minecraftheads.EggCatcher.events.EggCaptureEvent;
import java.io.File;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcherEntityListener implements Listener {
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
    private final boolean logCaptures;
    FileConfiguration config;
    JavaPlugin plugin;
    private final File captureLogFile;
    private final EggCatcherLogger captureLogger;

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
        this.logCaptures = this.config.getBoolean("LogEggCaptures", false);
        this.captureLogFile = new File(plugin.getDataFolder(), "captures.txt");
        this.captureLogger = new EggCatcherLogger(this.captureLogFile);
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onEntityHitByEgg(EntityDamageEvent event) {
        EntityDamageByEntityEvent damageEvent = null;
        Egg egg = null;
        EggType eggType = null;
        double vaultCost = 0.0D;
        Entity entity = event.getEntity();
        if (event instanceof EntityDamageByEntityEvent) {
            damageEvent = (EntityDamageByEntityEvent)event;
            if (damageEvent.getDamager() instanceof Egg) {
                egg = (Egg)damageEvent.getDamager();
                eggType = EggType.getEggType(entity);
                if (eggType != null) {
                    if (!this.spawnChickenOnFail) {
                        EggCatcher.eggs.add(egg);
                    }

                    if (!this.preventCatchingBabyAnimals || !(entity instanceof Ageable) || ((Ageable)entity).isAdult()) {
                        if (!this.preventCatchingTamedAnimals || !(entity instanceof Tameable) || !((Tameable)entity).isTamed()) {
                            if (!this.preventCatchingShearedSheeps || !(entity instanceof Sheep) || !((Sheep)entity).isSheared()) {
                                EggCaptureEvent eggCaptureEvent = new EggCaptureEvent(entity, egg);
                                this.plugin.getServer().getPluginManager().callEvent(eggCaptureEvent);
                                if (!eggCaptureEvent.isCancelled()) {
                                    if (egg.getShooter() instanceof Player) {
                                        Player player = (Player)egg.getShooter();
                                        if (!this.playerHasRequirements(entity, player, eggType.getFriendlyName())) {
                                            if (!this.looseEggOnFail) {
                                                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                                                EggCatcher.eggs.add(egg);
                                            }

                                            return;
                                        }

                                        vaultCost = this.config.getDouble("Entity." + eggType.getFriendlyName() + ".VaultCost");
                                        EggCatcher.economy.withdrawPlayer(player, vaultCost);
                                        if (!this.vaultTargetBankAccount.isEmpty()) {
                                            EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                                        }

                                        player.sendMessage(String.format(this.config.getString("Messages.VaultSuccess"), vaultCost));
                                        Material itemMaterial = Material.matchMaterial(this.config.getString("Entity." + eggType.getFriendlyName() + ".ItemCost.ItemName", "gold_nugget"));
                                        int itemAmount = this.config.getInt("Entity." + eggType.getFriendlyName() + ".ItemCost.Amount", 0);
                                        ItemStack itemStack = new ItemStack(itemMaterial, itemAmount);
                                        player.sendMessage(String.format(this.config.getString("Messages.ItemCostSuccess"), String.valueOf(itemAmount)));
                                        player.getInventory().removeItem(new ItemStack[]{itemStack});
                                    } else {
                                        if (!this.nonPlayerCatching) {
                                            return;
                                        }

                                        if (this.useCatchChance) {
                                            double catchChance = this.config.getDouble("Entity." + eggType.getFriendlyName() + ".CatchChance");
                                            if (Math.random() * 100.0D > catchChance) {
                                                return;
                                            }
                                        }
                                    }

                                    entity.remove();
                                    if (this.explosionEffect) {
                                        entity.getWorld().createExplosion(entity.getLocation(), 0.0F);
                                    }

                                    if (this.smokeEffect) {
                                        entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
                                    }

                                    ItemStack eggStack = new ItemStack(eggType.getMaterial(), 1);
                                    String customName = ((LivingEntity)entity).getCustomName();
                                    if (customName != null) {
                                        ItemMeta meta = eggStack.getItemMeta();
                                        meta.setDisplayName(customName);
                                        eggStack.setItemMeta(meta);
                                    }

                                    if (entity instanceof Pig && ((Pig)entity).hasSaddle()) {
                                        entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
                                    }

                                    if (entity instanceof ChestedHorse) {
                                        entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CHEST));
                                    }

                                    if (entity instanceof Villager && !this.deleteVillagerInventoryOnCatch || !(entity instanceof Villager) && entity instanceof InventoryHolder) {
                                        ItemStack[] items = ((InventoryHolder)entity).getInventory().getContents();
                                        ItemStack[] var21 = items;
                                        int var13 = items.length;

                                        for(int var14 = 0; var14 < var13; ++var14) {
                                            ItemStack itemStack = var21[var14];
                                            if (itemStack != null) {
                                                entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
                                            }
                                        }
                                    }

                                    entity.getWorld().dropItem(entity.getLocation(), eggStack);
                                    if (!this.spawnChickenOnSuccess && !EggCatcher.eggs.contains(egg)) {
                                        EggCatcher.eggs.add(egg);
                                    }

                                    if (this.logCaptures) {
                                        this.captureLogger.logToFile("Player " + ((Player)egg.getShooter()).getName() + " caught " + entity.getType() + " at X" + Math.round(entity.getLocation().getX()) + ",Y" + Math.round(entity.getLocation().getY()) + ",Z" + Math.round(entity.getLocation().getZ()));
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean playerHasRequirements(Entity entity, Player player, String eggtype) {
        boolean hasRequirements = true;
        double vaultCost = 0.0D;
        boolean freeCatch = player.hasPermission("eggcatcher.free");
        if (this.usePermissions && !player.hasPermission("eggcatcher.catch." + eggtype.toLowerCase())) {
            player.sendMessage(this.config.getString("Messages.PermissionFail"));
            return false;
        } else {
            double healthPercentage;
            if (this.useHealthPercentage) {
                healthPercentage = this.config.getDouble("Entity." + eggtype + ".HealthPercentage");
                double currentHealth = ((LivingEntity)entity).getHealth() * 100.0D / ((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                if (healthPercentage < currentHealth) {
                    if (this.healthPercentageFailMessage.length() > 0) {
                        player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                    }

                    hasRequirements = false;
                }
            }

            if (this.useCatchChance) {
                healthPercentage = this.config.getDouble("Entity." + eggtype + ".CatchChance");
                if (Math.random() * 100.0D <= healthPercentage) {
                    if (this.catchChanceSuccessMessage.length() > 0) {
                        player.sendMessage(this.catchChanceSuccessMessage);
                    }
                } else {
                    if (this.catchChanceFailMessage.length() > 0) {
                        player.sendMessage(this.catchChanceFailMessage);
                    }

                    hasRequirements = false;
                }
            }

            if (this.useVaultCost && !freeCatch) {
                vaultCost = this.config.getDouble("Entity." + eggtype + ".VaultCost");
                if (!EggCatcher.economy.has(player, vaultCost)) {
                    player.sendMessage(String.format(this.config.getString("Messages.VaultFail"), vaultCost));
                    hasRequirements = false;
                }
            }

            if (this.useItemCost && !freeCatch) {
                Material itemMaterial = Material.matchMaterial(this.config.getString("Entity." + eggtype + ".ItemCost.ItemName", "gold_nugget"));
                int itemAmount = this.config.getInt("Entity." + eggtype + ".ItemCost.Amount", 0);
                ItemStack itemStack = new ItemStack(itemMaterial, itemAmount);
                if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    player.sendMessage(String.format(this.config.getString("Messages.ItemCostFail"), String.valueOf(itemAmount)));
                    hasRequirements = false;
                }
            }

            return hasRequirements;
        }
    }
}