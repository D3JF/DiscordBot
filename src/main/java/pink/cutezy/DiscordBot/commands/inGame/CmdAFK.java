package pink.cutezy.DiscordBot.commands.inGame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pink.cutezy.DiscordBot.framework.AFKPlayer;
import pink.cutezy.DiscordBot.listeners.AFKPlayerListener;

public class CmdAFK implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof ConsoleCommandSender)
            return true;
        Player player = (Player) sender;
        AFKPlayer afkPlayer = AFKPlayerListener.getAfkPlayerByName(player.getName());
        if(afkPlayer == null)
            return true;
        AFKPlayerListener.sendAfk(player, afkPlayer, !afkPlayer.isAFK);
        return true;
    }
}
