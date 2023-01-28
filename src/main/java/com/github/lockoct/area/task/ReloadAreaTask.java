package com.github.lockoct.area.task;

import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.CollectAreaChest;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutTxDao;

import java.util.ArrayList;
import java.util.List;

public class ReloadAreaTask extends BukkitRunnable {
    private final CollectArea area;
    private final Player player;

    public ReloadAreaTask(CollectArea area, Player player) {
        this.area = area;
        this.player = player;
    }

    @Override
    public void run() {
        Dao dao = DatabaseUtil.getDao();
        if (dao == null) {
            return;
        }
        NutTxDao tx = new NutTxDao(dao);

        ArrayList<String> existChestIdList = new ArrayList<>();
        int newChestCount = 0;
        String playerId = this.player.getUniqueId().toString();

        dao.fetchLinks(area, "chests");

        try {
            tx.beginRC();

            // 遍历区域
            int maxY = Math.max(this.area.getY1(), this.area.getY2());
            int minY = Math.min(this.area.getY1(), this.area.getY2());
            for (int y = minY; y <= maxY; y++) {
                int maxZ = Math.max(this.area.getZ1(), this.area.getZ2());
                int minZ = Math.min(this.area.getZ1(), this.area.getZ2());
                for (int z = minZ; z <= maxZ; z++) {
                    int maxX = Math.max(this.area.getX1(), this.area.getX2());
                    int minX = Math.min(this.area.getX1(), this.area.getX2());
                    for (int x = minX; x <= maxX; x++) {
                        Block areaBlock = new Location(Bukkit.getWorld(this.area.getWorld()), x, y, z).getBlock();
                        if (areaBlock.getType() == Material.CHEST) {
                            List<CollectAreaChest> tmpList = area.getChests().stream().filter(e -> e.getX() == areaBlock.getX() && e.getY() == areaBlock.getY() && e.getZ() == areaBlock.getZ()).toList();
                            CollectAreaChest chest;
                            if (tmpList.isEmpty()) {
                                newChestCount++;
                                chest = new CollectAreaChest();
                                chest.setAreaId(this.area.getId());
                                chest.setX(areaBlock.getX());
                                chest.setY(areaBlock.getY());
                                chest.setZ(areaBlock.getZ());
                                chest.setCreateUser(playerId);
                                chest.setUpdateUser(playerId);
                                tx.insert(chest);
                            } else {
                                chest = tmpList.get(0);
                            }
                            existChestIdList.add(chest.getId());
                        }
                    }
                }
            }
            Cnd cond = Cnd.where("area_id", "=", this.area.getId());
            if (existChestIdList.size() > 0) {
                cond.and("id", "NOT IN", existChestIdList);
            }
            int res = tx.clear(CollectAreaChest.class, cond);
            tx.commit();

            String msg = "采集区域更新完成";
            if (res > 0) {
                msg = msg.concat("，减少了" + res + "个箱子");
            }
            if (newChestCount > 0) {
                msg = msg.concat("，增加了" + newChestCount + "个箱子");
            }
            this.player.sendMessage(ChatColor.GREEN + msg);
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
            this.player.sendMessage(ChatColor.RED + "采集区域重新识别箱子失败");
        } finally {
            tx.close();
        }
    }
}
