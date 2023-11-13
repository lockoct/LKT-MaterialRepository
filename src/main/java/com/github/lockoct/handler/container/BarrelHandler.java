package com.github.lockoct.handler.container;

import com.github.lockoct.Main;
import com.github.lockoct.handler.item.ItemHandler;
import com.github.lockoct.handler.item.ItemHandlerFactory;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class BarrelHandler implements ContainerHandler {
    @Override
    public void getMarkStatisticsMsg(Map<Material, ArrayList<Location>> locationMap, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg.barrelCount", locationMap.get(Material.BARREL).size()));
    }

    @Override
    public void getReloadStatisticsMsg(int containerListSize, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.barrelCount", containerListSize));
    }

    @Override
    public void collectItem(Block block) {
        Barrel barrel = (Barrel) block.getState();
        Inventory inv = barrel.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is != null) {
                ItemHandler handler = ItemHandlerFactory.getHandler(is);
                if (handler.execute()) {
                    // 新增或更新成功后去除原本箱子中的物品
                    inv.clear(i);
                }
            }
        }
    }
}
