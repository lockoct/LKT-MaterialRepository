package com.github.lockoct.area.listener;

import com.github.lockoct.menu.AreaManageMenu;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AreaManageMenuListener extends BaseMenuListener {
    public AreaManageMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            AreaManageMenu menu = (AreaManageMenu) this.getMenu();
            if (is != null) {
                String sign = menu.getOperationItemPos().get(e.getRawSlot());
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "exit" -> menu.close();
                    case "back" -> menu.back();
                    case "delete" -> menu.delete();
                    case "enable" -> menu.enable();
                    case "reload" -> menu.reload();
                }
            }
            return true;
        }
        return false;
    }
}
