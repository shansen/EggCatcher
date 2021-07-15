/*
EggCatcher
Copyright (C) 2012, 2013  me@shansen.me

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.minecraftheads.EggCatcher.listeners;

import com.minecraftheads.EggCatcher.EggCatcherLogger;
import com.minecraftheads.EggCatcher.EggType;
import com.minecraftheads.EggCatcher.EggCatcher;
import com.minecraftheads.EggCatcher.events.EggCaptureEvent;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.attribute.Attribute;

import java.io.File;

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
		this.captureLogger = new EggCatcherLogger(captureLogFile);
	}

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

        if (!(damageEvent.getDamager() instanceof Egg)) {
            return;
        }

        egg = (Egg) damageEvent.getDamager();
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
            Player player = (Player) egg.getShooter();

            // check if player meets all requirements
            if (!playerHasRequirements(entity, player, egg, eggType.getFriendlyName())) {
                if (!this.looseEggOnFail) {
                    player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                    EggCatcher.eggs.add(egg);
                }
                return;
            }

            // Withdraw money
            vaultCost = config.getDouble("Entity." + eggType.getFriendlyName() + ".VaultCost");
            EggCatcher.economy.withdrawPlayer(player, vaultCost);
            if (!this.vaultTargetBankAccount.isEmpty()) {
                EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
            }
            player.sendMessage(String.format(config.getString("Messages.VaultSuccess"), vaultCost));

            // Take Items
            Material itemMaterial = Material.matchMaterial(config.getString("Entity." + eggType.getFriendlyName() + ".ItemCost.ItemName", "gold_nugget"));
            int itemAmount = config.getInt("Entity." + eggType.getFriendlyName() + ".ItemCost.Amount", 0);
            ItemStack itemStack = new ItemStack(itemMaterial, itemAmount);
            player.sendMessage(String.format(config.getString("Messages.ItemCostSuccess"),
                    String.valueOf(itemAmount)));
            player.getInventory().removeItem(itemStack);

        } else {
            // Dispenser
            if (!this.nonPlayerCatching) {
                return;
            }
            if (this.useCatchChance) {
                double catchChance = config.getDouble("Entity." + eggType.getFriendlyName() + ".CatchChance");
                if (Math.random() * 100 > catchChance) {
                    return;
                }
            }
        }

        entity.remove();
        if (this.explosionEffect) {
            entity.getWorld().createExplosion(entity.getLocation(), 0);
        }
        if (this.smokeEffect) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
        }

        ItemStack eggStack = new ItemStack(eggType.getMaterial(), 1);

        String customName = ((LivingEntity) entity).getCustomName();

        if (customName != null) {
            // Entity had custom name
            ItemMeta meta = eggStack.getItemMeta();
            meta.setDisplayName(customName);
            eggStack.setItemMeta(meta);
        }

        if(entity instanceof Pig) {
            if(((Pig)entity).hasSaddle()) {
                entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
            }
        }

        if(entity instanceof Horse) {
            if(((Horse) entity).isCarryingChest()){
                entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CHEST));
            }
        }

        if((entity instanceof Villager && !this.deleteVillagerInventoryOnCatch) ||
                (!(entity instanceof Villager) && entity instanceof InventoryHolder)) {

            ItemStack[] items = ((InventoryHolder) entity).getInventory().getContents();

            for(ItemStack itemStack : items) {
                if(itemStack!=null){
                    entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
                }
            }
        }

        entity.getWorld().dropItem(entity.getLocation(), eggStack);

        if (!this.spawnChickenOnSuccess) {
            if (!EggCatcher.eggs.contains(egg)) {
                EggCatcher.eggs.add(egg);
            }
        }
        
        if (this.logCaptures){
			captureLogger.logToFile("Player " + ((Player) egg.getShooter()).getName() + " caught " + entity.getType() + " at X" + Math.round(entity.getLocation().getX()) + ",Y" + Math.round(entity.getLocation().getY()) + ",Z" + Math.round(entity.getLocation().getZ()));
        }
    }

    private boolean playerHasRequirements(Entity entity, Player player, Egg egg, String eggtype) {
        boolean hasRequirements = true;
        double vaultCost = 0.0;
        boolean freeCatch = player.hasPermission("eggcatcher.free");

        if (this.usePermissions) {
            if (!player.hasPermission("eggcatcher.catch." + eggtype.toLowerCase())) {
                player.sendMessage(config.getString("Messages.PermissionFail"));

                hasRequirements = false;
            }
        }
        if (this.useHealthPercentage) {
            double healthPercentage = config.getDouble("Entity." + eggtype + ".HealthPercentage");
            double currentHealth = ((LivingEntity) entity).getHealth() * 100.0 / ((LivingEntity) entity)
                    .getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (healthPercentage < currentHealth) {
                if (this.healthPercentageFailMessage.length() > 0) {
                    player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                }

                hasRequirements = false;
            }
        }

        if (this.useCatchChance) {
            double catchChance = config.getDouble("Entity." + eggtype + ".CatchChance");
            if (Math.random() * 100 <= catchChance) {
                if (this.catchChanceSuccessMessage.length() > 0) {
                    player.sendMessage(catchChanceSuccessMessage);
                }
            } else {
                if (this.catchChanceFailMessage.length() > 0) {
                    player.sendMessage(this.catchChanceFailMessage);
                }

                hasRequirements = false;
            }
        }

        if (this.useVaultCost && !freeCatch) {
            vaultCost = config.getDouble("Entity." + eggtype + ".VaultCost");
            if (!EggCatcher.economy.has(player, vaultCost)) {
                player.sendMessage(String.format(config.getString("Messages.VaultFail"), vaultCost));

                hasRequirements = false;
            }
        }

        if (this.useItemCost && !freeCatch) {
            Material itemMaterial = Material.matchMaterial(config.getString("Entity." + eggtype + ".ItemCost.ItemName", "gold_nugget"));
            int itemAmount = config.getInt("Entity." + eggtype + ".ItemCost.Amount", 0);
            ItemStack itemStack = new ItemStack(itemMaterial, itemAmount);
            if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                player.sendMessage(String.format(config.getString("Messages.ItemCostFail"),
                        String.valueOf(itemAmount)));

                hasRequirements = false;
            }
        }

        return hasRequirements;
    }
}
