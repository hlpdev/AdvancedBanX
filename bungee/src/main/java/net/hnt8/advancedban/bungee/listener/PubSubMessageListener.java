package net.hnt8.advancedban.bungee.listener;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.hnt8.advancedban.MethodInterface;
import net.hnt8.advancedban.Universal;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 *
 * @author Beelzebu
 */
public class PubSubMessageListener implements Listener {
    
    private static final MethodInterface mi = Universal.get().getMethods();

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onMessageReceive(PubSubMessageEvent e) {
        if (e.getChannel().equals("advancedban:main")) {
            String result = e.getMessage().replace('§', '&');
            MiniMessage miniMessage = MiniMessage.miniMessage();
            LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
            result = ChatColor.translateAlternateColorCodes('&', serializer.serialize(miniMessage.deserialize(result)));

            String[] msg = result.split(" ");
            
            if (e.getMessage().startsWith("kick ")) {
                if (ProxyServer.getInstance().getPlayer(msg[1]) != null) {
                    ProxyServer.getInstance().getPlayer(msg[1]).disconnect(e.getMessage().substring((msg[0] + msg[1]).length() + 2));
                }
            } else if (e.getMessage().startsWith("notification ")) {
                for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
                    if (mi.hasPerms(pp, msg[1])) {
                        mi.sendMessage(pp, e.getMessage().substring((msg[0] + msg[1]).length() + 2));
                    }
                }
            } else if (e.getMessage().startsWith("message ")) {
                if (ProxyServer.getInstance().getPlayer(msg[1]) != null) {
                    ProxyServer.getInstance().getPlayer(msg[1]).sendMessage(e.getMessage().substring((msg[0] + msg[1]).length() + 2));
                }
                if (msg[1].equalsIgnoreCase("CONSOLE")) {
                    ProxyServer.getInstance().getConsole().sendMessage(e.getMessage().substring((msg[0] + msg[1]).length() + 2));
                }
            }
        } else if (e.getChannel().equals("advancedban:connection")) {
            String[] msg = e.getMessage().split(",");
            Universal.get().getIps().remove(msg[0].toLowerCase());
            Universal.get().getIps().put(msg[0].toLowerCase(), msg[1]);
        }
    }
}