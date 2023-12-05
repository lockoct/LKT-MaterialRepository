package com.github.lockoct.area.task;

import com.github.lockoct.Main;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.CollectAreaContainer;
import com.github.lockoct.handler.container.ContainerHandler;
import com.github.lockoct.handler.container.ContainerHandlerFactory;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
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

import java.util.*;
import java.util.stream.Collectors;

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

        ArrayList<String> existContainerIdList = new ArrayList<>();
        HashMap<Material, ArrayList<CollectAreaContainer>> newContainerMap = new HashMap<>();
        String playerId = player.getUniqueId().toString();

        dao.fetchLinks(area, "containers");

        try {
            tx.beginRC();

            // 遍历区域
            int maxY = Math.max(area.getY1(), area.getY2());
            int minY = Math.min(area.getY1(), area.getY2());
            for (int y = minY; y <= maxY; y++) {
                int maxZ = Math.max(area.getZ1(), area.getZ2());
                int minZ = Math.min(area.getZ1(), area.getZ2());
                for (int z = minZ; z <= maxZ; z++) {
                    int maxX = Math.max(area.getX1(), area.getX2());
                    int minX = Math.min(area.getX1(), area.getX2());
                    for (int x = minX; x <= maxX; x++) {
                        Block areaBlock = new Location(Bukkit.getWorld(area.getWorld()), x, y, z).getBlock();
                        Material blockType = areaBlock.getType();
                        if (ContainerHandlerFactory.getSupportedContainers().contains(blockType)) {
                            Optional<CollectAreaContainer> res = area.getContainers().stream().filter(e -> e.getX() == areaBlock.getX() && e.getY() == areaBlock.getY() && e.getZ() == areaBlock.getZ() && e.getType().equals(areaBlock.getType().toString())).findFirst();
                            CollectAreaContainer container;
                            if (res.isEmpty()) {
                                container = new CollectAreaContainer();
                                container.setType(blockType.toString());
                                container.setAreaId(area.getId());
                                container.setX(areaBlock.getX());
                                container.setY(areaBlock.getY());
                                container.setZ(areaBlock.getZ());
                                container.setCreateUser(playerId);
                                container.setUpdateUser(playerId);
                                tx.insert(container);

                                // 记录新增加的容器
                                newContainerMap.computeIfAbsent(blockType, k -> new ArrayList<>()).add(container);
                            } else {
                                container = res.get();
                            }
                            existContainerIdList.add(container.getId());
                        }
                    }
                }
            }
            Cnd cond = Cnd.where("area_id", "=", area.getId());
            if (existContainerIdList.size() > 0) {
                cond.and("id", "NOT IN", existContainerIdList);
            }
            int res = tx.clear(CollectAreaContainer.class, cond);
            tx.commit();

            StringBuilder msg = new StringBuilder(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.updateComplete"));
            // 输出减少的容器
            if (res > 0) {
                msg.append(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.decrease"));
                List<CollectAreaContainer> containerList = area.getContainers();
                // 找出减少的容器
                containerList.removeIf(e -> existContainerIdList.contains(e.getId()));
                // 按容器类型分组
                Map<String, List<CollectAreaContainer>> oldContainerMap = containerList.stream().collect(Collectors.groupingBy(CollectAreaContainer::getType));
                // 遍历map拼接文本
                oldContainerMap.forEach((k, v) -> {
                    ContainerHandler handler = ContainerHandlerFactory.getHandler(Material.getMaterial(k));
                    assert handler != null;
                    handler.getReloadStatisticsMsg(v.size(), msg, player);
                });
                // 去掉尾部多余的逗号
                msg.deleteCharAt(msg.length() - 1);
            }

            // 输出增加的容器
            if (!newContainerMap.isEmpty()) {
                msg.append(I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.reloadMsg.increase"));
                newContainerMap.forEach((k, v) -> {
                    ContainerHandler handler = ContainerHandlerFactory.getHandler(k);
                    handler.getReloadStatisticsMsg(v.size(), msg, player);
                });
                // 去掉尾部多余的逗号
                msg.deleteCharAt(msg.length() - 1);
            }
            player.sendMessage(ChatColor.GREEN + msg.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.areaCmd.identifyContainerFailed"));
        } finally {
            tx.close();
        }
    }
}
