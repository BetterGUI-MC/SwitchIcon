package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Manager {

    private static final Map<String, Config> configMap = new HashMap<>();
    private static File folder;

    private Manager() {
        // EMPTY
    }

    public static void setFolder(File folder) {
        Manager.folder = folder;
    }

    public static void load() {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            Config config = new BukkitConfig(file);
            config.setup();
            configMap.put(file.getName(), config);
        }
    }

    public static void clear() {
        configMap.clear();
    }

    public static Config get(Menu menu) {
        return configMap.computeIfAbsent(menu.getName(), s -> new BukkitConfig(new File(folder, s)));
    }
}
