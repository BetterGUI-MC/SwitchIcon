package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.event.ClickEvent;
import me.hsgamer.hscore.minecraft.gui.object.Item;
import me.hsgamer.hscore.ui.property.Initializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SwitchButton implements WrappedButton {
    private final List<Button> buttons = new ArrayList<>();
    private final Map<UUID, Integer> currentIndexMap = new HashMap<>();
    private final Menu menu;
    private final String name;

    public SwitchButton(ButtonBuilder.Input input) {
        this.menu = input.menu;
        this.name = input.name;

        Map<String, Object> keys = new CaseInsensitiveStringHashMap<>(input.options);
        Optional.ofNullable(keys.get("child"))
                .flatMap(MapUtils::castOptionalStringObjectMap)
                .map(o -> ButtonBuilder.INSTANCE.getChildButtons(this, o))
                .ifPresent(buttons::addAll);
    }

    private void loadData() {
        Config data = Manager.get(menu);
        String hash = String.valueOf(name.hashCode());
        data.getNormalizedValues(new PathString(hash), false)
                .forEach((k, v) -> currentIndexMap.put(UUID.fromString(PathString.toPath(k)), Integer.parseInt(String.valueOf(v))));
    }

    private void saveData() {
        Config config = Manager.get(menu);
        String hash = String.valueOf(name.hashCode());
        config.remove(new PathString(hash));
        currentIndexMap.forEach((uuid, integer) -> config.set(new PathString(hash, uuid.toString()), integer));
        config.save();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public Item getItem(@NotNull UUID uuid) {
        currentIndexMap.putIfAbsent(uuid, 0);
        return buttons.get(currentIndexMap.get(uuid)).getItem(uuid);
    }

    @Override
    public void handleAction(ClickEvent event) {
        UUID uuid = event.getViewerID();
        currentIndexMap.putIfAbsent(uuid, 0);
        buttons.get(currentIndexMap.get(uuid)).handleAction(event);
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
