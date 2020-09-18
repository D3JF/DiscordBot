package pink.cutezy.DiscordBot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import pink.cutezy.DiscordBot.DiscordConfig;
import pink.cutezy.DiscordBot.DiscordPlugin;

import static pink.cutezy.DiscordBot.DiscordUtil.*;
import static pink.cutezy.DiscordBot.DiscordPlugin.sendOut;

public class DiscordPlayerListener extends PlayerListener {
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordConfig instance = DiscordPlugin.getConfig();
        if(instance.joinMessage.enabled) {
            Player player = event.getPlayer();
            String name = player.getName();
            if (instance.playerWebhookJoinLeave)
                DiscordPlugin.sendOut(name, getImageFromCfg(name), msgToUsefulOut(player, instance.joinMessage.obj));
            else
                sendOut(msgToUsefulOut(player, instance.joinMessage.obj));
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        DiscordPlugin.sendOut(name, getImageFromCfg(name), event.getMessage());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        DiscordConfig instance = DiscordPlugin.getConfig();
        if(instance.quitMessage.enabled) {
            Player player = event.getPlayer();
            String name = player.getName();
            if (instance.playerWebhookJoinLeave)
                DiscordPlugin.sendOut(name, getImageFromCfg(name), msgToUsefulOut(player, instance.quitMessage.obj));
            else
                sendOut(msgToUsefulOut(player, instance.quitMessage.obj));
        }
    }
}
