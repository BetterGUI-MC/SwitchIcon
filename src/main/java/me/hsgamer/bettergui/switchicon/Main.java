package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.api.addon.Reloadable;
import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.expansion.common.Expansion;
import me.hsgamer.hscore.expansion.extra.expansion.DataFolder;

public final class Main implements Expansion, Reloadable, DataFolder {

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
