package net.hnt8.advancedban.bungee.event;

import net.hnt8.advancedban.utils.Punishment;
import net.md_5.bungee.api.plugin.Event;

/**
 * Event fired when a punishment is revoked
 */

public class RevokePunishmentEvent extends Event {
    private final Punishment punishment;
    private final boolean massClear;

    public RevokePunishmentEvent(Punishment punishment, boolean massClear) {
        this.punishment = punishment;
        this.massClear = massClear;
    }

    public Punishment getPunishment() {
        return punishment;
    }

    public boolean isMassClear() {
        return massClear;
    }
}