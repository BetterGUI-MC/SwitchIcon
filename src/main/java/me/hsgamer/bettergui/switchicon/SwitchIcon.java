package me.hsgamer.bettergui.switchicon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import me.hsgamer.bettergui.lib.xseries.XMaterial;
import me.hsgamer.bettergui.object.ClickableItem;
import me.hsgamer.bettergui.object.Icon;
import me.hsgamer.bettergui.object.Menu;
import me.hsgamer.bettergui.object.ParentIcon;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SwitchIcon extends Icon implements ParentIcon {

  private final List<Icon> icons = new ArrayList<>();
  private Map<UUID, Integer> currentIndexMap = new HashMap<>();
  private final Consumer<InventoryClickEvent> nextIndexConsumer = e -> currentIndexMap
      .computeIfPresent(e.getWhoClicked().getUniqueId(), (uuid1, integer) -> {
        if (integer + 1 >= icons.size()) {
          return 0;
        }
        return integer + 1;
      });

  public SwitchIcon(String name, Menu<?> menu) {
    super(name, menu);
  }

  public SwitchIcon(Icon original) {
    super(original);
    if (original instanceof SwitchIcon) {
      icons.addAll(((SwitchIcon) original).icons);
      currentIndexMap = ((SwitchIcon) original).currentIndexMap;
    }
  }

  public Map<UUID, Integer> getData() {
    return currentIndexMap;
  }

  private void loadData() {
    currentIndexMap.putAll(Main.getManager().getData(getMenu().getName(), getName()));
  }

  @Override
  public void setFromSection(ConfigurationSection configurationSection) {
    setChildFromSection(getMenu(), configurationSection);
    Main.getManager().registerIcon(this);
    loadData();
  }

  @Override
  public Optional<ClickableItem> createClickableItem(Player player) {
    UUID uuid = player.getUniqueId();
    currentIndexMap.putIfAbsent(uuid, 0);

    Optional<ClickableItem> optional = icons.get(currentIndexMap.get(uuid))
        .createClickableItem(player);
    if (optional.isPresent()) {
      ClickableItem clickableItem = optional.get();
      return Optional.of(new ClickableItem(clickableItem.getItem(),
          clickableItem.getClickEvent().andThen(nextIndexConsumer)));
    }

    return Optional.of(new ClickableItem(XMaterial.AIR.parseItem(), nextIndexConsumer));
  }

  @Override
  public Optional<ClickableItem> updateClickableItem(Player player) {
    UUID uuid = player.getUniqueId();

    Optional<ClickableItem> optional = icons.get(currentIndexMap.get(uuid))
        .updateClickableItem(player);
    if (optional.isPresent()) {
      ClickableItem clickableItem = optional.get();
      return Optional.of(new ClickableItem(clickableItem.getItem(),
          clickableItem.getClickEvent().andThen(nextIndexConsumer)));
    }

    return Optional.of(new ClickableItem(XMaterial.AIR.parseItem(), nextIndexConsumer));
  }

  @Override
  public void addChild(Icon icon) {
    icons.add(icon);
  }

  @Override
  public List<Icon> getChild() {
    return icons;
  }
}
