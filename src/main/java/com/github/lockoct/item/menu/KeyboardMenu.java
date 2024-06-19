package com.github.lockoct.item.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.item.listener.ItemListMenuListener;
import com.github.lockoct.item.listener.ShulkerBoxPlaceMenuListener;
import com.github.lockoct.item.listener.UnstackItemListMenuListener;
import com.github.lockoct.item.task.SendItemTask;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Bukkit;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class KeyboardMenu extends BaseMenu {
    private final int[] numKeyPos = new int[]{38, 10, 11, 12, 19, 20, 21, 28, 29, 30};
    private final int[] modePos = new int[]{14, 15, 16};
    private int mode = 1;
    private int calcResult = 0;

    public KeyboardMenu(HashMap<String, Object> menuContext, Player player) {
        this(I18nUtil.getText(Main.plugin, player, "keyboardMenu.title"), menuContext, player);
    }

    public KeyboardMenu(String title, HashMap<String, Object> menuContext, Player player) {
        super(54, title, menuContext, player, Main.plugin);
        setKeyboardBtn();
        setModeBtn();
        setSwitchSpecialItemModeBtn();
        setOptItem(Material.DARK_OAK_DOOR, I18nUtil.getCommonText(player, "menu.exit"), 48, "exit");
        setOptItem(Material.ARROW, I18nUtil.getCommonText(player, "menu.back"), 50, "back");
        setBackGround(Material.BLUE_STAINED_GLASS_PANE);
    }

    private void setKeyboardBtn() {
        Player player = getPlayer();
        // 数字键
        for (int i = 0; i < numKeyPos.length; i++) {
            ItemMeta im = Bukkit.getItemFactory().getItemMeta(Material.LIGHT);
            assert im != null;
            BlockData data = Material.LIGHT.createBlockData();
            ((Light) data).setLevel(i);
            ((BlockDataMeta) im).setBlockData(data);
            setOptItem(Material.LIGHT, im, "" + i, numKeyPos[i], "" + i);
        }

        // 计算结果告示牌
        setOptItem(Material.OAK_SIGN, I18nUtil.getText(Main.plugin, player, "keyboardMenu.calcResPrefix"), 24, null);

        // 计算结果操作键
        setOptItem(Material.BARRIER, I18nUtil.getText(Main.plugin, player, "keyboardMenu.btn.clear"), 41, "clear");
        setOptItem(Material.RED_CONCRETE, I18nUtil.getText(Main.plugin, player, "keyboardMenu.btn.delete"), 42, "delete");
        setOptItem(Material.LIME_CONCRETE, I18nUtil.getText(Main.plugin, player, "keyboardMenu.btn.confirm"), 43, "confirm");
    }

    private void setModeBtn() {
        Inventory inv = getInventory();
        Player player = getPlayer();

        for (int modePosItem : modePos) {
            String title = null, optSign = null;
            Material material = null;
            switch (modePosItem) {
                case 14 -> {
                    title = I18nUtil.getText(Main.plugin, player, "keyboardMenu.mode.getBySingle");
                    optSign = "single";
                    material = Material.DIAMOND;
                }
                case 15 -> {
                    title = I18nUtil.getText(Main.plugin, player, "keyboardMenu.mode.getByGroup");
                    optSign = "group";
                    material = Material.DIAMOND;
                }
                case 16 -> {
                    title = I18nUtil.getText(Main.plugin, player, "keyboardMenu.mode.getByBox");
                    optSign = "box";
                    material = Material.WHITE_SHULKER_BOX;
                }
            }
            ItemStack is = setOptItem(material, title, modePosItem, optSign);
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
        for (int modePosItem : modePos) {
            ItemStack modeItem = getInventory().getItem(modePosItem);
            assert modeItem != null;
            if (modePosItem == 16) {
                modeItem.setType(Material.WHITE_SHULKER_BOX);
            }
            modeItem.removeEnchantment(Enchantment.MENDING);
            getInventory().setItem(modePosItem, modeItem);
        }

        // 添加选中效果
        ItemStack modeItem = getInventory().getItem(modePos[mode - 1]);
        assert modeItem != null;
        if (modePos[mode - 1] == 16) {
            modeItem.setType(Material.SHULKER_BOX);
        }
        ItemMeta im = modeItem.getItemMeta();
        assert im != null;
        im.addEnchant(Enchantment.MENDING, 1, true);
        modeItem.setItemMeta(im);
        getInventory().setItem(modePos[mode - 1], modeItem);
        setCalcResult(getCalcResult());
    }

    // 设置切换特殊物品模式按钮
    public void setSwitchSpecialItemModeBtn() {
        Optional<Material> optionalMaterial = Optional.ofNullable(getMenuContext().get("itemInfo"))
            .map(itemInfo -> (Item) itemInfo)
            .map(Item::getType)
            .map(Material::getMaterial);

        if (optionalMaterial.isEmpty()) {
            return;
        }

        // 仅在选择物品为不可堆叠物品时起效
        if (new ItemStack(optionalMaterial.get()).getMaxStackSize() > 1) {
            return;
        }

        ItemMeta im = Bukkit.getItemFactory().getItemMeta(Material.NETHER_STAR);

        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(I18nUtil.getText(Main.plugin, getPlayer(), "keyboardMenu.btn.special.description"));
        assert im != null;
        im.setLore(loreList);

        setOptItem(Material.NETHER_STAR, im, I18nUtil.getText(Main.plugin, getPlayer(), "keyboardMenu.btn.special.title"), 49, "special");
    }

    public int getCalcResult() {
        return calcResult;
    }

    public void setCalcResult(int calcResult) {
        Player player = getPlayer();
        // 库存单位换算
        Item itemInfo = (Item) getMenuContext().get("itemInfo");
        int amount = itemInfo.getAmount();
        Material material = Material.getMaterial(itemInfo.getType());
        assert material != null;
        int maxStackSize = material.getMaxStackSize();
        switch (mode) {
            case 2 -> amount = amount / maxStackSize;
            case 3 -> amount = Math.min(amount / (maxStackSize * 27), 45); // 按盒获取最多只能装45盒
        }
        this.calcResult = Math.min(calcResult, amount);

        // 更新计算结果文字
        ItemStack calcMsg = getInventory().getItem(24);
        assert calcMsg != null;
        ItemMeta im = calcMsg.getItemMeta();
        assert im != null;
        String modeStr = null;
        switch (mode) {
            case 1 -> modeStr = I18nUtil.getText(Main.plugin, player, "keyboardMenu.unit.single");
            case 2 -> modeStr = I18nUtil.getText(Main.plugin, player, "keyboardMenu.unit.group");
            case 3 -> modeStr = I18nUtil.getText(Main.plugin, player, "keyboardMenu.unit.box");
        }
        im.setDisplayName(I18nUtil.getText(Main.plugin, player, "keyboardMenu.calcResPrefix") + this.calcResult + modeStr);
        calcMsg.setItemMeta(im);
        getInventory().setItem(24, calcMsg);
    }

    public void clear() {
        setCalcResult(0);
    }

    public void back() {
        ItemListMenu menu = new ItemListMenu((int) getMenuContext().get("fromPage"), getPlayer());
        close();
        menu.open(new ItemListMenuListener(menu));
    }

    public void confirm() {
        Player player = getPlayer();
        if (getCalcResult() > 0) {
            if (mode != 3) {
                new SendItemTask(player, (Item) getMenuContext().get("itemInfo"), mode, getCalcResult()).runTaskAsynchronously(Main.plugin);
                close();
            } else {
                getMenuContext().put("boxCount", calcResult);
                ShulkerBoxPlaceMenu menu = new ShulkerBoxPlaceMenu(getMenuContext(), player);
                close();
                menu.open(new ShulkerBoxPlaceMenuListener(menu));
            }
        }
    }

    public void toUnstackItemMenu() {
        UnstackItemListMenu menu = new UnstackItemListMenu(getMenuContext(), getPlayer());
        close();
        menu.open(new UnstackItemListMenuListener(menu));
    }
}
