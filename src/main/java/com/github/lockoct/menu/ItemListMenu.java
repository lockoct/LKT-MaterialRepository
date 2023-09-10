package com.github.lockoct.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.item.listener.KeyboardMenuListener;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
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
                    items = dao.query(Item.class, null, pager);
                    pager.setRecordCount(dao.count(Item.class));
                    setTotalPage(pager.getPageCount());
                    setTotal(pager.getRecordCount());
                    // 设置分页
                    setPageElement();
                    // 设置退出
                    setOptItem(Material.DARK_OAK_DOOR, "退出", 48, "exit");
                    // 填充物品
                    for (int i = 0; i < PAGE_SIZE; i++) {
                        Inventory inv = getInventory();
                        if (i < items.size()) {
                            Material m = Material.getMaterial(items.get(i).getType());
                            if (m != null) {
                                ItemStack is = new ItemStack(m);
                                ItemMeta im = is.getItemMeta();
                                assert im != null;
                                // 物品数量填在附加信息中
                                ArrayList<String> loreList = new ArrayList<>();
                                loreList.add("剩余 " + items.get(i).getAmount() + " 个");
                                im.setLore(loreList);
                                is.setItemMeta(im);
                                inv.setItem(i, is);
                            }
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
        loreList.add("共 " + this.getTotal() + " 类物品");
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    public void toKeyboardMenu(int index) {
        if (index < PAGE_SIZE) {
            HashMap<String, Object> context = this.getMenuContext();
            // 物品信息
            context.put("itemInfo", items.get(index));
            // 列表菜单当前页码
            context.put("fromPage", this.getCurrentPage());

            KeyboardMenu menu = new KeyboardMenu("数量选择", this.getPlayer(), context);
            this.close();
            menu.open(new KeyboardMenuListener(menu));
        }
    }
}
