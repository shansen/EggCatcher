/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.inventory.ItemStack
 */
package me.shansen.nbt;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class NbtReflection {
    public static ItemStack setNewEntityTag(ItemStack itemStack, String entityType) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            if (version.contains("1_7") || version.contains("1_8")) {
                return itemStack;
            }
            Class craftItemStack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            Object nmsItemStack = craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(craftItemStack, new Object[]{itemStack});
            Object rootTag = Class.forName("net.minecraft.server." + version + ".NBTTagCompound").newInstance();
            Object nbtEntityTag = Class.forName("net.minecraft.server." + version + ".NBTTagCompound").newInstance();
            nbtEntityTag.getClass().getMethod("setString", String.class, String.class).invoke(nbtEntityTag, "id", entityType);
            rootTag.getClass().getMethod("set", String.class, rootTag.getClass().getSuperclass()).invoke(rootTag, "EntityTag", nbtEntityTag);
            nmsItemStack.getClass().getMethod("setTag", rootTag.getClass()).invoke(nmsItemStack, rootTag);
            return (ItemStack)craftItemStack.getMethod("asBukkitCopy", nmsItemStack.getClass()).invoke(craftItemStack, nmsItemStack);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

