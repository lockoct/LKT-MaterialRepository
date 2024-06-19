package com.github.lockoct.area.menu;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.AreaListMenuListener;
import com.github.lockoct.area.task.ReloadAreaTask;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Dao;

import java.util.ArrayList;
import java.util.HashMap;

public class AreaManageMenu extends BaseMenu {
    private boolean enabled;

    public AreaManageMenu(HashMap<String, Object> menuContext, Player player) {
        this(I18nUtil.getText(Main.plugin, player, "areaManageMenu.title"), menuContext, player);
    }

    public AreaManageMenu(String title, HashMap<String, Object> menuContext, Player player) {
        super(54, title, menuContext, player, Main.plugin);

        // 取出上下文信息
        CollectArea areaInfo = (CollectArea) menuContext.get("areaInfo");
        int areaContainerCount = (int) menuContext.get("areaContainerCount");

        // 设置告示信息
        ItemMeta im = Bukkit.getItemFactory().getItemMeta(Material.OAK_SIGN);
        assert im != null;
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(I18nUtil.getText(Main.plugin, player, "areaManageMenu.containerStatisticsInfo", areaContainerCount));
        im.setLore(loreList);
        setOptItem(Material.OAK_SIGN, im, areaInfo.getName(), 13, null);

        // 删除按钮
        setOptItem(Material.BARRIER, I18nUtil.getText(Main.plugin, player, "areaManageMenu.btn.delete"), 29, "delete");

        // 启用/禁用按钮
        enabled = areaInfo.isEnabled();
        String enableStr = I18nUtil.getText(Main.plugin, player, enabled ? "areaManageMenu.btn.enable" : "areaManageMenu.btn.disable");
        Material enableItemMaterial = enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE;
        setOptItem(enableItemMaterial, enableStr, 31, "enable");

        // 重新识别按钮
        setOptItem(Material.COMPASS, I18nUtil.getText(Main.plugin, player, "areaManageMenu.btn.reload"), 33, "reload");

        // 返回、退出按钮
        setOptItem(Material.DARK_OAK_DOOR, I18nUtil.getCommonText(player, "menu.exit"), 48, "exit");
        setOptItem(Material.ARROW, I18nUtil.getCommonText(player, "menu.back"), 50, "back");

        // 背景
        setBackGround(Material.BLUE_STAINED_GLASS_PANE);
    }

    public void enable() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    CollectArea area = (CollectArea) getMenuContext().get("areaInfo");
                    enabled = !enabled;
                    area.setEnabled(enabled);
                    int res = dao.update(area);
                    if (res > 0) {
                        String enableStr = I18nUtil.getText(Main.plugin, getPlayer(), enabled ? "areaManageMenu.btn.enable" : "areaManageMenu.btn.disable");
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
                    CollectArea area = (CollectArea) getMenuContext().get("areaInfo");
                    area.setDeleted(true);
                    int res = dao.update(area);
                    if (res > 0) {
                        getPlayer().sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, getPlayer(), "areaManageMenu.deleteSuccessful", area.getName()));
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
            close();
        }
    }

    public void reload() {
        new ReloadAreaTask((CollectArea) getMenuContext().get("areaInfo"), getPlayer()).runTaskAsynchronously(Main.plugin);
        close();
    }

    public void back() {
        AreaListMenu menu = new AreaListMenu((int) getMenuContext().get("fromPage"), getPlayer());
        close();
        menu.open(new AreaListMenuListener(menu));
    }
}
