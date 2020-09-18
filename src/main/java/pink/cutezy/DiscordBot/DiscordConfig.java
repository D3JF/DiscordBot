package pink.cutezy.DiscordBot;

import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import pink.cutezy.PluginCore.ModConfig;
import pink.cutezy.PluginCore.PluginException;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiscordConfig {
    final String token;
    protected String channelId;
    public final String selfName;
    public final String selfIcon;
    public final String webhookURL;
    public final String defaultStatus;
    public final String defaultStatusType;
    public final String onlineStatus;

    public final boolean playerRequireAccConfirm;
    public final boolean playerWebhookDeaths;
    public final boolean playerWebhookOwnName;
    public final boolean playerWebhookJoinLeave;

    public final Enabler<String> deathMessages;
    public final Enabler<String> msgToDiscord;
    public final Enabler<String> msgFromDiscord;
    public final Enabler<String> joinMessage;
    public final Enabler<String> quitMessage;
    public final Enabler<String> endMessage;
    public final Enabler<String> startMessage;

    final Configuration c;

    DiscordConfig() throws PluginException {
        this.c = ModConfig.getConfigByName("Discord");
        DiscordConfig.getMap().forEach((String, Object) -> {
            boolean save = false;
            if(!ModConfig.contains(c, String)) {
                System.out.println("Doesn't contain " + String);
                this.c.setProperty(String, Object);save=true;}
            if(save)
                this.c.save();
        });
        final ConfigurationNode botNode = c.getNode("Bot");
        this.token = botNode.getString("Token");
        this.webhookURL = botNode.getString("WebhookURL");

        if(this.token.isEmpty() || this.webhookURL.isEmpty())
            throw new PluginException("Required BotToken for DiscordPlugin; Check your DiscordPlugin Config");

        final ConfigurationNode botSelfNode = botNode.getNode("Self");
        this.selfName = botSelfNode.getString("Name");
        final String selfIcon = botSelfNode.getString("IconUrl");

        this.selfIcon = selfIcon.isEmpty() || selfIcon.equalsIgnoreCase("None") ? null : selfIcon;

        final ConfigurationNode msgNode = c.getNode("Messages");
        this.startMessage = this.<String>enablerToClass(msgNode.getNode("StartMessage"));
        this.endMessage = this.<String>enablerToClass(msgNode.getNode("EndMessage"));
        this.joinMessage = this.<String>enablerToClass(msgNode.getNode("JoinMessage"));
        this.quitMessage = this.<String>enablerToClass(msgNode.getNode("QuitMessage"));

        final ConfigurationNode playerMessages = msgNode.getNode("PlayerMessages");
        this.playerRequireAccConfirm = playerMessages.getBoolean("RequiredAccConfirm", false);
        this.msgToDiscord = this.<String>enablerToClass(playerMessages.getNode("ToDiscord"));
        this.msgFromDiscord = this.<String>enablerToClass(playerMessages.getNode("FromDiscord"));
        this.deathMessages = this.<String>enablerToClass(playerMessages.getNode("DeathMessage"));

        final ConfigurationNode webhookNode = playerMessages.getNode("Webhook");
        this.playerWebhookOwnName = webhookNode.getBoolean("UseOwnPlayerName", true);
        this.playerWebhookDeaths = webhookNode.getBoolean("UseForDeaths", true);
        this.playerWebhookJoinLeave = webhookNode.getBoolean("UseForJoinLeave", false);

        defaultStatus = botNode.getString("Activity");
        defaultStatusType = botNode.getString("ActivityType");
        onlineStatus = botNode.getString("OnlineStatus");
    }

    public static class Enabler<T> {
        public final boolean enabled;
        public final T obj;
        Enabler(boolean enabled, @Nullable T obj) {
            this.enabled = enabled;
            this.obj = obj;
        }
    }
    private <T> Enabler<T> enablerToClass(ConfigurationNode node) {
        boolean enabled = (boolean) node.getProperty("Enabled");
        if(node.getProperty("Message") == null)
            return new Enabler<>(enabled, null);
        T msg = (T) node.getProperty("Message");
        return new Enabler<>(enabled, msg);
    }
    @Nullable
    private <T> T enablerToString(Enabler<T> enabler) {
        return enabler.enabled ? enabler.obj : null;
    }

    static DiscordConfig instance() throws PluginException {
        return new DiscordConfig();
    }
    static Map<String, Object> getMap() {
        return autoConfigMap;}
    private static Map<String, Object> autoConfigMap = new LinkedHashMap<String, Object>(){
        {
            put("Bot", new LinkedHashMap<String, Object>() {
                {
                    put("Self", new LinkedHashMap<String, Object>() {
                        {
                            put("Name", "Server");
                            put("IconUrl", "None");
                        }
                    });
                    put("Token", "");
                    put("Guild", new LinkedHashMap<String, Object>() {
                        {
                            put("GuildID", 0);
                            put("ChannelID", 0);
                        }
                    });
                    put("WebhookURL", "");
                    put("OnlineStatus", "ONLINE");
                    put("Activity", "Minecraft b1.7.3");
                    put("ActivityType", "STREAMING");
                }
            });
            put("Messages", new LinkedHashMap<String, Object>() {
                {
                    put("PlayerMessages", new LinkedHashMap<String, Object>() {
                        {
                            put("RequiredAccConfirm", false);
                            put("DeathMessage", new LinkedHashMap<String, Object>() {
                                {
                                    put("Enabled", true);
                                    put("Messages", "```css\\n[%username% died]```");
                                }
                            });
                            put("ToDiscord", new LinkedHashMap<String, Object>() {
                                {
                                    put("Enabled", true);
                                    put("Message", "%username%");
                                }
                            });
                            put("FromDiscord", new LinkedHashMap<String, Object>() {
                                {
                                    put("Enabled", true);
                                    put("Message", "&c[D] [%role%] %username%: %msg%");
                                }
                            });
                            put("Webhook", new LinkedHashMap<String, Object>() {
                                {
                                    put("UseOwnPlayerName", true);
                                    put("UseForDeaths", true);
                                    put("UseForJoinLeave", false);
                                }
                            });
                        }
                    });
                    put("FromDiscord", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("Message", "&c[D] [%role%] %username%: %msg%");
                        }
                    });

                    put("JoinMessage", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("Message", "**[%role%] %username%** Joined The Game [%onlineCount%/%maxCount%]");
                        }
                    });

                    put("QuitMessage", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("Message", "**%username%** Quit The Game [%onlineCount%/%maxCount%]");
                        }
                    });

                    put("StartMessage", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("Message", "**Server Has Started**");
                        }
                    });

                    put("EndMessage", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("Message", "**Server Was Stopped**");
                        }
                    });
                }
            });
        }
    };
}
