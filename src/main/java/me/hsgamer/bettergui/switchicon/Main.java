package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.builder.ButtonBuilder;
import me.hsgamer.hscore.bukkit.addon.PluginAddon;

public final class Main extends PluginAddon {

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
