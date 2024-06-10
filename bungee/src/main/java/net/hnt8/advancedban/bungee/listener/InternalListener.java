package net.hnt8.advancedban.bungee.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.hnt8.advancedban.Universal;
import net.hnt8.advancedban.bungee.event.PunishmentEvent;
import net.hnt8.advancedban.bungee.event.RevokePunishmentEvent;
import net.hnt8.advancedban.manager.TimeManager;
import net.hnt8.advancedban.manager.UUIDManager;
import net.hnt8.advancedban.utils.Punishment;
import net.hnt8.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Beelzebu
 */
public class InternalListener implements Listener {

    private final Universal universal = Universal.get();

    @EventHandler
    public void onPunish(PunishmentEvent e) {
        sendToBukkit("Punish", Collections.singletonList(e.getPunishment().toString()));
    }

    @EventHandler
    public void onUnPunish(RevokePunishmentEvent e) {
        sendToBukkit("Unpunish", Collections.singletonList(e.getPunishment().toString()));
    }

    @EventHandler
    public void onPluginMessageEvent(PluginMessageEvent e) {
        if (!e.getTag().equals("advancedban:main")) {
            return;
        }
        if (e.getSender() instanceof ProxiedPlayer) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String channel = in.readUTF();
        switch (channel) {
            case "Punish":
                String message = in.readUTF();
                try {
                    JsonObject punishment = universal.getGson().fromJson(message, JsonObject.class);
                    new Punishment(
                            punishment.get("name").getAsString(),
                            UUIDManager.get().getUUID(punishment.get("uuid").getAsString()),
                            punishment.get("reason").getAsString(),
                            punishment.get("operator") != null ? punishment.get("operator").getAsString() : "CONSOLE",
                            PunishmentType.valueOf(punishment.get("punishmenttype").getAsString().toUpperCase()),
                            punishment.get("start") != null ? punishment.get("start").getAsLong() : TimeManager.getTime(),
                            TimeManager.getTime() + punishment.get("end").getAsLong(),
                            punishment.get("calculation") != null ? punishment.get("calculation").getAsString() : null,
                            -1
                    ).create(punishment.get("silent") != null && punishment.get("silent").getAsBoolean());
                    universal.getLogger().info("A punishment was created using PluginMessaging listener.");
                    universal.getLogger().fine(punishment.toString());
                } catch (JsonSyntaxException | NullPointerException ex) {
                    universal.getLogger().severe("An exception as occurred while reading a punishment from plugin messaging channel.");
                    universal.getLogger().fine("Message: " + message);
                    universal.getLogger().fine("StackTrace:");
                    
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    ex.printStackTrace(printWriter);
                    universal.getLogger().fine(stringWriter.toString());
                }
                break;
            default:
                universal.getLogger().fine("Unknown channel for tag \"AdvancedBan\"");
                break;
        }
    }

    public void sendToBukkit(String channel, List<String> messages) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channel);
        messages.forEach(out::writeUTF);
        ProxyServer.getInstance().getServers().keySet().forEach(server -> ProxyServer.getInstance().getServerInfo(server).sendData("advancedban:main", out.toByteArray(), true));
    }
}
