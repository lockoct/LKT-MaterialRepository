package com.github.lockoct.handler;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.nutz.dao.Dao;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

public interface ContainerHandler {
    void getMarkStatisticsMsg(Map<Material, ArrayList<Location>> locationMap, StringBuilder sb, Player player);

    void getReloadStatisticsMsg(int containerListSize, StringBuilder sb, Player player);

    void collectItem(BiConsumer<Dao, ItemStack> itemsToDB, Dao dao, Block block);
}
