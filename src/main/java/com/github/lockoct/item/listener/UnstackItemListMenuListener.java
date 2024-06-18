package com.github.lockoct.item.listener;

import com.github.lockoct.item.menu.UnstackItemListMenu;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class UnstackItemListMenuListener extends BaseMenuListener {

    public UnstackItemListMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            UnstackItemListMenu menu = (UnstackItemListMenu) getMenu();
            if (is != null) {
                int currentPos = e.getRawSlot();
                String sign = menu.getOperationItemPos().get(e.getRawSlot());
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "exit" -> menu.close();
                    case "nextPage" -> menu.setCurrentPage(menu.getCurrentPage() + 1);
                    case "prePage" -> menu.setCurrentPage(menu.getCurrentPage() - 1);
                    case "back" -> menu.back();
                    case "pageInfo" -> {
                    }
                    case "normal" -> menu.toKeyboardMenu();
                    default -> {
                        // 当点击的物品是存储的物品时，直接放入背包中
                        if (currentPos < 45) {
                            menu.getSelectedItems().add(menu.getItems().get(currentPos));
                            menu.confirm();
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
