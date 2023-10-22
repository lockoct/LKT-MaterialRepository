package com.github.lockoct.cronjob;

import com.github.lockoct.Main;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.Item;
import com.github.lockoct.handler.ContainerHandler;
import com.github.lockoct.handler.ContainerHandlerFactory;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
                                // 获取区域中的容器
                                e = dao.fetchLinks(e, "containers");
                                final CollectArea areaTmp = e;
                                e.getContainers().forEach(c -> {
                                    Block areaBlock = new Location(Bukkit.getWorld(areaTmp.getWorld()), c.getX(), c.getY(), c.getZ()).getBlock();
                                    ContainerHandler handler = ContainerHandlerFactory.getHandler(areaBlock.getType());
                                    if (handler != null) {
                                        handler.collectItem(AutoCollectJob.this::itemsToDB, dao, areaBlock);
                                    }
                                });
                            });
                            ColorLogUtil.logSuccess(Main.plugin, I18nUtil.getText(Main.plugin, "pluginMsg.autoCollectComplete"));
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
