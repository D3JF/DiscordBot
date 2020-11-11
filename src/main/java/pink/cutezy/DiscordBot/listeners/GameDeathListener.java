package pink.cutezy.DiscordBot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import pink.cutezy.DiscordBot.DiscordConfig;
import pink.cutezy.DiscordBot.DiscordPlugin;

import static pink.cutezy.DiscordBot.DiscordUtil.*;
import static pink.cutezy.DiscordBot.DiscordPlugin.sendOut;

public class GameDeathListener extends EntityListener {
    public void onEntityDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        DiscordConfig config = DiscordPlugin.getConfig();
        if(config.deathMessages.enabled) {
            Player player = (Player) event.getEntity();
            String name = player.getName();
            if(!config.playerWebhookDeaths)
                sendOut(msgToUsefulOut(player, config.deathMessages.msg, null, null));
            else
                DiscordPlugin.sendOut(name, getImageFromCfg(name), msgToUsefulOut(player, config.deathMessages.msg, null, null));
        }
    }
}
