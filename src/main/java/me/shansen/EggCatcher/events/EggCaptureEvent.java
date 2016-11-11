package me.shansen.EggCatcher.events;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EggCaptureEvent
extends EntityEvent
implements Cancellable {
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

