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
    private static JavaPlugin plugin;
    public static void initialize(JavaPlugin javaPlugin) {
        plugin = javaPlugin;
    }

    private static Map<String, Configuration> configDict = new HashMap<>();
    public static Configuration getConfigByName(String config) {
        config = config + ".yml";
        if(configDict.containsKey(config))
            return configDict.get(config);
        File file = new File(plugin.getDataFolder(), config);
        Configuration c = new Configuration(file);
        c.load();
        configDict.put(config, c);
        return c;
    }
    public static Configuration getPlayerConfigMod(String mod, String playerName) {
        return getConfigByName(mod+"/"+playerName);
    }

    public static Object get(Configuration c, String object) {
        return c.getProperty(object);
    }

    public static void addDefault(Configuration c, String object, Object value) {
        if(c.getProperty(object) == null)
            addTo(c, object, value);
    }

    public static void addTo(Configuration c, String object, Object value) {
        c.setProperty(object, value);
        c.save();
    }

    public static boolean contains(Configuration c, String object) {
        Object s = c.getProperty(object);
        return s != null;
    }

    public static void remove(Configuration c, String object) {
        c.removeProperty(object);
        c.save();
    }
}
