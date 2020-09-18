package pink.cutezy.PluginCore;

/**
 * 2019-2020
 * Do not redistribute without crediting me
 * @author cutezyash
 */
public class PluginUtils {
    public static String colorize(String msg) {
        return msg.replaceAll("&([a-z0-9])","§$1");
    }
}
