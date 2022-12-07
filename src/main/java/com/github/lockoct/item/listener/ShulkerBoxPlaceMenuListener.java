package com.github.lockoct.item.listener;

import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShulkerBoxPlaceMenuListener extends BaseMenuListener {
    public ShulkerBoxPlaceMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        return super.onClick(e);
    }
}
