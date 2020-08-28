package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.object.addon.Addon;

public final class Main extends Addon {

  private static Manager manager;

  public static Manager getManager() {
    return manager;
  }

  @Override
  public void onEnable() {
    IconBuilder.register(SwitchIcon::new, "switch");
    manager = new Manager(this);
  }

  @Override
  public void onReload() {
    manager.reloadFile();
  }

  @Override
  public void onDisable() {
    manager.saveData();
  }
}
