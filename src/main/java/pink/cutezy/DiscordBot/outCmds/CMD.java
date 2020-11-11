package pink.cutezy.DiscordBot.outCmds;

import net.dv8tion.jda.api.entities.Message;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CMD {
    void Invoke(@Nonnull final Message msg, @Nullable String[] args);
    @Nonnull String getDesc();
}
