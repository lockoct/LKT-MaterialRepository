package com.github.lockoct.menu;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.AreaListMenuListener;
import com.github.lockoct.area.task.ReloadAreaTask;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.MenuContext;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Dao;

import java.util.ArrayList;

public class AreaManageMenu extends BaseMenu {
    private final MenuContext context;
    private boolean enabled;

    public AreaManageMenu(String title, Player player, MenuContext context) {
        super(54, title, player, Main.plugin);
        this.context = context;
        // 设置告示信息
        ItemStack is = this.setOptItem(Material.OAK_SIGN, context.getAreaInfo().getName(), 13, null);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add("区域内共有 " + context.getAreaChestCount() + " 个箱子");
        im.setLore(loreList);
        is.setItemMeta(im);
        this.getInventory().setItem(13, is);

        this.setOptItem(Material.BARRIER, "删除区域", 29, "delete");

        this.enabled = context.getAreaInfo().isEnabled();
        String enableStr = this.enabled ? "已启用" : "已禁用";
        Material enableItemMaterial = this.enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
        this.setOptItem(enableItemMaterial, enableStr, 31, "enable");

        this.setOptItem(Material.COMPASS, "重新识别区域内箱子", 33, "reload");

        this.setOptItem(Material.ARROW, "返回", 48, "back");
        this.setOptItem(Material.DARK_OAK_DOOR, "退出", 50, "exit");
        this.setBackGround(Material.BLUE_STAINED_GLASS_PANE);
    }

    public void enable() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectArea area = context.getAreaInfo();
                    enabled = !enabled;
                    area.setEnabled(enabled);
                    int res = dao.update(area);
                    if (res > 0) {
                        String enableStr = enabled ? "已启用" : "已禁用";
                        Material enableItemMaterial = enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
                        ItemStack is = getInventory().getItem(31);
                        assert is != null;
                        is.setType(enableItemMaterial);
                        ItemMeta im = is.getItemMeta();
                        assert im != null;
                        im.setDisplayName(enableStr);
                        is.setItemMeta(im);
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
        }
    }

    public void delete() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectArea area = context.getAreaInfo();
                    area.setDeleted(true);
                    int res = dao.update(area);
                    if (res > 0) {
                        getPlayer().sendMessage(ChatColor.GREEN + "区域 " + area.getName() + " 删除成功");
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
            this.close();
        }
    }

    public void reload() {
        new ReloadAreaTask(this.context.getAreaInfo(), this.getPlayer()).runTaskAsynchronously(Main.plugin);
        this.close();
    }

    public void back() {
        AreaListMenu menu = new AreaListMenu("区域管理菜单", this.getPlayer());
        this.close();
        menu.setAreaItems(this.context.getFromPage());
        menu.open(new AreaListMenuListener(menu));
    }
}
