package com.github.lockoct.handler.container;

import com.github.lockoct.Main;
import com.github.lockoct.handler.item.ItemHandler;
import com.github.lockoct.handler.item.ItemHandlerFactory;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class DecoratedPotHandler implements ContainerHandler {
    @Override
    public void getMarkStatisticsMsg(Map<Material, ArrayList<Location>> locationMap, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg.decoratedPotCount", locationMap.get(Material.BLAST_FURNACE).size()));
    }

    @Override
    public void getReloadStatisticsMsg(int containerListSize, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.decoratedPotCount", containerListSize));
    }

    @Override
    public void collectItem(Block block) {
        TileState decoratedPot = (TileState) block.getState();
        NBT.modify(decoratedPot, nbt -> {
            ReadWriteNBT rwNBT = nbt.getCompound("item");
            if (rwNBT == null) {
                return;
            }

            ItemStack is = NBT.itemStackFromNBT(rwNBT);
            if (is == null) {
                return;
            }
            ItemHandler handler = ItemHandlerFactory.getHandler(is);
            if (handler.execute()) {
                // 新增或更新成功后去除原本陶罐中的物品
                nbt.removeKey("item");
            }
        });
    }
}
