# DiscordBot
###### A reliable and efficient DiscordBot Plugin for Minecraft Beta 1.7.3 with many settings and configurations to suit your needs. Perfect for small, or big communities and servers!

# Features:
1. **Set custom bot statuses and setup the bot with ease**
2. **Customize the way each message looks and feels**
3. **Make your commands yours, each command with it's own custom description and response**
4. **Enable or disable any message of your choice with simple and easy to read config files**
5. **Customize message with rich embeds, making your messages yours**

# Installation
Download a jar from the [releases](https://github.com/cutezyash/DiscordBot/releases) tab of this Github page, install to your /plugins/ folder. Once installed, start the server and the configuration for the bot will generate. The Configuration for the bot will generate at /plugins/DiscordBot/Discord.yml. Simply edit it and restart the server to apply your changes.

### Token & WebhookURL?
A "token" is basically the key for your bot. It will let your server take control of your bot. You can create your own bot and find your own token [here](https://discord.com/developers/applications/). Once created invite your bot to your own server with the following URL, replacing "IDHERE" with the Client ID of your new application, found in the "General Information" tab of the application <br/>
https://discordapp.com/oauth2/authorize?client_id=IDHERE&scope=bot&permissions=8

Once you've invited the bot to your server, go to the "Bot" section of your new application, underneeth the bot's name, copy the Bot's token to your config. Sweet! The difficult part is over. Now go to your server, pick a channel of your choice and open it's settings. Head over to the "Integrations" tab, and create a new Webhook. Copy it's URL to the "WebhookURL" section of the config.

### ChannelID and GuildID?
Check out [this link](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-). It will show you the ropes of how to use developer mode in Discord to get your GuildID (known as a server ID in this article). You can also get your ChannelID by right clicking the channel you put your webhook on and pressing "Copy ID", just like the GuildID!

#### You're done! 
If you've read all of this, I applaud you, you've now setup your very own Discord bot in your MC server (unless you skipped to the bottom), now you can customize your config file the way you want to!
