package me.hsgamer.bettergui.switchicon;

import me.hsgamer.bettergui.builder.IconBuilder;
import me.hsgamer.bettergui.object.addon.Addon;

public final class Main extends Addon {

  @Override
  public void onEnable() {
    IconBuilder.register("switch", SwitchIcon.class);
  }
}
