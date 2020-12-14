package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.addon.BetterGUIAddon;
import me.hsgamer.bettergui.builder.ButtonBuilder;

public final class Main extends BetterGUIAddon {

    @Override
    public boolean onLoad() {
        Manager.setFolder(getDataFolder());
        return true;
    }

    @Override
    public void onEnable() {
        ButtonBuilder.INSTANCE.register(SwitchButton::new, "switch");
        Manager.load();
    }

    @Override
    public void onReload() {
        Manager.clear();
        Manager.load();
    }

    @Override
    public void onDisable() {
        Manager.clear();
    }
}
