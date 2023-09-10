package com.github.lockoct.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.item.listener.ItemListMenuListener;
import com.github.lockoct.item.listener.ShulkerBoxPlaceMenuListener;
import com.github.lockoct.item.task.SendItemTask;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class KeyboardMenu extends BaseMenu {
    private final String CALC_RESULT_PREFIX = "拿取物品的数量为：";
    private final int[] numKeyPos = new int[]{38, 10, 11, 12, 19, 20, 21, 28, 29, 30};
    private final int[] modePos = new int[]{14, 15, 16};
    private int mode = 1;
    private int calcResult = 0;

    public KeyboardMenu(String title, Player player, HashMap<String, Object> menuContext) {
        super(54, title, menuContext, player, Main.plugin);
        this.setKeyboard();
        this.setModeItem();
        this.setOptItem(Material.ARROW, "返回", 48, "back");
        this.setOptItem(Material.DARK_OAK_DOOR, "退出", 50, "exit");
        this.setBackGround(Material.BLUE_STAINED_GLASS_PANE);
    }

    private void setKeyboard() {
        // 数字键
        for (int i = 0; i < numKeyPos.length; i++) {
            ItemStack is = this.setOptItem(Material.LIGHT, "" + i, numKeyPos[i], "" + i);
            ItemMeta im = is.getItemMeta();
            assert im != null;
            BlockData data = Material.LIGHT.createBlockData();
            ((Light) data).setLevel(i);
            ((BlockDataMeta) im).setBlockData(data);
            is.setItemMeta(im);
            this.getInventory().setItem(numKeyPos[i], is);
        }

        // 计算结果告示牌
        this.setOptItem(Material.OAK_SIGN, CALC_RESULT_PREFIX, 24, null);

        // 计算结果操作键
        this.setOptItem(Material.BARRIER, "清空", 41, "clear");
        this.setOptItem(Material.RED_CONCRETE, "删除", 42, "delete");
        this.setOptItem(Material.LIME_CONCRETE, "确认", 43, "confirm");
    }

    private void setModeItem() {
        Inventory inv = this.getInventory();

        for (int modePosItem : this.modePos) {
            String title = null, optSign = null;
            Material material = null;
            switch (modePosItem) {
                case 14 -> {
                    title = "按个获取";
                    optSign = "single";
                    material = Material.DIAMOND;
                }
                case 15 -> {
                    title = "按组获取";
                    optSign = "group";
                    material = Material.DIAMOND;
                }
                case 16 -> {
                    title = "按盒获取";
                    optSign = "box";
                    material = Material.WHITE_SHULKER_BOX;
                }
            }
            ItemStack is = this.setOptItem(material, title, modePosItem, optSign);
            // 按个获取操作键，默认选中
            ItemMeta im = is.getItemMeta();
            assert im != null;

            switch (modePosItem) {
                case 14 -> im.addEnchant(Enchantment.MENDING, 1, true);
                case 15 -> is.setAmount(64);
            }

            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(im);
            inv.setItem(modePosItem, is);
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
        // 清空选中（附魔发光效果）
        for (int modePosItem : this.modePos) {
            ItemStack modeItem = this.getInventory().getItem(modePosItem);
            assert modeItem != null;
            if (modePosItem == 16) {
                modeItem.setType(Material.WHITE_SHULKER_BOX);
            }
            modeItem.removeEnchantment(Enchantment.MENDING);
            this.getInventory().setItem(modePosItem, modeItem);
        }

        // 添加选中效果
        ItemStack modeItem = this.getInventory().getItem(this.modePos[mode - 1]);
        assert modeItem != null;
        if (this.modePos[mode - 1] == 16) {
            modeItem.setType(Material.SHULKER_BOX);
        }
        ItemMeta im = modeItem.getItemMeta();
        assert im != null;
        im.addEnchant(Enchantment.MENDING, 1, true);
        modeItem.setItemMeta(im);
        this.getInventory().setItem(this.modePos[mode - 1], modeItem);
        this.setCalcResult(this.getCalcResult());
    }

    public int getCalcResult() {
        return calcResult;
    }

    public void setCalcResult(int calcResult) {
        // 库存单位换算
        Item itemInfo = (Item) this.getMenuContext().get("itemInfo");
        int amount = itemInfo.getAmount();
        Material material = Material.getMaterial(itemInfo.getType());
        assert material != null;
        int maxStackSize = material.getMaxStackSize();
        switch (this.mode) {
            case 2 -> amount = amount / maxStackSize;
            case 3 -> amount = Math.min(amount / (maxStackSize * 27), 45); // 按盒获取最多只能装45盒
        }
        this.calcResult = Math.min(calcResult, amount);

        // 更新计算结果文字
        ItemStack calcMsg = this.getInventory().getItem(24);
        assert calcMsg != null;
        ItemMeta im = calcMsg.getItemMeta();
        assert im != null;
        String modeStr = null;
        switch (this.mode) {
            case 1 -> modeStr = "个";
            case 2 -> modeStr = "组";
            case 3 -> modeStr = "盒";
        }
        im.setDisplayName(CALC_RESULT_PREFIX + this.calcResult + modeStr);
        calcMsg.setItemMeta(im);
        this.getInventory().setItem(24, calcMsg);
    }

    public void clear() {
        this.setCalcResult(0);
    }

    public void back() {
        ItemListMenu menu = new ItemListMenu((int) this.getMenuContext().get("fromPage"), "物料选择菜单", this.getPlayer());
        this.close();
        menu.open(new ItemListMenuListener(menu));
    }

    public void confirm() {
        if (this.getCalcResult() > 0) {
            if (this.mode != 3) {
                new SendItemTask(this.getPlayer(), (Item) this.getMenuContext().get("itemInfo"), this.mode, this.getCalcResult()).runTaskAsynchronously(Main.plugin);
                this.close();
            } else {
                this.getMenuContext().put("boxCount", this.calcResult);
                ShulkerBoxPlaceMenu menu = new ShulkerBoxPlaceMenu("请放置对应数量的潜影盒", this.getPlayer(), this.getMenuContext());
                menu.open(new ShulkerBoxPlaceMenuListener(menu));
            }
        }
    }
}
