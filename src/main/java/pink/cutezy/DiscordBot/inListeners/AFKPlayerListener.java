package pink.cutezy.DiscordBot.inListeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import pink.cutezy.DiscordBot.DiscordPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AFKPlayerListener extends PlayerListener {
    static HashMap<String/*name*/, AFKPlayer> userMap = new HashMap<>();
    private static class AFKPlayer {
        private boolean shouldUnAFK = false;
        private boolean isAFK = false;
        Location lastLocation = null;
        long lastActiveMilli = -1;
        private AFKPlayer(Player player) {
            this.lastLocation = player.getLocation();
            this.lastActiveMilli = System.currentTimeMillis();
        }
        private long getSubMilli() {
            return System.currentTimeMillis() - lastActiveMilli;
        }
    }

    public AFKPlayerListener(final Plugin plugin) {
        Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin,
            () -> userMap.forEach((userName, afkPlayer) -> {
                if(!afkPlayer.isAFK && afkPlayer.getSubMilli() > 120000 /* 2 minutes */) {
                    Player player = Bukkit.getServer().getPlayer(userName);
                    sendAfk(player, afkPlayer, true);
                }
            }
        ), 600, 600);
    }

    private void sendAfk(Player player, AFKPlayer afkPlayer, boolean isAfk) {
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
        PermissionUser user = PermissionsEx.getPermissionManager().getUser(player);
        String prefix = user.getPrefix();
        String userColor = getColorFromPrefix(prefix);
        for(Player online : Bukkit.getServer().getOnlinePlayers()) {
            if(online.getName().equals(player.getName())) {
                if(!isAfk)
                    online.sendMessage(""+
                        ChatColor.WHITE+"Welcome back "+userColor+player.getName()+ChatColor.WHITE+"! " +
                        ChatColor.WHITE+"You were AFK for " + getTimeFromAFK(afkPlayer));
                else {
                    prefix = getColorFromPrefix(prefix);
                    online.sendMessage(userColor + "You"+ChatColor.WHITE+" are now AFK");
                }
            } else {
                if(isAfk)
                    online.sendMessage(prefix + player.getName() + ChatColor.WHITE + " is now AFK");
                else
                    online.sendMessage(prefix + player.getName() + ChatColor.WHITE + " is no longer AFK");
            }
        }
        DiscordPlugin.sendOut("`"+player.getName() + " is "+(isAfk ? "now" : "no longer")+" AFK`");
        afkPlayer.isAFK = isAfk;
    }

    private String getTimeFromAFK(AFKPlayer afkPlayer) {
        long sub = afkPlayer.getSubMilli();
        String hours = String.format("%.0f", (float)TimeUnit.MILLISECONDS.toHours(sub));
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(sub);
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(sub);
        return ((hours) + " hours " + (minutes) + " minutes " + (seconds) + " seconds");
    }

    public static String getColorFromPrefix(String prefix) {
        int one = prefix.indexOf("[");
        if(one == -1)
            return prefix;
        int two = prefix.indexOf("]");
        if(two == -1)
            return prefix;
        one += 1;
        two -= 2;
        prefix = prefix.substring(one, two-one);
        prefix = prefix.substring(0, 2);
        prefix = prefix.replace("&", "§");
        return prefix;
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String pName = player.getName();
        if(!userMap.containsKey(pName))
            userMap.put(pName, new AFKPlayer(player));
        else {
            AFKPlayer afkPlayer = userMap.get(pName);
            if(afkPlayer.isAFK) {
                afkPlayer.lastActiveMilli = System.currentTimeMillis();
                sendAfk(player, afkPlayer, false);
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String pName = player.getName();
        Location from = event.getFrom();
        Location to = event.getTo();
        if(!userMap.containsKey(pName))
            userMap.put(pName, new AFKPlayer(player));
        else {
            AFKPlayer afkPlayer = userMap.get(pName);
            if(afkPlayer.shouldUnAFK) {
                afkPlayer.lastActiveMilli = System.currentTimeMillis();
                sendAfk(player, afkPlayer, false);
            } else if(afkPlayer.isAFK) {
                if(from.distance(to) < 0.1F)
                    return;
                afkPlayer.lastActiveMilli = System.currentTimeMillis();
                sendAfk(player, afkPlayer, false);
            } else {
                if(from.distance(to) > 0.5F)
                    afkPlayer.lastActiveMilli = System.currentTimeMillis();
            }
        }
    }
}
