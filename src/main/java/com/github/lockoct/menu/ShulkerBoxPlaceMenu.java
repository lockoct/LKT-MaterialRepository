package com.github.lockoct.menu;

import com.github.lockoct.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShulkerBoxPlaceMenu extends BaseMenu{
    public ShulkerBoxPlaceMenu(String title, Player player) {
        super(45, title, player, Main.plugin);
        this.setOptItem(Material.RED_CONCRETE, "取消", 29, "cancel");
        this.setOptItem(Material.LIME_CONCRETE,"确认", 33, "confirm");
        this.setOptItem(Material.ARROW,"返回", 40, "confirm");
        this.setBackGround(Material.BLUE_STAINED_GLASS_PANE);
        // 清除出用于放置潜影盒的位置
        this.getInventory().clear(13);
    }
}
