package com.github.lockoct.area.task;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutTxDao;

public class SaveTask extends BukkitRunnable {

    private final CollectArea ca;
    private final Player player;

    public SaveTask(Player player, CollectArea ca) {
        this.player = player;
        this.ca = ca;
    }

    @Override
    public void run() {
        Dao dao = DatabaseUtil.getDao();
        if (dao == null) {
            return;
        }
        NutTxDao tx = new NutTxDao(dao);
        try {
            tx.beginRC();
            CollectArea caTmp = tx.insertWith(ca, "chests");
            if (caTmp != null) {
                this.player.sendMessage(ChatColor.GREEN + "采集区域保存成功，已退出标记模式");
            }
            tx.commit();
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
            this.player.sendMessage(ChatColor.RED + "采集区域保存失败，已退出标记模式");
        } finally {
            tx.close();
            MarkListener.clearMarkData(this.player);
        }
    }
}
