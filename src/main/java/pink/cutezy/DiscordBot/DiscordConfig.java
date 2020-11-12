package pink.cutezy.DiscordBot;

import express.japanese.botto.RichEmbed;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import pink.cutezy.DiscordBot.commands.discord.ICMD;
import pink.cutezy.PluginCore.CoreJavaPlugin;
import pink.cutezy.PluginCore.ModConfig;
import pink.cutezy.PluginCore.PluginException;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiscordConfig {
    protected final String token;
    public String channelId;
    public final String selfName;
    public final String selfIcon;
    public final String webhookURL;
    public final String defaultStatus;
    public final String defaultStatusType;
    public final String onlineStatus;

    public final boolean cmdConfUseRichMessages;
    public final String cmdConfMessageTitle;
    public final String cmdConfMessageThumbnail;
    public final String cmdConfMessageImage;
    public final String cmdConfMessageFooter;
    public final String cmdConfMessageFooterIcon;
    public final Color cmdConfMessageColor;

    public final boolean playerRequireAccConfirm;
    public final boolean playerSendAsBotMessage;
    public final boolean playerWebhookDeaths;
    public final boolean playerWebhookOwnName;
    public final boolean playerWebhookJoinLeave;

    public final boolean playerAutoAFKEnabled;
    public final int playerAutoAFKSeconds;

    public final boolean permissions_pex;

    public final Enabler<String> deathMessages;
    public final Enabler<String> msgToDiscord;
    public final Enabler<String> msgFromDiscord;
    public final Enabler<String> joinMessage;
    public final Enabler<String> quitMessage;
    public final Enabler<String> startMessage;
    public final Enabler<String> endMessage;
    public final Enabler<String> forceEndMessage;

    public final EnablerCmd<String> cmdOnline;
    public final EnablerCmd<String> cmdTown;
    public final EnablerCmd<String> cmdUser;

    final Configuration c;

    public static ModConfig modConfig;

    public DiscordConfig(CoreJavaPlugin javaPlugin) throws PluginException {
        modConfig = javaPlugin.getModConfig();
        this.c = modConfig.getConfigByName("Discord");
        this.nodeCheck(c);

        final ConfigurationNode botNode = c.getNode("Bot");
        this.token = botNode.getString("Token");
        this.webhookURL = botNode.getString("WebhookURL");

        if(this.token.isEmpty() || this.webhookURL.isEmpty())
            throw new PluginException("Required BotToken for DiscordPlugin; Check your DiscordPlugin Config");

        final ConfigurationNode botSelfNode = nodeCheck(botNode, "Self");
        this.selfName = botSelfNode.getString("Name");
        final String selfIcon = botSelfNode.getString("IconUrl");
        this.selfIcon = selfIcon.isEmpty() || selfIcon.equalsIgnoreCase("None") ? null : selfIcon;

        final ConfigurationNode permsNode = nodeCheck(botNode, "Permissions");
        this.permissions_pex = permsNode.getBoolean("UsesPermissionsEx", true);
        /* TODO: Add more permisison styles */

        final ConfigurationNode msgNode = nodeCheck(c, "Messages");
        this.startMessage = this.enablerToClass("StartMessage", msgNode, String.class);
        this.endMessage = this.enablerToClass("EndMessage", msgNode, String.class);
        this.forceEndMessage = this.enablerToClass("ForceEndMessage", msgNode, String.class);
        this.joinMessage = this.enablerToClass("JoinMessage", msgNode, String.class);
        this.quitMessage = this.enablerToClass("QuitMessage", msgNode, String.class);

        final ConfigurationNode playerMessages = nodeCheck(msgNode, "PlayerMessages");
        this.playerRequireAccConfirm = playerMessages.getBoolean("RequiredAccConfirm", false);
        this.playerSendAsBotMessage = playerMessages.getBoolean("SendAsBotMessage", false);
        this.msgToDiscord = this.enablerToClass("ToDiscord", playerMessages, String.class);
        this.msgFromDiscord = this.enablerToClass("FromDiscord", playerMessages, String.class);
        this.deathMessages = this.enablerToClass("DeathMessage", playerMessages, String.class);

        final ConfigurationNode afkNode = nodeCheck(playerMessages, "AFKMessages");
        this.playerAutoAFKEnabled = afkNode.getBoolean("Enabled", true);
        this.playerAutoAFKSeconds = afkNode.getInt("SecondsTilAFK", 60+60);

        final ConfigurationNode webhookNode = nodeCheck(playerMessages, "Webhook");
        this.playerWebhookOwnName = webhookNode.getBoolean("UseOwnPlayerName", true);
        this.playerWebhookDeaths = webhookNode.getBoolean("UseForDeaths", true);
        this.playerWebhookJoinLeave = webhookNode.getBoolean("UseForJoinLeave", false);

        final ConfigurationNode commandsNode = nodeCheck(c, "DiscordCommands");
        this.cmdOnline = (EnablerCmd<String>) this.enablerToClass("online", commandsNode, String.class);
        this.cmdTown = (EnablerCmd<String>) this.enablerToClass("town", commandsNode, String.class);
        this.cmdUser = (EnablerCmd<String>) this.enablerToClass("user", commandsNode, String.class);
        this.pushCmd(this.cmdOnline, this.cmdTown, this.cmdUser);

        final ConfigurationNode cmdConfNode = nodeCheck(commandsNode, "Configuration");
        this.cmdConfUseRichMessages = cmdConfNode.getBoolean("UseRichMessages", true);
        this.cmdConfMessageTitle = noneCheck(cmdConfNode.getProperty("RichMessageTitle"));
        this.cmdConfMessageFooter = noneCheck(cmdConfNode.getProperty("RichMessageFooter"));
        this.cmdConfMessageThumbnail = noneCheck(cmdConfNode.getProperty("RichMessageThumbnail"));
        this.cmdConfMessageImage = noneCheck(cmdConfNode.getProperty("RichMessageImage"));
        this.cmdConfMessageFooterIcon = noneCheck(cmdConfNode.getProperty("RichMessageFooterIcon"));
        String richColor = noneCheck(cmdConfNode.getProperty("RichMessageColor"));
        if(richColor == null)
            this.cmdConfMessageColor = Color.BLACK;
        else {
            this.cmdConfMessageColor = Color.decode(richColor);
        }

        defaultStatus = botNode.getString("Activity");
        defaultStatusType = botNode.getString("ActivityType");
        onlineStatus = botNode.getString("OnlineStatus");
    }

    private void nodeCheck(Configuration c) {
        DiscordConfig.getMap().forEach((String, Object) -> {
            boolean save = false;
            if(!modConfig.contains(c, String)) {
                System.out.println("Doesn't contain " + String);
                this.c.setProperty(String, Object);save=true;}
            if(save)
                this.c.save();
        });
    }
    private ConfigurationNode nodeCheck(final ConfigurationNode c, String nodeName) {
        ConfigurationNode node = c.getNode(nodeName);
        /*Map<String, Object> map = getMap();
        if(nodeName.contains(".")) {
            String[] split = nodeName.split("\\.");
            for(String s : split)
                map = (Map<String, Object>) map.get(s);
        } else
            map = (Map<String, Object>) map.get(nodeName);
        map.forEach((String, Object) -> {
            boolean save = false;
            if(!modConfig.contains(node, String)) {
                System.out.println("Doesn't contain " + String);
                node.setProperty(nodeName+"."+String, Object);save=true;}
            if(save)
                this.c.save();
        });*/
        return node;
    }
    private String noneCheck(Object obj) {
        if(obj == null)
            return null;
        String s = (String) obj;
        return s.equals("None") || s.isEmpty() ? null : s;
    }

    @SafeVarargs
    private final void pushCmd(final EnablerCmd<String>... cmdEnabler) {
        for(final EnablerCmd<String> enabler : cmdEnabler) {
            final String sendable = DiscordUtil.msgToUsefulOut(null, enabler.msg, null, enabler.msg);
            ICMD newICMD = new ICMD() {
                @Override
                public void Invoke(@NotNull Message msg, @org.jetbrains.annotations.Nullable String[] args) {
                    RichEmbed richEmbed = null;
                    if(cmdConfUseRichMessages)
                        richEmbed = new RichEmbed()
                            .setTitle(DiscordUtil.msgToUsefulOut(null, cmdConfMessageTitle, null, null))
                            .setFooterText(DiscordUtil.msgToUsefulOut(null, cmdConfMessageFooter, null, null))
                            .setFooterIcon(cmdConfMessageFooterIcon)
                            .setThumbnail(cmdConfMessageThumbnail)
                            .setImage(cmdConfMessageImage)
                            .setDescription(sendable)
                            .setColor(cmdConfMessageColor);
                    if(enabler.sendPrivately) {
                        PrivateChannel channel = msg.getAuthor().openPrivateChannel().complete();
                        if(richEmbed == null)
                            channel.sendMessage(sendable).queue();
                        else
                            channel.sendMessage(richEmbed.build()).queue();
                        channel.close().queue();
                    } else {
                        TextChannel channel = (TextChannel) msg.getChannel();
                        if(richEmbed == null)
                            channel.sendMessage(sendable).queue();
                        else
                            msg.getChannel().sendMessage(richEmbed.build()).queue();
                    }
                }

                @NotNull
                @Override
                public String getDesc() {
                    return enabler.description;
                }
            };
            DiscordPlugin.getInstance().registerCmdOut(newICMD, enabler.originalName);
        }
    }

    public static class Enabler<Message> {
        public final String originalName;
        public final boolean enabled;
        public final Message msg;
        Enabler(String origName, boolean enabled, @Nullable Message msg) {
            this.originalName = origName;
            this.enabled = enabled;
            this.msg = msg;
        }
    }
    public static class EnablerCmd<Message> extends Enabler<Message> {
        public String description = null;
        public boolean sendPrivately = false;
        EnablerCmd(String name, boolean enabled, @Nullable Message msg, String desc, boolean priv) {
            super(name, enabled, msg);
            this.description = desc;
            this.sendPrivately = priv;
        }
    }
    private <T> Enabler<T> enablerToClass(String nodeName, ConfigurationNode originalNode, Class<T> clz) {
        ConfigurationNode node = originalNode.getNode(nodeName);
        boolean enabled = (boolean) node.getProperty("Enabled");
        Object msgProperty = node.getProperty("Message");
        if(msgProperty == null || !msgProperty.getClass().isAssignableFrom(clz))
            return new Enabler<>(nodeName, enabled, null);
        T msg = (T) msgProperty;
        Object descriptionObj = node.getProperty("Description");
        if(descriptionObj != null && descriptionObj.getClass().isAssignableFrom(String.class)) {
            String desc = (String) descriptionObj;
            boolean priv = (boolean) node.getProperty("SendPrivately");
            return new EnablerCmd<>(nodeName, enabled, msg, desc, priv);
        }
        return new Enabler<>(nodeName, enabled, msg);
    }
    @Nullable
    private <T> T enablerToString(Enabler<T> enabler) {
        return enabler.enabled ? enabler.msg : null;
    }

    static Map<String, Object> getMap() {
        return autoConfigMap;}
    private static final Map<String, Object> autoConfigMap = new LinkedHashMap<String, Object>(){
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
                    put("Activity", "Watching Minecraft Servers");
                    put("ActivityType", "STREAMING");
                    put("Permissions", new LinkedHashMap<String, Object>() {
                        {
                            put("UsesPermissionsEx", true);
                        }
                    });
                }
            });
            put("DiscordCommands", new LinkedHashMap<String, Object>() {
                {
                    put("Configuration", new LinkedHashMap<String, Object>() {
                        {
                            put("UseRichMessages", true);
                            put("RichMessageTitle", "Your Server Name Here");
                            put("RichMessageFooter", "None");
                            put("RichMessageImage", "None");
                            put("RichMessageFooterIcon", "None");
                            put("RichMessageColor", "#ffffff");
                        }
                    });
                    put("online", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("SendPrivately", false);
                            put("Description", "Shows how many players are currently online");
                            put("Message", "There are currently %online% online players out of %max%");
                        }
                    });
                    put("town", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", false);
                            put("SendPrivately", false);
                            put("Description", "Get town/faction by name and it's information");
                            put("Message", "Not Available");
                        }
                    });
                    put("user", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("SendPrivately", false);
                            put("Description", "Get information about a Discord user *if* they've confirmed their account");
                            put("Message", "Not Available");
                        }
                    });
                }
            });
            put("Messages", new LinkedHashMap<String, Object>() {
                {
                    put("PlayerMessages", new LinkedHashMap<String, Object>() {
                        {
                            put("RequiredAccConfirm", false);
                            put("SendAsBotMessage", false);
                            put("AFKMessages", new LinkedHashMap<String, Object>() {
                                {
                                    put("Enabled", true);
                                    put("SecondsTilAFK", 60+60);
                                    put("Discord", new LinkedHashMap<String, Object>() {
                                        {
                                            put("AFK", "`%pName% is now AFK`");
                                            put("UnAFK", "`%pName% is no longer AFK`");
                                        }
                                    });
                                    put("InGame", new LinkedHashMap<String, Object>() {
                                        {
                                            put("ToSelf", new LinkedHashMap<String, Object>() {
                                                {
                                                    put("AFK", "&eYou are now AFK");
                                                    put("UnAFK", "&aWelcome back %pName%! &fYou were afk for %h% minutes, %m% minutes and %s% seconds.");
                                                }
                                            });
                                            put("ToOthers", new LinkedHashMap<String, Object>() {
                                                {
                                                    put("AFK", "&7%pName% is now AFK");
                                                    put("UnAFK", "&7%pName% is no longer AFK");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
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
                            put("Message", "**Server Was stopped (Normally)**");
                        }
                    });
                    put("ForceEndMessage", new LinkedHashMap<String, Object>() {
                        {
                            put("Enabled", true);
                            put("Message", "**Server was stopped (Crashed or forced shutdown)**");
                        }
                    });
                }
            });
        }
    };
}
