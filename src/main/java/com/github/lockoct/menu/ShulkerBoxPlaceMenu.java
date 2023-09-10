package com.github.lockoct.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.ShulkerBoxPlaceMenuData;
import com.github.lockoct.item.listener.KeyboardMenuListener;
import com.github.lockoct.item.task.SendBoxTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;

public class ShulkerBoxPlaceMenu extends BaseMenu {
    private final HashMap<Integer, ShulkerBoxPlaceMenuData> shulkerBoxMap = new HashMap<>();
    private final int emptySlot;

    public ShulkerBoxPlaceMenu(String title, Player player, HashMap<String, Object> menuContext) {
        super(54, title, menuContext, player, Main.plugin);
        this.emptySlot = (int) menuContext.get("boxCount");
        // 设置操作按钮
        this.setOptItem(Material.RED_CONCRETE, "取消", 47, "cancel");
        this.setOptItem(Material.ARROW, "返回", 49, "back");
        this.setOptItem(Material.LIME_CONCRETE, "确认", 51, "confirm");
        // 设置背景
        this.setBackGround(Material.BLUE_STAINED_GLASS_PANE);
        // 设置空位
        for (int i = 0; i < this.emptySlot; i++) {
            this.getInventory().clear(i);
        }
    }

    public HashMap<Integer, ShulkerBoxPlaceMenuData> getShulkerBoxMap() {
        return shulkerBoxMap;
    }

    public int getEmptySlot() {
        return emptySlot;
    }

    public void confirm() {
        int boxCount = this.getShulkerBoxMap().size();
        if (boxCount == 0) {
            return;
        }

        for (int i = 0; i < this.emptySlot; i++) {
            ItemStack is = this.getInventory().getItem(i);
            if (is != null) {
                BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
                assert bsm != null;
                ShulkerBox box = (ShulkerBox) bsm.getBlockState();
                Inventory boxInv = box.getInventory();
                // 检查潜影箱是否为空
                if (!boxInv.isEmpty()) {
                    this.getPlayer().sendMessage(ChatColor.RED + "用于取货的潜影盒必须为空潜影盒");
                    this.close();
                    return;
                }
            }
        }

        new SendBoxTask(this.getPlayer(), (Item) this.getMenuContext().get("itemInfo"), this.shulkerBoxMap, boxCount).runTaskAsynchronously(Main.plugin);
        this.close();
    }

    public void back() {
        KeyboardMenu menu = new KeyboardMenu("数量选择", this.getPlayer(), this.getMenuContext());
        this.close();
        menu.open(new KeyboardMenuListener(menu));
    }
}
