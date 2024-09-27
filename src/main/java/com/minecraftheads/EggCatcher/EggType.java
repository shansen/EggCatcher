package com.minecraftheads.EggCatcher;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum EggType {
    DROWNED(EntityType.DROWNED, "Drowned", Material.DROWNED_SPAWN_EGG),
    EVOKER(EntityType.EVOKER, "Evoker", Material.EVOKER_SPAWN_EGG),
    VEX(EntityType.VEX, "Vex", Material.VEX_SPAWN_EGG),
    VINDICATOR(EntityType.VINDICATOR, "Vindicator", Material.VINDICATOR_SPAWN_EGG),
    PIG_ZOMBIE(EntityType.ZOMBIFIED_PIGLIN, "PigZombie", Material.ZOMBIFIED_PIGLIN_SPAWN_EGG),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, "MagmaCube", Material.MAGMA_CUBE_SPAWN_EGG),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, "CaveSpider", Material.CAVE_SPIDER_SPAWN_EGG),
    MOOSHROOM(EntityType.MOOSHROOM, "Mooshroom", Material.MOOSHROOM_SPAWN_EGG),
    CREEPER(EntityType.CREEPER, "Creeper", Material.CREEPER_SPAWN_EGG),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, "WitherSkeleton", Material.WITHER_SKELETON_SPAWN_EGG),
    STRAY(EntityType.STRAY, "Stray", Material.STRAY_SPAWN_EGG),
    SKELETON(EntityType.SKELETON, "Skeleton", Material.SKELETON_SPAWN_EGG),
    SPIDER(EntityType.SPIDER, "Spider", Material.SPIDER_SPAWN_EGG),
    HUSK(EntityType.HUSK, "Husk", Material.HUSK_SPAWN_EGG),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, "ZombieVillager", Material.ZOMBIE_VILLAGER_SPAWN_EGG),
    ZOMBIE(EntityType.ZOMBIE, "Zombie", Material.ZOMBIE_SPAWN_EGG),
    SLIME(EntityType.SLIME, "Slime", Material.SLIME_SPAWN_EGG),
    GHAST(EntityType.GHAST, "Ghast", Material.GHAST_SPAWN_EGG),
    ENDERMAN(EntityType.ENDERMAN, "Enderman", Material.ENDERMAN_SPAWN_EGG),
    SILVERFISH(EntityType.SILVERFISH, "Silverfish", Material.SILVERFISH_SPAWN_EGG),
    BLAZE(EntityType.BLAZE, "Blaze", Material.BLAZE_SPAWN_EGG),
    PIG(EntityType.PIG, "Pig", Material.PIG_SPAWN_EGG),
    SHEEP(EntityType.SHEEP, "Sheep", Material.SHEEP_SPAWN_EGG),
    COW(EntityType.COW, "Cow", Material.COW_SPAWN_EGG),
    CHICKEN(EntityType.CHICKEN, "Chicken", Material.CHICKEN_SPAWN_EGG),
    GLOW_SQUID(EntityType.GLOW_SQUID, "GlowSquid", Material.GLOW_SQUID_SPAWN_EGG),
    SQUID(EntityType.SQUID, "Squid", Material.SQUID_SPAWN_EGG),
    WOLF(EntityType.WOLF, "Wolf", Material.WOLF_SPAWN_EGG),
    VILLAGER(EntityType.VILLAGER, "Villager", Material.VILLAGER_SPAWN_EGG),
    OCELOT(EntityType.OCELOT, "Ocelot", Material.OCELOT_SPAWN_EGG),
    BAT(EntityType.BAT, "Bat", Material.BAT_SPAWN_EGG),
    WITCH(EntityType.WITCH, "Witch", Material.WITCH_SPAWN_EGG),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, "ZombieHorse", Material.ZOMBIE_HORSE_SPAWN_EGG),
    SKELETON_HORSE(EntityType.SKELETON_HORSE, "SkeletonHorse", Material.SKELETON_HORSE_SPAWN_EGG),
    TRADER_LLAMA(EntityType.TRADER_LLAMA, "TraderLlama", Material.TRADER_LLAMA_SPAWN_EGG),
    LLAMA(EntityType.LLAMA, "Llama", Material.LLAMA_SPAWN_EGG),
    DONKEY(EntityType.DONKEY, "Donkey", Material.DONKEY_SPAWN_EGG),
    MULE(EntityType.MULE, "Mule", Material.MULE_SPAWN_EGG),
    HORSE(EntityType.HORSE, "Horse", Material.HORSE_SPAWN_EGG),
    ENDERMITE(EntityType.ENDERMITE, "Endermite", Material.ENDERMITE_SPAWN_EGG),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, "ElderGuardian", Material.ELDER_GUARDIAN_SPAWN_EGG),
    GUARDIAN(EntityType.GUARDIAN, "Guardian", Material.GUARDIAN_SPAWN_EGG),
    RABBIT(EntityType.RABBIT, "Rabbit", Material.RABBIT_SPAWN_EGG),
    POLAR_BEAR(EntityType.POLAR_BEAR, "PolarBear", Material.POLAR_BEAR_SPAWN_EGG),
    SHULKER(EntityType.SHULKER, "Shulker", Material.SHULKER_SPAWN_EGG),
    PARROT(EntityType.PARROT, "Parrot", Material.PARROT_SPAWN_EGG),
    BEE(EntityType.BEE, "Bee", Material.BEE_SPAWN_EGG),
    CAT(EntityType.CAT, "Cat", Material.CAT_SPAWN_EGG),
    COD(EntityType.COD, "Cod", Material.COD_SPAWN_EGG),
    DOLPHIN(EntityType.DOLPHIN, "Dolphin", Material.DOLPHIN_SPAWN_EGG),
    FOX(EntityType.FOX, "Fox", Material.FOX_SPAWN_EGG),
    PANDA(EntityType.PANDA, "Panda", Material.PANDA_SPAWN_EGG),
    PHANTOM(EntityType.PHANTOM, "Phantom", Material.PHANTOM_SPAWN_EGG),
    PILLAGER(EntityType.PILLAGER, "Pillager", Material.PILLAGER_SPAWN_EGG),
    PUFFERFISH(EntityType.PUFFERFISH, "Pufferfish", Material.PUFFERFISH_SPAWN_EGG),
    RAVAGER(EntityType.RAVAGER, "Ravager", Material.RAVAGER_SPAWN_EGG),
    SALMON(EntityType.SALMON, "Salmon", Material.SALMON_SPAWN_EGG),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, "TropicalFish", Material.TROPICAL_FISH_SPAWN_EGG),
    TURTLE(EntityType.TURTLE, "Turtle", Material.TURTLE_SPAWN_EGG),
    HOGLIN(EntityType.HOGLIN, "Hoglin", Material.HOGLIN_SPAWN_EGG),
    PIGLIN(EntityType.PIGLIN, "Piglin", Material.PIGLIN_SPAWN_EGG),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, "PiglinBrute", Material.PIGLIN_BRUTE_SPAWN_EGG),
    STRIDER(EntityType.STRIDER, "Strider", Material.STRIDER_SPAWN_EGG),
    ZOGLIN(EntityType.ZOGLIN, "Zoglin", Material.ZOGLIN_SPAWN_EGG),
    WANDERING_TRADER(EntityType.WANDERING_TRADER, "WanderingTrader", Material.WANDERING_TRADER_SPAWN_EGG),
    AXOLOTL(EntityType.AXOLOTL, "Axolotl", Material.AXOLOTL_SPAWN_EGG),
    GOAT(EntityType.GOAT, "Goat", Material.GOAT_SPAWN_EGG),
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, "ZombifiedPiglin", Material.ZOMBIFIED_PIGLIN_SPAWN_EGG),
    ALLAY(EntityType.ALLAY, "Allay", Material.ALLAY_SPAWN_EGG),
    FROG(EntityType.FROG, "Frog", Material.FROG_SPAWN_EGG),
    TADPOLE(EntityType.TADPOLE, "Tadpole", Material.TADPOLE_SPAWN_EGG),
    WARDEN(EntityType.WARDEN, "Warden", Material.WARDEN_SPAWN_EGG),
    CAMEL(EntityType.CAMEL, "Camel", Material.CAMEL_SPAWN_EGG),
    SNIFFER(EntityType.SNIFFER, "Sniffer", Material.SNIFFER_SPAWN_EGG),
    ARMADILLO(EntityType.ARMADILLO, "Armadillo", Material.ARMADILLO_SPAWN_EGG);

    private final EntityType entityType;
    private final String friendlyName;
    private final Material material;

    private EggType(EntityType entityType, String friendlyName, Material material) {
        this.entityType = entityType;
        this.friendlyName = friendlyName;
        this.material = material;
    }

    public EntityType getCreatureType() {
        return this.entityType;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public static EggType getEggType(Entity entity) {
        EggType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EggType eggType = var1[var3];
            if (eggType.getCreatureType().getEntityClass().isInstance(entity)) {
                return eggType;
            }
        }

        return null;
    }
}
