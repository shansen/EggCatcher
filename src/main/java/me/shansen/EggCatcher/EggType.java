package me.shansen.EggCatcher;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum EggType {
    PIG_ZOMBIE(EntityType.PIG_ZOMBIE, 57, "PigZombie"),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 62, "MagmaCube"),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, 59, "CaveSpider"),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, 96, "MushroomCow"),
    CREEPER(EntityType.CREEPER, 50, "Creeper"),
    SKELETON(EntityType.SKELETON, 51, "Skeleton"),
    SPIDER(EntityType.SPIDER, 52, "Spider"),
    ZOMBIE(EntityType.ZOMBIE, 54, "Zombie"),
    SLIME(EntityType.SLIME, 55, "Slime"),
    GHAST(EntityType.GHAST, 56, "Ghast"),
    ENDERMAN(EntityType.ENDERMAN, 58, "Enderman"),
    SILVERFISH(EntityType.SILVERFISH, 60, "Silverfish"),
    BLAZE(EntityType.BLAZE, 61, "Blaze"),
    PIG(EntityType.PIG, 90, "Pig"),
    SHEEP(EntityType.SHEEP, 91, "Sheep"),
    COW(EntityType.COW, 92, "Cow"),
    CHICKEN(EntityType.CHICKEN, 93, "Chicken"),
    SQUID(EntityType.SQUID, 94, "Squid"),
    WOLF(EntityType.WOLF, 95, "Wolf"),
    VILLAGER(EntityType.VILLAGER, 120, "Villager"),
    OCELOT(EntityType.OCELOT, 98, "Ocelot"),
    BAT(EntityType.BAT, 65, "Bat"),
    WITCH(EntityType.WITCH, 66, "Witch"),
    HORSE(EntityType.HORSE, 100, "Horse"),
    ENDERMITE(EntityType.ENDERMITE, 67, "Endermite"),
    GUARDIAN(EntityType.GUARDIAN, 68, "Guardian"),
    RABBIT(EntityType.RABBIT, 101, "Rabbit"),
    POLAR_BEAR(EntityType.POLAR_BEAR, 102, "PolarBear"),
    IRON_GOLEM(EntityType.IRON_GOLEM, 99, "IronGolem");
    
    private final EntityType entityType;
    private final Integer creatureId;
    private final String friendlyName;

    private EggType(EntityType entityType, Integer creatureId, String friendlyName) {
        this.entityType = entityType;
        this.creatureId = creatureId;
        this.friendlyName = friendlyName;
    }

    public short getCreatureId() {
        return this.creatureId.shortValue();
    }

    public EntityType getCreatureType() {
        return this.entityType;
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public static EggType getEggType(Entity entity) {
        for (EggType eggType : EggType.values()) {
            if (!eggType.getCreatureType().getEntityClass().isInstance((Object)entity)) continue;
            return eggType;
        }
        return null;
    }
}

