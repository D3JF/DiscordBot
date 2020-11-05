package pink.cutezy.PluginCore;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 2019-2020
 * Do not redistribute without crediting me
 * @author cutezyash
 */
public class ModConfig {
    private JavaPlugin plugin;
    public static ModConfig initialize(JavaPlugin javaPlugin) {
        return new ModConfig(javaPlugin);
    }

    ModConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private Map<String, Configuration> configDict = new HashMap<>();
    public Configuration getConfigByName(String config) {
        config = config + ".yml";
        if(configDict.containsKey(config))
            return configDict.get(config);
        File file = new File(plugin.getDataFolder(), config);
        Configuration c = new Configuration(file);
        c.load();
        configDict.put(config, c);
        return c;
    }
    public Configuration getPlayerConfigMod(String modifier, String playerName) {
        return getConfigByName(modifier+"/"+playerName);
    }

    public Object get(Configuration c, String object) {
        return c.getProperty(object);
    }
    public <T> T getOrDefault(Configuration c, String object, T defaul, Class<T> type) {
        T ret = type.cast(c.getProperty(object));
        System.out.println(ret);
        if(ret == null) {
            addTo(c, object, defaul);
            return type.cast(defaul);
        }
        return ret;
    }

    public void addDefault(Configuration c, String object, Object value) {
        if(c.getProperty(object) == null)
            addTo(c, object, value);
    }

    public void addTo(Configuration c, String object, Object value) {
        c.setProperty(object, value);
        c.save();
    }

    public boolean contains(Configuration c, String object) {
        Object s = c.getProperty(object);
        return s != null;
    }

    public void remove(Configuration c, String object) {
        c.removeProperty(object);
        c.save();
    }
}
