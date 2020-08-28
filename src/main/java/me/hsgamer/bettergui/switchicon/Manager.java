package me.hsgamer.bettergui.switchicon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.hsgamer.bettergui.util.config.PluginConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Manager {

  private final Main instance;
  private final Map<String, PluginConfig> configMap = new HashMap<>();
  private final List<SwitchIcon> icons = new ArrayList<>();

  public Manager(Main main) {
    this.instance = main;
    loadFile();
  }

  public void registerIcon(SwitchIcon icon) {
    icons.add(icon);
  }

  public void loadFile() {
    File folder = instance.getDataFolder();
    for (File file : Objects.requireNonNull(folder.listFiles())) {
      configMap.put(file.getName(), new PluginConfig(instance.getPlugin(), file));
    }
  }

  public void reloadFile() {
    saveData();
    configMap.clear();
    icons.clear();
    loadFile();
  }

  public Map<UUID, Integer> getData(String menu, String icon) {
    Map<UUID, Integer> uuidMap = new HashMap<>();
    if (configMap.containsKey(menu)) {
      FileConfiguration data = configMap.get(menu).getConfig();
      if (data.isConfigurationSection(icon)) {
        ConfigurationSection section = data.getConfigurationSection(icon);
        section.getKeys(false).forEach(s -> uuidMap.put(UUID.fromString(s), section.getInt(s)));
      }
    }
    return uuidMap;
  }

  public void saveData() {
    icons.forEach(switchIcon -> {
      String menu = switchIcon.getMenu().getName();
      configMap.computeIfAbsent(menu,
          s -> new PluginConfig(instance.getPlugin(), new File(instance.getDataFolder(), menu)));
      PluginConfig config = configMap.get(menu);
      String icon = switchIcon.getName();
      switchIcon.getData().forEach(
          (uuid, integer) -> config.getConfig().set(icon + "." + uuid.toString(), integer));
      config.saveConfig();
    });
  }
}
