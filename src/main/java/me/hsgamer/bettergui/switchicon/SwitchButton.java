package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.bettergui.lib.core.bukkit.gui.button.Button;
import me.hsgamer.bettergui.lib.core.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.bettergui.lib.core.config.Config;
import me.hsgamer.bettergui.lib.core.ui.property.Initializable;
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

    private void loadData() {
        Config data = Manager.get(menu);
        String hash = String.valueOf(name.hashCode());
        data.getNormalizedValues(hash, false)
                .forEach((k, v) -> currentIndexMap.put(UUID.fromString(k), Integer.parseInt(String.valueOf(v))));
    }

    private void saveData() {
        Config config = Manager.get(menu);
        String hash = String.valueOf(name.hashCode());
        config.remove(hash);
        currentIndexMap.forEach((uuid, integer) -> config.set(hash + "." + uuid.toString(), integer));
        config.save();
    }

    @Override
    public void setFromSection(Map<String, Object> map) {
        Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(map);
        Optional.ofNullable(keys.get("child"))
                .filter(Map.class::isInstance)
                .map(o -> (Map<String, Object>) o)
                .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, o))
                .ifPresent(buttons::addAll);
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
        currentIndexMap.computeIfPresent(uuid, (uuid1, integer) -> (integer + 1) % buttons.size());
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
