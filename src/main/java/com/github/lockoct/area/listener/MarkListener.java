package com.github.lockoct.area.listener;

import com.github.lockoct.Main;
import com.github.lockoct.area.task.CalcAreaTask;
import com.github.lockoct.entity.MarkData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

public class MarkListener implements Listener {
    private static HashMap<Integer, MarkData> markModePlayers = new HashMap<>();

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        // 查找进入标记状态玩家
        Player player = e.getPlayer();
        MarkData data = getMarkModePlayers().get(player.hashCode());
        if (data != null) {
            Block block = e.getClickedBlock();
            Action action = e.getAction();
            // 玩家点击的必须是方块，且不能是空气，不能是流体
            if (block != null && !block.isEmpty() && !block.isLiquid()) {
                // 检查玩家动作，必须是左键点击方块或右键点击方块，且右键点击方块时必须是主手
                int positionNum = 1;
                if (!action.equals(Action.LEFT_CLICK_BLOCK)) {
                    if (action.equals(Action.RIGHT_CLICK_BLOCK) && EquipmentSlot.HAND.equals(e.getHand())) {
                        positionNum = 2;
                        data.setMarkPoint2(block.getLocation());
                    } else {
                        return;
                    }
                } else {
                    data.setMarkPoint1(block.getLocation());
                }

                player.sendMessage(ChatColor.LIGHT_PURPLE + "已选中第" + positionNum + "个标记点[" + block.getX() + ", " + block.getY() + ", " + block.getZ() + "]");

                // 选中两个标记点后计算范围大小，查找范围中的箱子数量
                Location point1 = data.getMarkPoint1();
                Location point2 = data.getMarkPoint2();
                if (point1 != null && point2 != null) {
                    // 如果之前存在正在计算的线程，需要先取消
                    if (data.getCalcTaskId() > 0) {
                        Bukkit.getScheduler().cancelTask(data.getCalcTaskId());
                    }
                    // 计算可能属于耗时操作，需要异步操作
                    CalcAreaTask task = new CalcAreaTask(point1, point2, player);
                    int taskId = task.runTaskAsynchronously(Main.plugin).getTaskId();
                    data.setCalcTaskId(taskId);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        clearMarkData(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        clearMarkData(e.getPlayer());
    }

    // 清除玩家标记数据
    public static boolean clearMarkData(Player player) {
        int key = player.hashCode();
        MarkData data = markModePlayers.get(key);
        if (data != null) {
            int taskId = data.getCalcTaskId();
            // 检查是否有还在运行的计算线程，如果有就停止线程
            if (taskId > 0) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
            markModePlayers.remove(key);
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "未进入标记模式，请先使用 /mr mark start 进入标记模式标记采集区域");
            return false;
        }
    }

    public static HashMap<Integer, MarkData> getMarkModePlayers() {
        if (markModePlayers == null) {
            markModePlayers = new HashMap<>();
        }
        return markModePlayers;
    }
}
