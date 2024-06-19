package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.area.task.SaveTask;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.CollectAreaContainer;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SaveSubCommandHandler extends BaseCommandHandler {
    private static SaveSubCommandHandler instance;

    public static SaveSubCommandHandler getInstance() {
        if (instance == null) {
            instance = new SaveSubCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        // 输出帮助
        if (args.length != 2) {
            doHelp(Main.plugin, player, "cmd.markCmd.saveCmd.helpMsg");
            return;
        }

        MarkData data = MarkListener.getMarkModePlayers().get(player);
        if (data == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.notInMarkMode"));
            return;
        }

        // 将区域信息转换为java bean
        Location point1 = data.getMarkPoint1();
        Location point2 = data.getMarkPoint2();
        String playerId = player.getUniqueId().toString();

        // 检查合法性
        if (point1 == null || point2 == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.markPointSelectNotComplete"));
            return;
        }

        if (point1.getWorld() == null || point2.getWorld() == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.cannotGetMarkPointWorld"));
            return;
        }

        if (!point1.getWorld().equals(point2.getWorld())) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.saveCmd.markPointsInDifferentWorld"));
            return;
        }

        // 提交采集区域信息
        CollectArea ca = new CollectArea();
        ca.setName(args[1]);
        ca.setWorld(point1.getWorld().getName());
        ca.setX1(point1.getBlockX());
        ca.setY1(point1.getBlockY());
        ca.setZ1(point1.getBlockZ());
        ca.setX2(point2.getBlockX());
        ca.setY2(point2.getBlockY());
        ca.setZ2(point2.getBlockZ());
        ca.setDeleted(false);
        ca.setEnabled(true);
        ca.setCreateUser(playerId);
        ca.setUpdateUser(playerId);

        // 将区域内容器信息转换为java bean
        ArrayList<CollectAreaContainer> cacList = new ArrayList<>();
        data.getContainerLocation().forEach((k, v) -> v.forEach(e -> {
            CollectAreaContainer cac = new CollectAreaContainer();
            cac.setType(k.toString());
            cac.setX(e.getBlockX());
            cac.setY(e.getBlockY());
            cac.setZ(e.getBlockZ());
            cac.setCreateUser(playerId);
            cac.setUpdateUser(playerId);
            cacList.add(cac);
        }));

        ca.setContainers(cacList);

        // 防止重复建立保存线程任务
        if (data.getSaveTaskId() > 0) {
            return;
        }
        SaveTask task = new SaveTask(player, ca);
        int taskId = task.runTaskAsynchronously(Main.plugin).getTaskId();
        data.setSaveTaskId(taskId);
    }
}
