package com.github.lockoct.item.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.UnstackItem;
import com.github.lockoct.item.listener.ItemListMenuListener;
import com.github.lockoct.item.task.SendUnstackItemTask;
import com.github.lockoct.menu.PageableMenu;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnstackItemListMenu extends PageableMenu {
    private List<UnstackItem> items;
    private List<UnstackItem> selectedItems;

    public UnstackItemListMenu(String title, HashMap<String, Object> menuContext, Player player) {
        super(title, menuContext, player, Main.plugin);
        setOptItem(Material.SPECTRAL_ARROW, I18nUtil.getCommonText(player, "menu.back"), 50, "back");
    }

    @Override
    protected void setPageContent(int page) {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Item itemInfo = (Item) getMenuContext().get("itemInfo");
                    // 分页查询
                    Pager pager = dao.createPager(page, PAGE_SIZE);
                    Cnd cond = Cnd.where("item_id", "=", itemInfo.getId());
                    items = dao.query(UnstackItem.class, cond, pager);
                    pager.setRecordCount(dao.count(UnstackItem.class, cond));
                    setTotalPage(pager.getPageCount());
                    setTotal(pager.getRecordCount());
                    // 设置分页
                    setPageElement();
                    // 填充物品
                    for (int i = 0; i < PAGE_SIZE; i++) {
                        Inventory inv = getInventory();
                        if (i < items.size()) {
                            ReadWriteNBT nbt = NBT.parseNBT(items.get(i).getNbt());
                            ItemStack is = NBT.itemStackFromNBT(nbt);
                            inv.setItem(i, is);
                        } else {
                            // 填充空位
                            inv.setItem(i, null);
                        }
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
        }
    }

    // 翻页按钮、分页信息
    @Override
    protected void setPageElement() {
        super.setPageElement();

        Inventory inv = getInventory();

        // 获取分页信息元素
        ItemStack is = inv.getItem(49);
        assert is != null;
        ItemMeta im = is.getItemMeta();
        assert im != null;
        // 分页附加信息
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(I18nUtil.getText(Main.plugin, getPlayer(), "unstackItemListMenu.pageStatisticsInfo", getTotal()));
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    public List<UnstackItem> getItems() {
        return items;
    }

    public List<UnstackItem> getSelectedItems() {
        if (selectedItems == null) {
            selectedItems = new ArrayList<>();
        }
        return selectedItems;
    }

    public void confirm() {
        new SendUnstackItemTask(getPlayer(), selectedItems).runTaskAsynchronously(Main.plugin);
        close();
    }

    public void back() {
        ItemListMenu menu = new ItemListMenu((int) getMenuContext().get("fromPage"), I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.title"), getPlayer());
        close();
        menu.open(new ItemListMenuListener(menu));
    }
}
