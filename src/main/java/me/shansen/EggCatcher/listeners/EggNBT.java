package me.shansen.EggCatcher.listeners;

import me.shansen.EggCatcher.EggType;
import net.minecraft.server.v1_10_R1.NBTBase;
import net.minecraft.server.v1_10_R1.NBTTagCompound;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Phillip from Mobcatcher
 *
 */
public class EggNBT {
    public static EntityType getSpawnEggEntityType(ItemStack item) {
        net.minecraft.server.v1_10_R1.ItemStack stack = CraftItemStack.asNMSCopy((ItemStack)item);
        NBTTagCompound tag = stack.getTag().getCompound("EntityTag");
        String entityString = tag.getString("id");
        char[] chars = entityString.toCharArray();
        int i = 0;
        while (i <= chars.length - 1) {
            if (Character.isUpperCase(chars[i]) && i != 0) {
                entityString = new StringBuilder(entityString).insert(i, "_").toString();
                break;
            }
            ++i;
        }
        entityString = entityString.toUpperCase();
        if (entityString.equalsIgnoreCase("OZELOT")) {
            entityString = "OCELOT";
        } else if (entityString.equalsIgnoreCase("LAVA_SLIME")) {
            entityString = "MAGMA_CUBE";
        } else if (entityString.equalsIgnoreCase("ENTITY_HORSE")) {
            entityString = "HORSE";
        } else if (entityString.equalsIgnoreCase("VILLAGER_GOLEM")) {
            entityString = "IRON_GOLEM";
        }
        EntityType type = EntityType.valueOf((String)entityString);
        return type;
    }
    
    public static ItemStack setSpawnEggEntityType(ItemStack item, EggType eggMob) {
        ItemStack newItem = new ItemStack(Material.MONSTER_EGG, 1);
        net.minecraft.server.v1_10_R1.ItemStack stack = CraftItemStack.asNMSCopy((ItemStack)newItem);
        NBTTagCompound tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagCompound id = new NBTTagCompound();
        id.setString("id", eggMob.getCreatureType().getName());
        tagCompound.set("EntityTag", (NBTBase)id);
        stack.setTag(tagCompound);
        ItemStack bukkitStack = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_10_R1.ItemStack)stack);
        bukkitStack.setDurability(item.getDurability());
        return bukkitStack;
    }
}

