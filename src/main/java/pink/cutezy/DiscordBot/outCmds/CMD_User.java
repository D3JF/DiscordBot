package pink.cutezy.DiscordBot.outCmds;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import pink.cutezy.DiscordBot.DiscordConfig;
import pink.cutezy.DiscordBot.DiscordPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CMD_User implements CMD {
    @Override
    public void Invoke(@Nonnull final Message msg, @Nullable String[] args) {
        if(args == null || args.length < 1) {
            return;
        }
        List<User> userList = msg.getMentionedUsers();
        if(userList.isEmpty())
            return;
        userList.get(0);
    }

    @Nonnull @Override
    public String getDesc() {
        return DiscordPlugin.getConfig().cmdUser.description;
    }
}
