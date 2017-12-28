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

package me.shansen.EggCatcher.listeners;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggCatcherLogger;
import me.shansen.EggCatcher.EggType;
import me.shansen.EggCatcher.events.EggCaptureEvent;

import me.shansen.nbt.NbtReflection;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
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

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class EggCatcherEntityListener implements Listener {

    private final boolean usePermissions;
    private final boolean useCatchChance;
    private final boolean useHealthPercentage;
    private final boolean loseEggOnFail;
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
        this.loseEggOnFail = this.config.getBoolean("LooseEggOnFail", true);
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
        final Entity entity = event.getEntity();

        Optional.of(event)
                // only capture entity damage by other entities, don't care about block damage
                .filter(EntityDamageByEntityEvent.class::isInstance)
                .map(EntityDamageByEntityEvent.class::cast)

                // egg catcher event restrictions
                .filter(this::checkIsEggEvent)
                .filter(this::checkAnimalRestrictions)
                .map(entityEvent -> new EggCaptureEvent(entity, (Egg) entityEvent.getDamager()))
                .ifPresent(eggEvent -> this.plugin.getServer().getPluginManager().callEvent(eggEvent));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEggCaptureEvent(EggCaptureEvent event) {
        Egg egg = event.getEgg();
        Entity entity = event.getEntity();
        EggType eggType = EggType.getEggType(entity);

        if (!this.spawnChickenOnFail) {
            EggCatcher.eggs.add(egg);
        }

        if (egg.getShooter() instanceof Player) {
            Player player = (Player) egg.getShooter();
            double vaultCost = getVaultCost(player, Objects.requireNonNull(eggType).getFriendlyName());
            ItemStack itemCost = getItemCost(player, eggType.getFriendlyName());

            // check that the player can actually catch this mob
            if (!playerHasRequirements(event, player, vaultCost, itemCost)) {
                return;
            }

            // send chance success message
            if (this.useCatchChance && this.catchChanceSuccessMessage.length() != 0) {
                player.sendMessage(this.catchChanceSuccessMessage);
            }

            // withdraw money
            if (vaultCost > 0) {
                EggCatcher.economy.withdrawPlayer(player, vaultCost);

                if (!this.vaultTargetBankAccount.isEmpty()) {
                    EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                }

                player.sendMessage(String.format(config.getString("Messages.VaultSuccess"), vaultCost));
            }

            // withdraw items
            if (itemCost != null) {
                player.sendMessage(String.format(config.getString("Messages.ItemCostSuccess"),
                        String.valueOf(itemCost.getAmount())));
                player.getInventory().removeItem(itemCost);
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

        ItemStack eggStack = new ItemStack(Material.MONSTER_EGG, 1, eggType.getCreatureId());
        eggStack = NbtReflection.setNewEntityTag(eggStack, entity.getType().getName());
        String customName = entity.getCustomName();

        if (customName != null) {
            // Entity had custom name
            ItemMeta meta = eggStack.getItemMeta();
            meta.setDisplayName(customName);
            eggStack.setItemMeta(meta);
        }

        if (entity instanceof Pig && ((Pig) entity).hasSaddle()) {
            entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
        } else if (entity instanceof ChestedHorse && ((ChestedHorse) entity).isCarryingChest()) {
            entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CHEST));
        } else if ((entity instanceof Villager && !this.deleteVillagerInventoryOnCatch) ||
                (!(entity instanceof Villager) && entity instanceof InventoryHolder)) {

            ItemStack[] items = ((InventoryHolder) entity).getInventory().getContents();

            for (ItemStack itemStack : items) {
                if (itemStack != null) {
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

        if (this.logCaptures) {
            captureLogger.logToFile(String.format("Player %s caught %s at X%d,Y%d,Z%d",
                    ((Player) egg.getShooter()).getName(),
                    entity.getType().toString(),
                    Math.round(entity.getLocation().getX()),
                    Math.round(entity.getLocation().getY()),
                    Math.round(entity.getLocation().getZ())));
        }
    }

    /**
     * Check that the player satisfies requirements necessary to capture mob
     *
     * @param event     egg capture event
     * @param player    player causing event
     * @param vaultCost economy cost to capture mob
     * @param itemCost  item cost to capture mob
     * @return if player should catch the mob
     */
    private boolean playerHasRequirements(EggCaptureEvent event, Player player, double vaultCost, ItemStack itemCost) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        String eggType = Objects.requireNonNull(EggType.getEggType(event.getEntity())).getFriendlyName();
        double entityHealthRequirement = getEntityHealthRequirement(eggType);

        String message = null;
        boolean hasRequirements = true;

        // perform player egg capture validations
        if (this.usePermissions && !player.hasPermission("eggcatcher.catch." + eggType.toLowerCase())) {
            hasRequirements = false;
            message = config.getString("Messages.PermissionFail");
        } else if (entityHealthRequirement > 0 && entityHealthRequirement < getEntityHealthPercentage(entity)) {
            hasRequirements = false;
            if (this.healthPercentageFailMessage.length() > 0) {
                message = String.format(this.healthPercentageFailMessage, entityHealthRequirement);
            }
        } else if (this.useCatchChance && Math.random() * 100 > config.getDouble("CatchChance." + eggType)) {
            hasRequirements = false;
            message = this.catchChanceFailMessage;
        } else if (vaultCost > 0 && !EggCatcher.economy.has(player, vaultCost)) {
            hasRequirements = false;
            message = String.format(config.getString("Messages.VaultFail"), vaultCost);
        } else if (itemCost != null && !player.getInventory().containsAtLeast(itemCost, itemCost.getAmount())) {
            hasRequirements = false;
            message = String.format(config.getString("Messages.ItemCostFail"), String.valueOf(itemCost.getAmount()));
        }

        if (message != null && message.length() != 0) {
            player.sendMessage(message);
        }

        if (!hasRequirements) {
            if (!this.loseEggOnFail) {
                player.getInventory().addItem(new ItemStack(Material.EGG, 1));
                EggCatcher.eggs.add(event.getEgg());
            }
        }

        return hasRequirements;
    }

    /**
     * Get the vault (economy) cost of capturing the given egg mob
     *
     * @param p       player attempting to capture mob
     * @param eggType friendly name of the mob egg being caught
     * @return vault cost of capturing the mob egg
     */
    private double getVaultCost(Player p, String eggType) {
        return this.useVaultCost && !p.hasPermission("eggcatcher.free") ? config.getDouble("VaultCost." + eggType) : 0;
    }

    /**
     * Get the item cost of catching a mob egg
     *
     * @param p       player attempting to capture mob
     * @param eggType friendly name of the mob egg being caught
     * @return item cost of capturing the mob egg
     */
    private ItemStack getItemCost(Player p, String eggType) {
        if (this.useItemCost && !p.hasPermission("eggcatcher.free")) {
            int itemId = config.getInt("ItemCost.ItemId", 266);
            int itemData = config.getInt("ItemCost.ItemData", 0);
            int itemAmount = config.getInt("ItemCost.Amount." + eggType, 0);
            return new ItemStack(itemId, itemAmount, (short) itemData);
        }
        return null;
    }

    /**
     * Get the required health percentage to catch given mob egg
     *
     * @param eggType friendly name of mob egg type being caught
     * @return required health percentage to catch given mob egg
     */
    private double getEntityHealthRequirement(String eggType) {
        return this.useHealthPercentage ? config.getDouble("HealthPercentage." + eggType) : 0;
    }

    /**
     * Get the current percentage of max health an entity currently has
     *
     * @param entity Entity to get health percentage of
     * @return current percentage of max health an entity currently has
     */
    private double getEntityHealthPercentage(LivingEntity entity) {
        return entity.getHealth() * 100.0 / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }

    /**
     * Check that the EntityDamageByEntityEvent is damage done by a valid egg to a valid egg-able mob
     *
     * @param e EntityDamageByEntityEvent
     * @return if this event is a valid egg capture
     */
    private boolean checkIsEggEvent(EntityDamageByEntityEvent e) {
        return e.getDamager() instanceof Egg && EggType.getEggType(e.getEntity()) != null;
    }

    /**
     * Check that the entity being damaged satisfies the configured baby/tamed/sheared restrictions
     *
     * @param e entity damage by entity event
     * @return if the entity being damated satisfies the restrictions
     */
    private boolean checkAnimalRestrictions(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();

        return !(
                (this.preventCatchingBabyAnimals && entity instanceof Ageable && !((Ageable) entity).isAdult()) ||
                        (this.preventCatchingTamedAnimals && entity instanceof Tameable && ((Tameable) entity).isTamed()) ||
                        (this.preventCatchingShearedSheeps && entity instanceof Sheep && ((Sheep) entity).isSheared())
        );
    }
}
