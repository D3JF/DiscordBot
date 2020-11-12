package pink.cutezy.DiscordBot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import pink.cutezy.DiscordBot.framework.ColorUtil;
import pink.cutezy.PluginCore.PluginUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pink.cutezy.DiscordBot.DiscordPlugin.getConfig;
import static pink.cutezy.DiscordBot.DiscordPlugin.getJDA;

public class DiscordUtil {
    private static final Map<String, String> replacers = new HashMap<String, String>() {
        {
            put("%username%", "%pName%");
            put("%name%", "%pName%");
            put("%nickname%", "%nick%");
            put("%onlineCount%", "%online%");
            put("%count%", "%online%");
            put("%onlinePlayers%", "%online%");
            put("%onlineMax%", "%max%");
            put("%maxCount%", "%max%");
            put("%maxPlayers%", "%max%");
            put("%topRole", "%role%");
            put("%faction%", "%town");
            put("%factionName%", "%town%");
            put("%townName", "%town%");
        }
    };

    private static String replacers(String s) {
        for(Map.Entry<String, String> entry : replacers.entrySet())
            s = s.replaceAll("%" + entry.getKey() + "%", entry.getValue());
        return s;
    }

    private static String varTalkMsg(
            String orig, String roleName, String townName,
             String playerName, String playerNick, Map<String, Object> args) {
        if(args.containsKey("msg")) {
            String msg = (String) args.get("msg");
            if(msg != null && !msg.isEmpty()) {
                // This fixes regex character methods like $ and . for replaceAll()
                while (orig.contains("%msg%")) {
                    orig = orig.replace("%msg%", msg);
                }
            }
        }
        orig = orig.replaceAll("%role%", roleName);
        orig = orig.replaceAll("%town%", townName);
        orig = orig.replaceAll("%nick%", playerNick);
        orig = orig.replaceAll("%pName%", playerName);
        int online = Bukkit.getServer().getOnlinePlayers().length;
        int max = Bukkit.getServer().getMaxPlayers();
        if(args.containsKey("roleColor"))
            orig = orig.replaceAll("%roleColor%", args.get("roleColor").toString());
        else
            orig = orig.replaceAll("%roleColor%", "");
        if(args.containsKey("curPlayers"))
            online = online + (int) args.get("curPlayers");
        if(!args.containsKey("disableColor"))
            orig = PluginUtils.colorize(orig);
        orig = orig.replaceAll("%online%", online+"");
        orig = orig.replaceAll("%max%", max+"");
        return orig;
    }
    public static String msgIn(User user, String orig, String msg) {
        orig = replacers(orig);
        TextChannel channel = getJDA().getTextChannelById(getConfig().channelId);
        if(channel == null) {
            System.out.println("Channel fail on " + user.getAsTag());
            return orig;
        }
        Member member = channel.getGuild().getMember(user);
        if(member == null) {
            System.out.println("Member fail on " + user.getAsTag());
            return orig;
        }
        Map<String, Object> args = new HashMap<>();
        args.put("msg", msg);
        String roleName = "";
        List<Role> roles = member.getRoles();
        if(roles.size() > 0) {
            Role topRole = roles.get(0);
            roleName = topRole.getName();
            if(roleName.length() > 12)
                roleName = roleName.substring(12) + "...";
            if(topRole.getColor() != null)
                args.put("roleColor", ColorUtil.fromRGB(topRole.getColor()));
        }
        String nick = member.getNickname() == null ? user.getName() : member.getNickname();
        orig = varTalkMsg(orig, roleName, "", user.getName(), nick, args);
        return orig;
    }
    public static String msgToUsefulOut(@Nullable Player user, String orig, Map<String, Object> args, String msg) {
        orig = replacers(orig);
        String userName = "?";
        String userNick = "?";
        String userRole = "?";
        if(user != null) {
            userName = user.getName();
            userNick = user.getDisplayName();
            if(getConfig().permissions_pex) {
                ru.tehkode.permissions.PermissionManager manager = ru.tehkode.permissions.bukkit.PermissionsEx.getPermissionManager();
                ru.tehkode.permissions.PermissionGroup mainGroup = manager.getUser(user).getGroups()[0];
                userRole = mainGroup.getName();
            }
        }
        if(args == null)
            args = new HashMap<>();
        args.put("msg", msg);
        orig = varTalkMsg(orig, userRole, "", userName, userNick, args);
        return orig;
    }

    public static String getImageFromCfg(String name) {
        Configuration config = DiscordConfig.modConfig.getPlayerConfigMod("players", name);
        int pic = config.getInt("pictureType", 6);
        String mino;
        switch(pic) {
            case 2: mino = "cube";break;
            case 3: mino = "body";break;
            case 4: mino = "armor/body";break;
            case 5: mino = "bust";break;
            case 6: mino = "armor/bust";break;
            default: mino = "helm";break;
        }
        return "http://minotar.net/" + mino + "/" + name + "/100.png";
    }
}
