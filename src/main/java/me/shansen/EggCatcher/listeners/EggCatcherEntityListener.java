/*
EggCatcher
Copyright (C) 2012  me@shansen.me

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

package me.shansen.EggCatcher.listeners;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggType;
import me.shansen.EggCatcher.events.EggCaptureEvent;
import org.bukkit.Effect;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcherEntityListener implements Listener {

    private final Boolean usePermissions;
    private final Boolean useCatchChance;
    private final Boolean useHealthPercentage;
    private final Boolean looseEggOnFail;
    private final Boolean useVaultCost;
    private final Boolean useItemCost;
    private final Boolean explosionEffect;
    private final Boolean smokeEffect;
    private final Boolean nonPlayerCatching;
    private final Boolean preventCatchingBabyAnimals;
    private final Boolean preventCatchingTamableAnimals;
    private final Boolean preventCatchingShearedSheeps;
    private final String catchChanceSuccessMessage;
    private final String catchChanceFailMessage;
    private final String healthPercentageFailMessage;
    private final String vaultTargetBankAccount;
    private final Boolean spawnChickenOnFail;
    private final Boolean spawnChickenOnSuccess;
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
        this.preventCatchingTamableAnimals = this.config.getBoolean("PreventCatchingTamableAnimals", true);
        this.preventCatchingShearedSheeps = this.config.getBoolean("PreventCatchingShearedSheeps", true);
        this.spawnChickenOnFail = this.config.getBoolean("SpawnChickenOnFail", true);
        this.spawnChickenOnSuccess = this.config.getBoolean("SpawnChickenOnSuccess", false);
        this.vaultTargetBankAccount = this.config.getString("VaultTargetBankAccount", "");
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
            if (entity instanceof Animals) {
                if (!((Animals) entity).isAdult()) {
                    return;
                }
            }
        }

        if (this.preventCatchingTamableAnimals) {
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

            if (this.usePermissions) {
                if (!player.hasPermission("eggcatcher.catch." + eggType.getFriendlyName().toLowerCase())) {
                    player.sendMessage(config.getString("Messages.PermissionFail"));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(344, 1));
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
                        player.getInventory().addItem(new ItemStack(344, 1));
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
                        player.getInventory().addItem(new ItemStack(344, 1));
                        EggCatcher.eggs.add(egg);
                    }
                    return;
                }
            }

            if (this.useVaultCost) {
                vaultCost = config.getDouble("VaultCost." + eggType.getFriendlyName());
                if (!EggCatcher.economy.has(player.getName(), vaultCost)) {
                    player.sendMessage(String.format(config.getString("Messages.VaultFail"), vaultCost));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(344, 1));
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

            if (this.useItemCost) {
                int itemId = config.getInt("ItemCost.ItemId", 266);
                int itemAmount = config.getInt("ItemCost.Amount." + eggType.getFriendlyName(), 0);
                ItemStack itemStack = new ItemStack(itemId, itemAmount);
                if (player.getInventory().contains(itemId, itemAmount)) {
                    player.sendMessage(String.format(config.getString("Messages.ItemCostSuccess"),
                            String.valueOf(itemAmount)));
                    player.getInventory().removeItem(itemStack);
                } else {
                    player.sendMessage(String.format(config.getString("Messages.ItemCostFail"),
                            String.valueOf(itemAmount)));
                    if (!this.looseEggOnFail) {
                        player.getInventory().addItem(new ItemStack(344, 1));
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

        entity.remove();
        if (this.explosionEffect) {
            entity.getWorld().createExplosion(entity.getLocation(), 0);
        }
        if (this.smokeEffect) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
        }

        ItemStack eggStack = new ItemStack(383, 1, eggType.getCreatureId());
        String customName = ((LivingEntity) entity).getCustomName();

        if (customName != null) {
            // Entity had custom name
            ItemMeta meta = eggStack.getItemMeta();
            meta.setDisplayName(customName);
            eggStack.setItemMeta(meta);
        }

        entity.getWorld().dropItem(entity.getLocation(), eggStack);

        if (!this.spawnChickenOnSuccess) {
            if (!EggCatcher.eggs.contains(egg)) {
                EggCatcher.eggs.add(egg);
            }
        }
    }
}
