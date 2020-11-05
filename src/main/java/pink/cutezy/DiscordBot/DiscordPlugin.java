package pink.cutezy.DiscordBot;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import pink.cutezy.DiscordBot.inListeners.DiscordDeathListener;
import pink.cutezy.DiscordBot.inListeners.DiscordPlayerListener;
import pink.cutezy.DiscordBot.outCmds.CMD;
import pink.cutezy.DiscordBot.outCmds.CMD_User;
import pink.cutezy.PluginCore.CoreJavaPlugin;
import pink.cutezy.PluginCore.PluginException;

import javax.annotation.Nullable;
import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DiscordPlugin extends CoreJavaPlugin {
    private static DiscordPlugin instance;
    public static DiscordPlugin getInstance() {
        return instance; }

    private WebhookClient client;
    protected DiscordConfig config;
    private boolean isShuttingDown = false;
    private boolean isForceShutDown = false;

    protected WebhookMessageBuilder self;
    protected Logger logger;
    protected JDA jda;

    private Map<String/*cmdName*/, CMD> cmdOutMap = new HashMap<>();

    public DiscordPlugin() {
        super();
        instance = this;
        newInst(DiscordDeathListener.class)
                .event(Event.Type.ENTITY_DEATH);
        newInst(DiscordPlayerListener.class)
                .event(Event.Type.PLAYER_JOIN).event(Event.Type.PLAYER_QUIT).event(Event.Type.PLAYER_CHAT);

        Thread thread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if(thread.isInterrupted()) {
                    System.out.println("Shutdown Triggered");
                    isForceShutDown = true;
                }
            }
        });
        registerCmdOut(new CMD_User(), "user");
    }

    //region Command Registers
    void registerCmdOut(CMD cmd, String... names) {
        for(String name : names) {
            cmdOutMap.put(name, cmd);
        }
    }
    @Nullable
    public CMD getCmdOut(String fromName) {
        if(cmdOutMap.containsKey(fromName))
            return cmdOutMap.get(fromName);
        return null;
    }
    //endregion

    //region Plugin Initialization
    @Override
    public void onEnable() {
        this.logger = Bukkit.getServer().getLogger();
        this.logger.info("DiscordBot initializing...");
        try {
            this.config = DiscordConfig.instance(this);
            this.self = new WebhookMessageBuilder()
                .setAllowedMentions(AllowedMentions.none())
                .setAvatarUrl(config.selfIcon)
                .setUsername(config.selfName)
                .setTTS(false);
            jda = JDABuilder.createDefault(getConfig().token)
                .addEventListeners(new DiscordListener(this))
                .setActivity(
                        Activity.of(
                                Activity.ActivityType.valueOf(config.defaultStatusType),
                                config.defaultStatus
                        )
                ).setStatus(OnlineStatus.valueOf(config.onlineStatus))
                    .build();
            super.onEnable();
        } catch(PluginException | LoginException e) {
            e.printStackTrace();
        }
    }

    void onReady() {
        client = WebhookClient.withUrl(config.webhookURL);
        if(getConfig().startMessage.enabled) {
            this.self.setContent(config.startMessage.obj);
            client.send(self.build()).thenAccept(msg ->
                    config.channelId = String.valueOf(msg.getChannelId()));
            logger.info("DiscordBot ready!");
        }
    }

    @Override
    public void onDisable() {
        this.isShuttingDown = true;
    }

    void onShutdown() {
        if(!isShuttingDown || isForceShutDown) {
            if (config.forceEndMessage.enabled)
                sendOut(config.forceEndMessage.obj);
        } else if(config.endMessage.enabled)
            sendOut(config.endMessage.obj);
    }
    //endregion

    //region Discord Send In/Out
    public static void sendIn(User user, String msg) {
        if(getConfig().msgFromDiscord.enabled) {
            String orig = getConfig().msgFromDiscord.obj;
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                msg = DiscordUtil.msgIn(user, orig, msg);
                player.sendMessage(msg);
            }
        }
    }
    public static void sendOut(String username, String avatarUrl, String msg) {
        getInstance().client.send(new WebhookMessageBuilder()
            .setAllowedMentions(AllowedMentions.none())
            .setAvatarUrl(avatarUrl)
            .setUsername(username)
            .setContent(msg)
            .setTTS(false)
                .build());
    }
    public static void sendOut(String msg) {
        DiscordPlugin instance = getInstance();
        instance.self.setContent(msg);
        instance.client.send(instance.self.build());
    }
    //endregion

    public static DiscordConfig getConfig() {
        return getInstance().config;
    }
    public static JDA getJDA() {
        return getInstance().jda;
    }
}
