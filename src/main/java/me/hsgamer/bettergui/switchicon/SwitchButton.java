package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.button.WrappedButton;
import me.hsgamer.bettergui.api.menu.Menu;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.collections.map.CaseInsensitiveStringHashMap;
import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.minecraft.gui.button.Button;
import me.hsgamer.hscore.minecraft.gui.button.DisplayButton;
import me.hsgamer.hscore.minecraft.gui.event.ViewerEvent;
import me.hsgamer.hscore.ui.property.Initializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

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
        data.getNormalizedValues(false, hash)
                .forEach((k, v) -> currentIndexMap.put(UUID.fromString(k[0]), Integer.parseInt(String.valueOf(v))));
    }

    private void saveData() {
        Config config = Manager.get(menu);
        String hash = String.valueOf(name.hashCode());
        config.remove(hash);
        currentIndexMap.forEach((uuid, integer) -> config.set(integer, hash, uuid.toString()));
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
    public void init() {
        loadData();
        buttons.forEach(Initializable::init);
    }

    @Override
    public void stop() {
        saveData();
        buttons.forEach(Initializable::stop);
    }

    @Override
    public @Nullable DisplayButton display(@NotNull UUID uuid) {
        DisplayButton displayButton = new DisplayButton();
        displayButton.apply(buttons.get(currentIndexMap.computeIfAbsent(uuid, uuid1 -> 0)).display(uuid));
        Consumer<ViewerEvent> action = displayButton.getAction();
        displayButton.setClickAction(event -> {
            if (action != null) {
                action.accept(event);
            }
            currentIndexMap.computeIfPresent(uuid, (uuid1, integer) -> (integer + 1) % buttons.size());
        });
        return displayButton;
    }
}
