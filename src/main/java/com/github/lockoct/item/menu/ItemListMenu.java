package com.github.lockoct.item.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.item.listener.KeyboardMenuListener;
import com.github.lockoct.item.listener.UnstackItemListMenuListener;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.PageableMenu;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
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

public class ItemListMenu extends PageableMenu {
    private List<Item> items;

    public ItemListMenu(String title, Player player) {
        super(title, new HashMap<>(), player, Main.plugin);
    }

    public ItemListMenu(int currentPage, String title, Player player) {
        super(currentPage, title, new HashMap<>(), player, Main.plugin);
    }

    @Override
    protected void setPageContent(int page) {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Pager pager = dao.createPager(page, PAGE_SIZE);
                    items = dao.query(Item.class, Cnd.orderBy().asc("type"), pager);
                    pager.setRecordCount(dao.count(Item.class));
                    setTotalPage(pager.getPageCount());
                    setTotal(pager.getRecordCount());
                    // 设置分页
                    setPageElement();
                    // 填充物品
                    for (int i = 0; i < PAGE_SIZE; i++) {
                        Inventory inv = getInventory();
                        if (i < items.size()) {
                            Item tmp = items.get(i);
                            ItemStack is = null;
                            ItemMeta im = null;

                            // 对有nbt标签的物品进行处理
                            if (StringUtils.isNotBlank(tmp.getNbtMd5())) {
                                ReadWriteNBT nbt = NBT.parseNBT(tmp.getNbt());
                                is = NBT.itemStackFromNBT(nbt);
                                assert is != null;
                                is.setAmount(1);
                                im = is.getItemMeta();
                            } else {
                                Material m = Material.getMaterial(tmp.getType());
                                if (m != null) {
                                    is = new ItemStack(m);
                                    im = is.getItemMeta();
                                }
                            }

                            // 物品数量填在附加信息中
                            ArrayList<String> loreList = new ArrayList<>();
                            loreList.add(I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.itemInfo", items.get(i).getAmount()));
                            assert im != null;
                            im.setLore(loreList);
                            is.setItemMeta(im);
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
        loreList.add(I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.pageStatisticsInfo", getTotal()));
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    public void toNextMenu(int index) {
        if (index < PAGE_SIZE) {
            HashMap<String, Object> context = getMenuContext();
            Item item = items.get(index);
            // 物品信息
            context.put("itemInfo", item);
            // 列表菜单当前页码
            context.put("fromPage", getCurrentPage());

            BaseMenu menu;
            if (item.isStack()) {
                menu = new KeyboardMenu(I18nUtil.getText(Main.plugin, getPlayer(), "keyboardMenu.title"), context, getPlayer());
                close();
                menu.open(new KeyboardMenuListener(menu));
            } else {
                menu = new UnstackItemListMenu(I18nUtil.getText(Main.plugin, getPlayer(), "unstackItemListMenu.title"), context, getPlayer());
                close();
                menu.open(new UnstackItemListMenuListener(menu));
            }
        }
    }
}
