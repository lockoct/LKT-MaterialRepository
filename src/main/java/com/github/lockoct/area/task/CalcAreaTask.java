package com.github.lockoct.area.task;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.handler.container.ContainerHandler;
import com.github.lockoct.handler.container.ContainerHandlerFactory;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Map;

public class CalcAreaTask extends BukkitRunnable {
    private final Player player;
    private final Location point1;
    private final Location point2;

    public CalcAreaTask(Location point1, Location point2, Player player) {
        this.point1 = point1;
        this.point2 = point2;
        this.player = player;
    }

    @Override
    public void run() {
        int area = (Math.abs(point1.getBlockX() - point2.getBlockX()) + 1) * (Math.abs(point1.getBlockY() - point2.getBlockY()) + 1) * (Math.abs(point1.getBlockZ() - point2.getBlockZ()) + 1);
        if (area > 27000) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaTooLarge"));
            return;
        }
        int key = player.hashCode();
        MarkData data = MarkListener.getMarkModePlayers().get(key);
        Map<Material, ArrayList<Location>> locationMap = data.getContainerLocation();
        // 统计选区内箱子前，先要把上一次选区的容器记录清掉
        // 避免相同位置的容器添加多次
        locationMap.clear();

        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        for (int y = minY; y <= maxY; y++) {
            int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
            int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
            for (int z = minZ; z <= maxZ; z++) {
                int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
                int minX = Math.min(point1.getBlockX(), point2.getBlockX());
                for (int x = minX; x <= maxX; x++) {
                    if (isCancelled()) {
                        return;
                    }
                    Block areaBlock = new Location(player.getWorld(), x, y, z).getBlock();
                    Material blockType = areaBlock.getType();
                    if (ContainerHandlerFactory.getSupportedContainers().contains(blockType)) {
                        // 如果当前map没有对应容器的数组，就新建数组
                        // 如果有就直接返回数组
                        // 并将该容器位置添加到数组中
                        locationMap.computeIfAbsent(blockType, k -> new ArrayList<>()).add(areaBlock.getLocation());
                    }
                }
            }
        }

        if (locationMap.keySet().size() == 0) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg.noContainer", area));
            return;
        }

        StringBuilder sb = new StringBuilder(ChatColor.LIGHT_PURPLE + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg.start", area));
        for (Material type : locationMap.keySet()) {
            ContainerHandler handler = ContainerHandlerFactory.getHandler(type);
            handler.getMarkStatisticsMsg(locationMap, sb, player);
        }

        sb.deleteCharAt(sb.length() - 1);
        player.sendMessage(sb.toString());
    }
}
