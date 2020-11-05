package pink.cutezy.DiscordBot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import javax.annotation.Nullable;
import java.util.HashMap;
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
             String playerName, String playerNick, String[] msg) {
        if(msg.length > 0)
            orig = orig.replaceAll("%msg%", msg[0]);
        orig = orig.replaceAll("%role%", roleName);
        orig = orig.replaceAll("%town%", townName);
        orig = orig.replaceAll("%nick%", playerNick);
        orig = orig.replaceAll("%pName%", playerName);
        orig = orig.replaceAll("%online%", Bukkit.getServer().getOnlinePlayers().length+"");
        orig = orig.replaceAll("%max%", Bukkit.getServer().getMaxPlayers()+"");
        return orig;
    }
    public static String msgIn(User user, String orig, String... msg) {
        orig = replacers(orig);
        TextChannel channel = getJDA().getTextChannelById(getConfig().channelId);
        if(channel == null)
            return orig;
        Member member = channel.getGuild().getMember(user);
        if(member == null)
            return orig;
        Role topRole = member.getRoles().get(0);
        String nick = member.getNickname() == null ? user.getName() : member.getNickname();
        orig = varTalkMsg(orig, topRole.getName(), "", user.getName(), nick, msg);
        return orig;
    }
    public static String msgToUsefulOut(@Nullable Player user, String orig, String... msg) {
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
        orig = varTalkMsg(orig, userRole, "", userName, userNick, msg);
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
