package pink.cutezy.DiscordBot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import pink.cutezy.DiscordBot.DiscordPlugin;
import pink.cutezy.DiscordBot.framework.AFKPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AFKPlayerListener extends PlayerListener {
    static HashMap<String/*name*/, AFKPlayer> userMap = new HashMap<>();

    public AFKPlayerListener() {
        if(!DiscordPlugin.getConfig().playerAutoAFKEnabled)
            return;
        final long secToMilli = DiscordPlugin.getConfig().playerAutoAFKSeconds * 1000;
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(DiscordPlugin.getInstance(), () -> {
            for (Map.Entry<String, AFKPlayer> entry : userMap.entrySet()) {
                String userName = entry.getKey();
                AFKPlayer afkPlayer = entry.getValue();
                if (!afkPlayer.isAFK && afkPlayer.getSubMilli() > secToMilli) {
                    System.out.println("AFK");
                    Player player = Bukkit.getServer().getPlayer(userName);
                    sendAfk(player, afkPlayer, true);
                }
            }
        }, 600, 600);
    }

    @Nullable
    public static AFKPlayer getAfkPlayerByName(String name) {
        return userMap.getOrDefault(name, null);
    }

    public static void sendAfk(Player player, AFKPlayer afkPlayer, boolean isAfk) {
        if(!isAfk) {
            Location location = player.getLocation();
            Entity[] entities = location.getWorld().getChunkAt(location).getEntities();
            if (entities.length > 0) {
                for (Entity entity : entities) {
                    if(entity.getEntityId() == player.getEntityId())
                        continue;
                    if(entity.getLocation().distance(location) < 1.25F)
                        return;
                }
            }
        }
        for(Player online : Bukkit.getServer().getOnlinePlayers()) {
            if(online.getName().equals(player.getName())) {
                if(!isAfk)
                    online.sendMessage(""+
                            ChatColor.GREEN+"Welcome back "+player.getName()+"! " +
                            ChatColor.WHITE+"You were AFK for " + getTimeFromAFK(afkPlayer));
                else {
                    online.sendMessage(ChatColor.YELLOW + "You are now AFK");
                }
            } else {
                if(isAfk)
                    online.sendMessage(player.getName() + ChatColor.WHITE + " is now AFK");
                else
                    online.sendMessage(player.getName() + ChatColor.WHITE + " is no longer AFK");
            }
        }
        DiscordPlugin.sendOut("`"+player.getName() + " is "+(isAfk ? "now" : "no longer")+" AFK`");
        afkPlayer.updateTime();
        afkPlayer.isAFK = isAfk;
    }

    private static String getTimeFromAFK(AFKPlayer afkPlayer) {
        long sub = afkPlayer.getSubMilli();
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        String hours = String.format("%.0f", (float)timeUnit.toHours(sub));
        int minutes = (int) timeUnit.toMinutes(sub);
        int seconds = (int) timeUnit.toSeconds(sub);
        return ((hours) + " hours " + (minutes) + " minutes " + (seconds) + " seconds");
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!DiscordPlugin.getConfig().playerAutoAFKEnabled)
            return;
        Player player = event.getPlayer();
        if(!userMap.containsKey(player.getName())) {
            userMap.put(player.getName(), new AFKPlayer(player));
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if(!DiscordPlugin.getConfig().playerAutoAFKEnabled)
            return;
        Player player = event.getPlayer();
        String pName = player.getName();
        AFKPlayer afkPlayer = userMap.get(pName);
        if(afkPlayer.isAFK)
            sendAfk(player, afkPlayer, false);
        else
            afkPlayer.updateTime();
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!DiscordPlugin.getConfig().playerAutoAFKEnabled)
            return;
        Player player = event.getPlayer();
        String pName = player.getName();
        Location from = event.getFrom();
        Location to = event.getTo();
        AFKPlayer afkPlayer = userMap.get(pName);
        if(afkPlayer.isAFK) {
            if(from.distance(to) < 0.10000D)
                return;
            sendAfk(player, afkPlayer, false);
        } else {
            if(from.distance(to) > 0.00000D)
                afkPlayer.updateTime();
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(!DiscordPlugin.getConfig().playerAutoAFKEnabled)
            return;
        Player player = event.getPlayer();
        userMap.remove(player.getName());
    }
}
