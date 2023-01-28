package com.github.lockoct.menu;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.AreaManageMenuListener;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.CollectAreaChest;
import com.github.lockoct.entity.MenuContext;
import com.github.lockoct.utils.DatabaseUtil;
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
import java.util.List;

public class AreaListMenu extends BaseMenu {
    private final int PAGE_SIZE = 45;
    private int currentPage;
    private int totalPage;
    private int total;
    private List<CollectArea> areas;
    private final List<Integer> chestCountList = new ArrayList<>();

    public AreaListMenu(String title, Player player) {
        super(54, title, player, Main.plugin);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setAreaItems() {
        this.setAreaItems(1);
    }

    public void setAreaItems(int page) {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Pager pager = dao.createPager(page, PAGE_SIZE);
                    Cnd cond = Cnd.where("deleted", "=", 0).and("create_user", "=", getPlayer().getUniqueId().toString());
                    areas = dao.query(CollectArea.class, cond.orderBy("create_time", "desc"), pager);
                    pager.setRecordCount(dao.count(CollectArea.class, cond));
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
                        if (i < areas.size()) {
                            ItemStack is = new ItemStack(Material.GRASS_BLOCK);
                            ItemMeta im = is.getItemMeta();
                            assert im != null;
                            im.setDisplayName(areas.get(i).getName());
                            // 区域内箱子数量填在附加信息中
                            int chestCount = dao.count(CollectAreaChest.class, Cnd.where("area_id", "=", areas.get(i).getId()));
                            chestCountList.add(i, chestCount);
                            ArrayList<String> loreList = new ArrayList<>();
                            loreList.add("区域内共有 " + chestCount + " 个箱子");
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
        loreList.add("共 " + this.total + " 块区域");
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    public void toManageMenu(int index) {
        if (index < PAGE_SIZE) {
            MenuContext context = new MenuContext();
            context.setAreaInfo(areas.get(index));
            context.setAreaChestCount(chestCountList.get(index));
            context.setFromPage(this.currentPage);
            AreaManageMenu menu = new AreaManageMenu("区域管理", this.getPlayer(), context);
            this.close();
            menu.open(new AreaManageMenuListener(menu));
        }
    }
}
