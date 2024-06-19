package com.github.lockoct.item.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.ShulkerBoxPlaceMenuData;
import com.github.lockoct.item.listener.KeyboardMenuListener;
import com.github.lockoct.item.task.SendBoxTask;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.utils.I18nUtil;
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

    public ShulkerBoxPlaceMenu(HashMap<String, Object> menuContext, Player player) {
        this(I18nUtil.getText(Main.plugin, player, "shulkerBoxPlaceMenu.title"), menuContext, player);
    }

    public ShulkerBoxPlaceMenu(String title, HashMap<String, Object> menuContext, Player player) {
        super(54, title, menuContext, player, Main.plugin);
        emptySlot = (int) menuContext.get("boxCount");
        // 设置操作按钮
        setOptItem(Material.RED_CONCRETE, I18nUtil.getText(Main.plugin, player, "shulkerBoxPlaceMenu.btn.cancel"), 47, "cancel");
        setOptItem(Material.ARROW, I18nUtil.getCommonText(player, "menu.back"), 49, "back");
        setOptItem(Material.LIME_CONCRETE, I18nUtil.getText(Main.plugin, player, "shulkerBoxPlaceMenu.btn.confirm"), 51, "confirm");
        // 设置背景
        setBackGround(Material.BLUE_STAINED_GLASS_PANE);
        // 设置空位
        for (int i = 0; i < emptySlot; i++) {
            getInventory().clear(i);
        }
    }

    public HashMap<Integer, ShulkerBoxPlaceMenuData> getShulkerBoxMap() {
        return shulkerBoxMap;
    }

    public int getEmptySlot() {
        return emptySlot;
    }

    public void confirm() {
        Player player = getPlayer();
        int boxCount = getShulkerBoxMap().size();
        if (boxCount == 0) {
            return;
        }

        for (int i = 0; i < emptySlot; i++) {
            ItemStack is = getInventory().getItem(i);
            if (is != null) {
                BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
                assert bsm != null;
                ShulkerBox box = (ShulkerBox) bsm.getBlockState();
                Inventory boxInv = box.getInventory();
                // 检查潜影箱是否为空
                if (!boxInv.isEmpty()) {
                    player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "shulkerBoxPlaceMenu.notEmptyBox"));
                    close();
                    return;
                }
            }
        }

        new SendBoxTask(player, (Item) getMenuContext().get("itemInfo"), shulkerBoxMap, boxCount).runTaskAsynchronously(Main.plugin);
        close();
    }

    public void back() {
        KeyboardMenu menu = new KeyboardMenu(getMenuContext(), getPlayer());
        close();
        menu.open(new KeyboardMenuListener(menu));
    }
}
