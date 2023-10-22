package com.github.lockoct.handler;

import com.github.lockoct.Main;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.nutz.dao.Dao;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;


public class ChestHandler implements ContainerHandler {
    @Override
    public void getMarkStatisticsMsg(Map<Material, ArrayList<Location>> locationMap, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg.chestCount", locationMap.get(Material.CHEST).size()));
    }

    @Override
    public void getReloadStatisticsMsg(int containerListSize, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.chestCount", containerListSize));
    }

    @Override
    public void collectItem(BiConsumer<Dao, ItemStack> itemsToDB, Dao dao, Block block) {
        Chest chest = (Chest) block.getState();
        Inventory inv = chest.getBlockInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack is = inv.getItem(i);
            if (is != null) {
                // 处理可堆叠物品
                if (is.getMaxStackSize() > 1) {
                    itemsToDB.accept(dao, is);
                    // 新增或更新后去除原本箱子中的物品
                    inv.clear(i);
                } else {
                    // 不可堆叠物品目前仅支持不死图腾
                    if (is.getType() == Material.TOTEM_OF_UNDYING) {
                        itemsToDB.accept(dao, is);
                        // 新增或更新后去除原本箱子中的物品
                        inv.clear(i);
                    }
                }
            }
        }
    }
}
