package pink.cutezy.DiscordBot;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class DiscordListener extends ListenerAdapter {
    private DiscordPlugin plugin;
    DiscordListener(DiscordPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if(event.getAuthor().isBot() || event.getAuthor().isFake())
            return;
        MessageChannel channel = event.getChannel();
        if(!channel.getId().equals(DiscordPlugin.getConfig().channelId))
            return;
        User author = event.getAuthor();
        DiscordPlugin.sendIn(author, event.getMessage().getContentStripped());
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        super.onReady(event);
        plugin.onReady();
    }
}
