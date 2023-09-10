package com.github.lockoct.item.listener;

import com.github.lockoct.entity.ShulkerBoxPlaceMenuData;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.ShulkerBoxPlaceMenu;
import com.github.lockoct.menu.listener.BaseMenuListener;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class ShulkerBoxPlaceMenuListener extends BaseMenuListener {
    // 点击位置与潜影盒放置位置哈希的关联
    private final HashMap<Integer, Integer> clickPosHashMap = new HashMap<>();

    public ShulkerBoxPlaceMenuListener(BaseMenu menu) {
        super(menu);
    }

    @Override
    @EventHandler
    public boolean onClick(InventoryClickEvent e) {
        if (super.onClick(e)) {
            ItemStack is = e.getCurrentItem();
            ShulkerBoxPlaceMenu menu = (ShulkerBoxPlaceMenu) this.getMenu();
            if (is != null) {
                int currentPos = e.getRawSlot();

                String sign = menu.getOperationItemPos().get(currentPos);
                sign = sign == null ? "" : sign;
                switch (sign) {
                    case "cancel" -> menu.close();
                    case "back" -> menu.back();
                    case "confirm" -> menu.confirm();
                }

                if (is.getType().toString().contains("SHULKER_BOX")) {
                    HashMap<Integer, ShulkerBoxPlaceMenuData> map = menu.getShulkerBoxMap();
                    Inventory inv = e.getInventory();

                    // 不可以用hashCode查找标记的潜影盒，两个相同类型的潜影盒的hashCode也相同
                    // ShulkerBoxPlaceMenuData data = map.get(is.hashCode());

                    // 获取点击位置对应的潜影盒放置位置哈希
                    Integer code = clickPosHashMap.get(currentPos);
                    // 再用哈希找到潜影盒放置位置
                    ShulkerBoxPlaceMenuData data = map.get(code);

                    // 潜影盒已被标记，data不为null，需要取消标记
                    if (data != null) {
                        // 去除潜影盒放置位置
                        map.remove(code);
                        // 去除点击位置和潜影盒放置位置哈希的关系
                        int tmpCode = code;
                        clickPosHashMap.values().removeIf(v -> v.equals(tmpCode));
                        // 菜单去除潜影盒标记
                        inv.clear(data.getToPos());
                        // 玩家背包潜影盒去除附魔标记
                        PlayerInventory playerInv = menu.getPlayer().getInventory();
                        is = playerInv.getItem(data.getFromPos());
                        if (is != null) {
                            is.removeEnchantment(Enchantment.MENDING);
                        }
                    } else {
                        if (map.size() < menu.getEmptySlot()) {
                            data = new ShulkerBoxPlaceMenuData();
                            data.setFromPos(e.getSlot());
                            data.setToPos(inv.firstEmpty());
                            // 给玩家背包潜影盒设置附魔标记
                            ItemMeta im = is.getItemMeta();
                            assert im != null;
                            im.addEnchant(Enchantment.MENDING, 1, true);
                            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            is.setItemMeta(im);
                            // 给菜单添加标记的潜影盒
                            inv.setItem(data.getToPos(), is);

                            code = data.hashCode();
                            map.put(code, data);
                            // 建立点击位置和潜影盒放置位置哈希的关系，分别是玩家物品栏点击的位置和出现在菜单中的位置
                            // 使玩家无论点击背包里的潜影盒或是菜单中的潜影盒都能指向同一个潜影盒放置位置信息
                            clickPosHashMap.put(currentPos, code);
                            clickPosHashMap.put(data.getToPos(), code);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @EventHandler
    public boolean onClose(InventoryCloseEvent e) {
        boolean validate = this.getMenu().getPlayer().equals(e.getPlayer()) && this.getMenu().getInventory().equals(e.getInventory());
        if (validate) {
            PlayerInventory playerInv = e.getPlayer().getInventory();
            // 清除所有物品附魔
            for (int i = 0; i < 36; i++) {
                ItemStack is = playerInv.getItem(i);
                if (is != null && is.getType().toString().contains("SHULKER_BOX")) {
                    is.removeEnchantment(Enchantment.MENDING);
                }
            }
            HandlerList.unregisterAll(this);
        }
        return validate;
    }
}
