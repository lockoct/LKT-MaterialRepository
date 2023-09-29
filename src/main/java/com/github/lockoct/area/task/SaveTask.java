package com.github.lockoct.area.task;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
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
                player.sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.saveSuccessful"));
            }
            tx.commit();
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.saveFailed"));
        } finally {
            tx.close();
            MarkListener.clearMarkData(player);
        }
    }
}
