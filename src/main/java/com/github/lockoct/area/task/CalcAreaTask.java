package com.github.lockoct.area.task;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.entity.MarkData;
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
        int area = (Math.abs(this.point1.getBlockX() - this.point2.getBlockX()) + 1) * (Math.abs(this.point1.getBlockY() - this.point2.getBlockY()) + 1) * (Math.abs(this.point1.getBlockZ() - this.point2.getBlockZ()) + 1);
        if (area > 27000) {
            this.player.sendMessage(ChatColor.RED + "区域范围不能大于27000方块大小");
            return;
        }
        int chestCount = 0;
        int key = this.player.hashCode();
        MarkData data = MarkListener.getMarkModePlayers().get(key);
        // 统计选区内箱子前，先要把上一次选区的箱子记录清掉
        // 避免同一个箱子添加多次
        data.getChestLocation().clear();

        int maxY = Math.max(this.point1.getBlockY(), this.point2.getBlockY());
        int minY = Math.min(this.point1.getBlockY(), this.point2.getBlockY());
        for (int y = minY; y <= maxY; y++) {
            int maxZ = Math.max(this.point1.getBlockZ(), this.point2.getBlockZ());
            int minZ = Math.min(this.point1.getBlockZ(), this.point2.getBlockZ());
            for (int z = minZ; z <= maxZ; z++) {
                int maxX = Math.max(this.point1.getBlockX(), this.point2.getBlockX());
                int minX = Math.min(this.point1.getBlockX(), this.point2.getBlockX());
                for (int x = minX; x <= maxX; x++) {
                    if (this.isCancelled()) {
                        return;
                    }
                    Block areaBlock = new Location(this.player.getWorld(), x, y, z).getBlock();
                    if (areaBlock.getType() == Material.CHEST) {
                        data.getChestLocation().add(areaBlock.getLocation());
                        chestCount++;
                    }
                }
            }
        }
        this.player.sendMessage(ChatColor.LIGHT_PURPLE+"该区域总计" + area + "方块大小，有箱子" + chestCount + "个");
    }
}
