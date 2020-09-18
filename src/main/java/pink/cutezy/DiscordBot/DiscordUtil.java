package pink.cutezy.DiscordBot;

import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import pink.cutezy.PluginCore.ModConfig;

import java.util.HashMap;
import java.util.Map;

import static pink.cutezy.DiscordBot.DiscordPlugin.getConfig;
import static pink.cutezy.DiscordBot.DiscordPlugin.getJDA;

public class DiscordUtil {
    private static Map<String, String> replacers = new HashMap<String, String>(){
        {
            put("%username%", "%pName%");
            put("%name%", "%pName%");
            put("%nickname%", "%nick%");
            put("%onlineCount%", "%online%");
            put("%count%", "%online%");
            put("%maxCount%", "%max%");
            put("%maxPlayers%", "%max%");
            put("%topRole", "%role%");
            put("%factionName%", "%faction%");
            put("%townName", "%town%");
        }
    };

    private static String replacers(String s) {
        for(Map.Entry<String, String> entry : replacers.entrySet()) {
            String key = entry.getKey();
            String replace = entry.getValue();
            s = s.replaceAll(key, replace);
        };
        return s;
    }

    public static String msgToUsefulIn(User user, String orig, String... msg) {
        orig = replacers(orig);
        TextChannel channel = getJDA().getTextChannelById(getConfig().channelId);
        if(channel == null)
            return orig;
        Member member = channel.getGuild().getMember(user);
        if(member == null)
            return orig;
        Role topRole = member.getRoles().get(0);
        if(msg.length > 0)
            orig = orig.replaceAll("%msg%", msg[0]);
        orig = orig.replaceAll("%role%", topRole.getName());
        orig = orig.replaceAll("%faction%", "");
        orig = orig.replaceAll("%town%", "");
        orig = orig.replaceAll("%nick%", member.getNickname() == null ? user.getName() : member.getNickname());
        orig = orig.replaceAll("%pName%", user.getName());
        orig = orig.replaceAll("%online%", Bukkit.getServer().getOnlinePlayers().length+"");
        orig = orig.replaceAll("%max%", Bukkit.getServer().getMaxPlayers()+"");
        return orig;
    }
    public static String msgToUsefulOut(Player user, String orig, String... msg) {
        orig = replacers(orig);
        if(msg.length > 0)
            orig = orig.replaceAll("%msg%", msg[0]);
        orig = orig.replaceAll("%role%", "");
        orig = orig.replaceAll("%faction%", "");
        orig = orig.replaceAll("%town%", "");
        orig = orig.replaceAll("%nick%", user.getDisplayName());
        orig = orig.replaceAll("%pName%", user.getName());
        orig = orig.replaceAll("%online%", Bukkit.getServer().getOnlinePlayers().length+"");
        orig = orig.replaceAll("%max%", Bukkit.getServer().getMaxPlayers()+"");
        return orig;
    }

    public static String getImageFromCfg(String name) {
        Configuration config = ModConfig.getPlayerConfigMod("DiscordBot", name);
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
