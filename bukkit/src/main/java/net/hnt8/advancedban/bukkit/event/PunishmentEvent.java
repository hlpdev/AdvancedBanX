package net.hnt8.advancedban.bukkit.event;

import net.hnt8.advancedban.utils.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a punishment is created
 */
public class PunishmentEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    
    private final Punishment punishment;

    public PunishmentEvent(Punishment punishment) {
        super(false);
        this.punishment = punishment;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Punishment getPunishment() {
        return this.punishment;
    }
}