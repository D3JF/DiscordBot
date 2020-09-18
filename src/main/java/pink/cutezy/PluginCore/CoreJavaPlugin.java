package pink.cutezy.PluginCore;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * 2019-2020
 * Do not redistribute without crediting me
 * @author cutezyash
 */
public abstract class CoreJavaPlugin extends JavaPlugin {
    public CoreJavaPlugin() {
        this.instList = new ArrayList<>();
        ModConfig.initialize(this);
    }

    /** INSTANCED PLUGIN STYLES **/
    private ArrayList<MainClass> instList;
    public static class MainClass {
        private boolean isCommand;
        private List<String> commands;
        private boolean isEvent;
        private List<Event.Type> eventType;
        private Event.Priority priority = Event.Priority.Normal;
        private final Class<?> clazz;
        private MainClass(Class<?> clazz) {
            this.clazz = clazz;
        }
        public MainClass cmd(String command) {
            if(commands == null)
                commands = new ArrayList<>();
            commands.add(command);
            isCommand = true;
            return this;
        }
        public MainClass event(Event.Type type) {
            if(eventType == null)
                eventType = new ArrayList<>();
            eventType.add(type);
            isEvent = true;
            return this;
        }
        public MainClass prio(Event.Priority priority) {
            this.priority = priority;
            return this;
        }
    }
    protected MainClass newInst(Class<? extends Listener> clazz) {
        MainClass mainClass = new MainClass(clazz);
        this.instList.add(mainClass);
        return mainClass;
    }
    /** END INSTANCE **/

    public abstract void onDisable();
    public void onEnable() {
        instList.forEach(MainClass -> {
            try {
                Object listener = MainClass.clazz.newInstance();
                if(MainClass.isEvent) {
                    for (Event.Type type : MainClass.eventType) {
                        getServer().getPluginManager().registerEvent(type, (Listener) listener, MainClass.priority, this);
                    }
                }
                if(MainClass.isCommand) {
                    for(String cmd : MainClass.commands) {
                        getCommand(cmd).setExecutor((CommandExecutor) listener);
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
