package com.github.lockoct.area.command;

import com.github.lockoct.Main;
import com.github.lockoct.command.BaseCommand;
import com.github.lockoct.entity.CollectArea;
import com.github.lockoct.entity.CollectAreaChest;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.area.task.SaveTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MarkCommand extends BaseCommand {
    private static MarkCommand instance;

    public static MarkCommand getInstance() {
        if (instance == null) {
            instance = new MarkCommand();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        int key = player.hashCode();
        if (args.length > 0) {
            switch (args[0]) {
                case "start" -> {
                    if (args.length > 1) {
                        markStartHelp(player);
                        break;
                    }
                    player.sendMessage("已进入标记模式，请使用左右键分别选取两个点，两个点之间形成立方体为采集区域");
                    MarkData data = new MarkData();
                    data.setPlayer(player);
                    data.setMarkStartTime(LocalDateTime.now());
                    MarkListener.getMarkModePlayers().put(key, data);
                } case "cancel" -> {
                    MarkListener.clearMarkData(player);
                    player.sendMessage("已取消并退出标记模式");
                } case "clear" -> {
                    MarkData data = MarkListener.getMarkModePlayers().get(key);
                    data.setMarkPoint1(null);
                    data.setMarkPoint2(null);
                    player.sendMessage("已清除选区");
                } case "save" -> {
                    if (args.length != 2) {
                        markSaveHelp(player);
                        break;
                    }
                    MarkData data = MarkListener.getMarkModePlayers().get(key);
                    if (data != null) {
                        // 将区域信息转换为java bean
                        Location point1 = data.getMarkPoint1();
                        Location point2 = data.getMarkPoint2();
                        String playerId = player.getUniqueId().toString();
                        // 检查合法性
                        if (point1 != null && point2 != null) {
                            if (point1.getWorld() != null && point2.getWorld() != null) {
                                if (!point1.getWorld().equals(point2.getWorld())) {
                                    player.sendMessage(ChatColor.RED + "两个标记点不在同一维度，请重新标记");
                                    break;
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "无法获取标记点所在世界");
                                break;
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "标记点未选取完成，请选取两个标记点后再进行保存");
                            break;
                        }

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

                        // 将区域内箱子信息转换为java bean
                        ArrayList<Location> chestLocationList = data.getChestLocation();
                        ArrayList<CollectAreaChest> cacList = new ArrayList<>();
                        chestLocationList.forEach(e -> {
                            CollectAreaChest cac = new CollectAreaChest();
                            cac.setX(e.getBlockX());
                            cac.setY(e.getBlockY());
                            cac.setZ(e.getBlockZ());
                            cac.setCreateUser(playerId);
                            cac.setUpdateUser(playerId);
                            cacList.add(cac);
                        });
                        ca.setChests(cacList);

                        // 防止重复建立保存线程任务
                        if (data.getSaveTaskId() > 0) {
                            break;
                        }
                        SaveTask task = new SaveTask(player, ca);
                        int taskId = task.runTaskAsynchronously(Main.plugin).getTaskId();
                        data.setSaveTaskId(taskId);
                    } else {
                        player.sendMessage(ChatColor.RED + "未进入标记模式，请先使用 /mr mark start 进入标记模式标记采集区域");
                    }
                } default -> allHelp(player);
            }
        } else {
            allHelp(player);
        }
    }

    private void markStartHelp(Player player) {
        ArrayList<String> helpStrList = new ArrayList<>();
        helpStrList.add("介绍：开启标记模式，在标记模式下可划定物料采集区域");
        helpStrList.add("命令：/mr mark start");
        doHelp(player, helpStrList);
    }

    private void markSaveHelp(Player player) {
        ArrayList<String> helpStrList = new ArrayList<>();
        helpStrList.add("介绍：保存标记区域，插件后续将从该区域中的箱子采集物品");
        helpStrList.add("命令：/mr mark save 区域名称");
        doHelp(player, helpStrList);
    }

    private void allHelp(Player player) {
        ArrayList<String> helpStrList = new ArrayList<>();
        helpStrList.add("物料插件标记命令帮助：");
        helpStrList.add("/mr mark start - 开启标记模式");
        helpStrList.add("/mr mark cancel - 不保存退出标记模式");
        helpStrList.add("/mr mark save 区域名称 - 保存区域");
        doHelp(player, helpStrList);
    }
}
