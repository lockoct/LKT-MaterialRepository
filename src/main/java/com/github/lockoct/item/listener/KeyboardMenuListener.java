package com.github.lockoct.item.listener;

import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.KeyboardMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KeyboardMenuListener extends BaseMenuListener {
    public KeyboardMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            KeyboardMenu menu = (KeyboardMenu) this.getMenu();
            if (is != null) {
                String sign = menu.getOperationItemPos().get(e.getRawSlot());
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "exit" -> menu.close();
                    case "back" -> menu.back();
                    case "single" -> menu.setMode(1);
                    case "group" -> menu.setMode(2);
                    case "box" -> menu.setMode(3);
                    case "clear" -> menu.clear();
                    case "delete" -> menu.setCalcResult(menu.getCalcResult()/10);
                    case "confirm" -> menu.confirm();
                    default -> {
                        if (!sign.equals("")) {
                            String calcTmp = "" + menu.getCalcResult() + sign;
                            menu.setCalcResult(Integer.parseInt(calcTmp));
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
