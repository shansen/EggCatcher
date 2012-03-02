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

package me.shansen.EggCatcher;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public enum EggType {
	PIG_ZOMBIE		(EntityType.PIG_ZOMBIE, 57, "PigZombie"),
	MAGMA_CUBE		(EntityType.MAGMA_CUBE, 62, "MagmaCube"),
	CAVE_SPIDER		(EntityType.CAVE_SPIDER, 59, "CaveSpider"),
	MUSHROOM_COW	(EntityType.MUSHROOM_COW, 96, "MushroomCow"),
	CREEPER			(EntityType.CREEPER, 50, "Creeper"),
	SKELETON		(EntityType.SKELETON, 51, "Skeleton"),
	SPIDER			(EntityType.SPIDER, 52, "Spider"),
	ZOMBIE			(EntityType.ZOMBIE, 54, "Zombie"),
	SLIME			(EntityType.SLIME, 55, "Slime"),
	GHAST			(EntityType.GHAST, 56, "Ghast"),
	ENDERMAN		(EntityType.ENDERMAN, 58, "Enderman"),
	SILVERFISH		(EntityType.SILVERFISH, 60, "Silverfish"),
	BLAZE			(EntityType.BLAZE, 61, "Blaze"),
	PIG				(EntityType.PIG, 90, "Pig"),
	SHEEP			(EntityType.SHEEP, 91, "Sheep"),
	COW				(EntityType.COW, 92, "Cow"),
	CHICKEN			(EntityType.CHICKEN, 93, "Chicken"),
	SQUID			(EntityType.SQUID, 94, "Squid"),
	WOLF			(EntityType.WOLF, 95, "Wolf"),
	VILLAGER		(EntityType.VILLAGER, 120, "Villager"),
	OCELOT			(EntityType.OCELOT, 98, "Ocelot");
	
	private final EntityType entityType;
	private final Integer creatureId;
	private final String friendlyName;
	
	EggType(EntityType entityType, Integer creatureId, String friendlyName){
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
	
	public static EggType getEggType(Entity entity){
		for(EggType eggType : EggType.values()){
			if(!eggType.getCreatureType().getEntityClass().isInstance(entity)){
				continue;
			}
			return eggType;
		}
		return null;
	}
}
