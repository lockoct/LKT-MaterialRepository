package com.github.lockoct.handler.container;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public interface ContainerHandler {
    void getMarkStatisticsMsg(Map<Material, ArrayList<Location>> locationMap, StringBuilder sb, Player player);

    void getReloadStatisticsMsg(int containerListSize, StringBuilder sb, Player player);

    void collectItem(Block block);
}
