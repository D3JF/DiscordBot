package pink.cutezy.DiscordBot.inListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.config.Configuration;
import pink.cutezy.DiscordBot.DiscordConfig;
import pink.cutezy.DiscordBot.DiscordPlugin;
import pink.cutezy.PluginCore.ModConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pink.cutezy.DiscordBot.DiscordUtil.*;
import static pink.cutezy.DiscordBot.DiscordPlugin.sendOut;

public class DiscordPlayerListener extends PlayerListener {
    public void onPlayerJoin(PlayerJoinEvent event) {
        DiscordPlugin inst = DiscordPlugin.getInstance();
        Player player = event.getPlayer();

        /*Simple way to convert Johny's Discord plugin to our own.
        * This method is only used because the two are very similar */
        File oldFile = new File(inst.getDataFolder(), "players/"+player.getName()+".properties");
        if(oldFile.exists()) {
            Configuration conf = inst.getModConfig().getPlayerConfigMod("players", player.getName());
            try {
                List<String> lines = Files.readAllLines(oldFile.toPath());
                for (String line : lines) {
                    if (line.startsWith("#"))
                        continue;
                    String[] split = line.split("=");
                    if (split.length < 2 || split[1].isEmpty())
                        continue;
                    switch (split[0].toLowerCase()) {
                        case "confirmeduser":
                            conf.setProperty("DiscordId", split[1]);
                            break;
                        case "picturetype":
                            conf.setProperty("DiscordPicture", split[1]);
                            break;
                    }
                }
                boolean b = oldFile.delete();
                if (!b)
                    System.out.println("");
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                conf.save();
            }
        }

        DiscordConfig instance = DiscordPlugin.getConfig();
        if(instance.joinMessage.enabled) {
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
