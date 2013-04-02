/*
EggCatcher
Copyright (C) 2012, 2013  me@shansen.me, andre@norcode.com

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

package me.shansen.EggCatcher.events;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EggCaptureEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    boolean cancelled = false;
    Egg egg;

    public EggCaptureEvent(Entity what, Egg egg) {
        super(what);
        this.egg = egg;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public Egg getEgg() {
        return this.egg;
    }

    public void setCancelled(boolean isCancelled) {
        this.cancelled = isCancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
