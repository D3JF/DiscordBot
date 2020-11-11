package pink.cutezy.DiscordBot.framework;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class AFKPlayer {
    private boolean shouldUnAFK = false;
    public boolean isAFK = false;
    private Location lastLocation = null;
    public long lastActiveMilli = -1L;
    public AFKPlayer(Player player) {
        this.lastLocation = player.getLocation();
        this.updateTime();
    }

    private long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }
    public void updateTime() {
        this.lastActiveMilli = getTime();
    }
    public long getSubMilli() {
        long curTime = getTime();
        return curTime - this.lastActiveMilli;
    }
}