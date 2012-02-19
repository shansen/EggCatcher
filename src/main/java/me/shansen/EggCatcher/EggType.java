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

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

public enum EggType {
	PIG_ZOMBIE		(CreatureType.PIG_ZOMBIE, 57, "PigZombie"),
	MAGMA_CUBE		(CreatureType.MAGMA_CUBE, 62, "MagmaCube"),
	CAVE_SPIDER		(CreatureType.CAVE_SPIDER, 59, "CaveSpider"),
	MUSHROOM_COW	(CreatureType.MUSHROOM_COW, 96, "MushroomCow"),
	CREEPER			(CreatureType.CREEPER, 50, "Creeper"),
	SKELETON		(CreatureType.SKELETON, 51, "Skeleton"),
	SPIDER			(CreatureType.SPIDER, 52, "Spider"),
	ZOMBIE			(CreatureType.ZOMBIE, 54, "Zombie"),
	SLIME			(CreatureType.SLIME, 55, "Slime"),
	GHAST			(CreatureType.GHAST, 56, "Ghast"),
	ENDERMAN		(CreatureType.ENDERMAN, 58, "Enderman"),
	SILVERFISH		(CreatureType.SILVERFISH, 60, "Silverfish"),
	BLAZE			(CreatureType.BLAZE, 61, "Blaze"),
	PIG				(CreatureType.PIG, 90, "Pig"),
	SHEEP			(CreatureType.SHEEP, 91, "Sheep"),
	COW				(CreatureType.COW, 92, "Cow"),
	CHICKEN			(CreatureType.CHICKEN, 93, "Chicken"),
	SQUID			(CreatureType.SQUID, 94, "Squid"),
	WOLF			(CreatureType.WOLF, 95, "Wolf"),
	VILLAGER		(CreatureType.VILLAGER, 120, "Villager");
	
	private final CreatureType creatureType;
	private final Integer creatureId;
	private final String friendlyName;
	
	EggType(CreatureType creatureType, Integer creatureId, String friendlyName){
		this.creatureType = creatureType;
		this.creatureId = creatureId;
		this.friendlyName = friendlyName;
	}
	
	public short getCreatureId() {
		return this.creatureId.shortValue();
	}
	
	public CreatureType getCreatureType() {
		return this.creatureType;
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
