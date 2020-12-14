package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.lib.core.bukkit.config.PluginConfig;
import me.hsgamer.bettergui.lib.core.bukkit.gui.Button;
import me.hsgamer.bettergui.lib.core.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.bettergui.lib.core.ui.property.Initializable;
import me.hsgamer.bettergui.lib.simpleyaml.configuration.ConfigurationSection;
import me.hsgamer.bettergui.lib.simpleyaml.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SwitchButton implements WrappedButton {
    private final List<Button> buttons = new ArrayList<>();
    private final Map<UUID, Integer> currentIndexMap = new HashMap<>();
    private final Menu menu;
    private String name;

    public SwitchButton(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void setFromSection(ConfigurationSection configurationSection) {
        Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(configurationSection.getValues(false));
        Optional.ofNullable(keys.get("child"))
                .filter(o -> o instanceof ConfigurationSection)
                .map(o -> (ConfigurationSection) o)
                .ifPresent(subsection -> buttons.addAll(ButtonBuilder.INSTANCE.getChildButtons(this, subsection)));
    }

    private void loadData() {
        Optional.ofNullable(Manager.get(menu))
                .ifPresent(config -> {
                    FileConfiguration data = config.getConfig();
                    if (data.isConfigurationSection(name)) {
                        ConfigurationSection section = data.getConfigurationSection(name);
                        section.getKeys(false).forEach(s -> currentIndexMap.put(UUID.fromString(s), section.getInt(s)));
                    }
                });
    }

    private void saveData() {
        PluginConfig config = Manager.get(menu);
        currentIndexMap.forEach((uuid, integer) -> config.getConfig().set(name + "." + uuid.toString(), integer));
        config.saveConfig();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public ItemStack getItemStack(UUID uuid) {
        currentIndexMap.putIfAbsent(uuid, 0);
        return buttons.get(currentIndexMap.get(uuid)).getItemStack(uuid);
    }

    @Override
    public void handleAction(UUID uuid, InventoryClickEvent inventoryClickEvent) {
        currentIndexMap.putIfAbsent(uuid, 0);
        buttons.get(currentIndexMap.get(uuid)).handleAction(uuid, inventoryClickEvent);
        currentIndexMap.computeIfPresent(uuid, (uuid1, integer) -> integer + 1 % buttons.size());
    }

    @Override
    public void init() {
        loadData();
        buttons.forEach(Initializable::init);
    }

    @Override
    public void stop() {
        saveData();
        buttons.forEach(Initializable::stop);
    }
}
