package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.lib.core.bukkit.config.PluginConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Manager {

    private static final Map<String, PluginConfig> configMap = new HashMap<>();
    private static File folder;

    public static void setFolder(File folder) {
        Manager.folder = folder;
    }

    public static void load() {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            configMap.put(file.getName(), new PluginConfig(file));
        }
    }

    public static void clear() {
        configMap.clear();
    }

    public static PluginConfig get(Menu menu) {
        return configMap.computeIfAbsent(menu.getName(), s -> new PluginConfig(new File(folder, s)));
    }

    private Manager() {
        // EMPTY
    }
}
