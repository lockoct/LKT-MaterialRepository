package com.github.lockoct.cronjob;

import com.github.lockoct.Main;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.Item;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.List;

public class AutoCollectJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        // 需要在异步方法中调用Bukkit API，必须在里面多套一层同步
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Dao dao = DatabaseUtil.getDao();
                        if (dao != null) {
                            // 获取区域
                            List<CollectArea> caList = dao.query(CollectArea.class, Cnd.where("enabled", "=", 1).and("deleted", "=", 0));
                            caList.forEach(e -> {
                                // 获取区域中的箱子
                                e = dao.fetchLinks(e, "chests");
                                final CollectArea areaTmp = e;
                                e.getChests().forEach(c -> {
                                    Block areaBlock = new Location(Bukkit.getWorld(areaTmp.getWorld()), c.getX(), c.getY(), c.getZ()).getBlock();
                                    if (areaBlock.getType() == Material.CHEST) {
                                        Chest chest = (Chest) areaBlock.getState();
                                        Inventory inv = chest.getBlockInventory();
                                        for (int i = 0; i < inv.getSize(); i++) {
                                            ItemStack is = inv.getItem(i);
                                            if (is != null) {
                                                // 处理可堆叠物品
                                                if (is.getMaxStackSize() > 1) {
                                                    itemsToDB(dao, is);
                                                    // 新增或更新后去除原本箱子中的物品
                                                    inv.clear(i);
                                                } else {
                                                    // 不可堆叠物品目前仅支持不死图腾
                                                    if (is.getType() == Material.TOTEM_OF_UNDYING) {
                                                        itemsToDB(dao, is);
                                                        // 新增或更新后去除原本箱子中的物品
                                                        inv.clear(i);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            });
                            ColorLogUtil.logSuccess(Main.plugin, "所有采集任务已执行完毕");
                        }
                    }
                }.runTask(Main.plugin);
            }
        }.runTaskAsynchronously(Main.plugin);
    }

    // 新增或更新物品到数据库中
    private void itemsToDB(Dao dao, ItemStack is) {
        List<Item> tmpList = dao.query(Item.class, Cnd.where("type", "=", is.getType()));
        Item item;
        if (tmpList.isEmpty()) {
            item = new Item();
        } else {
            item = tmpList.get(0);
        }
        item.setAmount(item.getAmount() + is.getAmount());
        item.setType(is.getType().toString());
        dao.insertOrUpdate(item);
    }
}
