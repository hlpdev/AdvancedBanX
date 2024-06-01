package net.hnt8.advancedban.bukkit.event;

import net.hnt8.advancedban.utils.Punishment;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a punishment is revoked
 */
public class RevokePunishmentEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Punishment punishment;
    private final boolean massClear;

    public RevokePunishmentEvent(Punishment punishment, boolean massClear) {
        super(false);
        this.punishment = punishment;
        this.massClear = massClear;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    public boolean isMassClear() {
        return massClear;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}