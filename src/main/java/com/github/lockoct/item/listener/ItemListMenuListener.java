package com.github.lockoct.item.listener;

import com.github.lockoct.Main;
import com.github.lockoct.item.menu.ItemListMenu;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemListMenuListener extends BaseMenuListener {
    public ItemListMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            ItemListMenu menu = (ItemListMenu) getMenu();
            if (is != null) {
                String sign = menu.getOperationItemPos().get(e.getRawSlot());
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "exit" -> menu.close();
                    case "nextPage" -> menu.setCurrentPage(menu.getCurrentPage() + 1);
                    case "prePage" -> menu.setCurrentPage(menu.getCurrentPage() - 1);
                    case "pageInfo" -> {
                    }
                    default -> {
                        if (!checkItemLoadError(is)) {
                            menu.toNextMenu(e.getRawSlot());
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkItemLoadError(ItemStack is) {
        ItemMeta im = is.getItemMeta();
        assert im != null;

        NamespacedKey namespacedKey = new NamespacedKey(Main.plugin, "loadErr");
        PersistentDataContainer pdc = im.getPersistentDataContainer();
        Integer res = pdc.get(namespacedKey, PersistentDataType.INTEGER);
        return res != null;
    }
}
