package com.github.lockoct.area.task;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
        int chestCount = 0;
        int key = player.hashCode();
        MarkData data = MarkListener.getMarkModePlayers().get(key);
        // 统计选区内箱子前，先要把上一次选区的箱子记录清掉
        // 避免同一个箱子添加多次
        data.getChestLocation().clear();

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
                    if (areaBlock.getType() == Material.CHEST) {
                        data.getChestLocation().add(areaBlock.getLocation());
                        chestCount++;
                    }
                }
            }
        }
        player.sendMessage(ChatColor.LIGHT_PURPLE + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.areaStatisticsMsg", area, chestCount));
    }
}
