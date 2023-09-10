package com.github.lockoct.item.listener;

import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.ItemListMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListMenuListener extends BaseMenuListener {
    public ItemListMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            ItemListMenu menu = (ItemListMenu) this.getMenu();
            if (is != null) {
                String sign = menu.getOperationItemPos().get(e.getRawSlot());
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "exit" -> menu.close();
                    case "nextPage" -> menu.setCurrentPage(menu.getCurrentPage() + 1);
                    case "prePage" -> menu.setCurrentPage(menu.getCurrentPage() - 1);
                    case "pageInfo" -> {
                    }
                    default -> menu.toKeyboardMenu(e.getRawSlot());
                }
            }
            return true;
        }
        return false;
    }
}
