package com.github.lockoct.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.MenuContext;
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
import java.util.List;

public class ItemListMenu extends BaseMenu{
    private final int PAGE_SIZE = 45;
    private int currentPage;
    private int totalPage;
    private int total;
    private List<Item> items;

    public ItemListMenu(String title, Player player) {
        super(54, title, player, Main.plugin);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setItems() {
        this.setItems(1);
    }

    public void setItems(int page) {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Pager pager = dao.createPager(page, PAGE_SIZE);
                    items = dao.query(Item.class, null, pager);
                    pager.setRecordCount(dao.count(Item.class));
                    currentPage = page;
                    totalPage = pager.getPageCount();
                    total = pager.getRecordCount();
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
    private void setPageElement() {
        Inventory inv = this.getInventory();

        // 上一页
        if (this.currentPage > 1) {
            this.setOptItem(Material.ARROW, "上一页：第" + (this.currentPage - 1) + "页", PAGE_SIZE, "prePage");
        } else {
            inv.setItem(PAGE_SIZE, null);
        }

        // 下一页
        if (this.currentPage < this.totalPage) {
            this.setOptItem(Material.ARROW, "下一页：第" + (this.currentPage + 1) + "页", 53, "nextPage");
        } else {
            inv.setItem(53, null);
        }

        // 分页信息
        ItemStack is = this.setOptItem(Material.BOOK, "当前 " + this.currentPage + " / " + this.totalPage + " 页", 49, "pageInfo");
        ItemMeta im = is.getItemMeta();
        assert im != null;
        // 分页附加信息
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("共 " + this.total + " 类物品");
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    public void toKeyboardMenu(int index) {
        if (index < PAGE_SIZE) {
            MenuContext context = new MenuContext();
            context.setItemInfo(items.get(index));
            context.setFromPage(this.currentPage);
            KeyboardMenu menu = new KeyboardMenu("数量选择", this.getPlayer(), context);
            this.close();
            menu.open(new KeyboardMenuListener(menu));
        }
    }
}
