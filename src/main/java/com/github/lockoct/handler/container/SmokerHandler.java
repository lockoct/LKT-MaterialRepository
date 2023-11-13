package com.github.lockoct.handler.container;

import com.github.lockoct.Main;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class SmokerHandler extends FurnaceHandler implements ContainerHandler {
    @Override
    public void getMarkStatisticsMsg(Map<Material, ArrayList<Location>> locationMap, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg.smokerCount", locationMap.get(Material.SMOKER).size()));
    }

    @Override
    public void getReloadStatisticsMsg(int containerListSize, StringBuilder sb, Player player) {
        sb.append(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.smokerCount", containerListSize));
    }
}
