package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.entity.MarkData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class StartSubCommandHandler extends BaseCommandHandler {
    private static StartSubCommandHandler instance;

    public StartSubCommandHandler() {
        // 先清除后添加
        helpStrList.clear();
        // 添加帮助信息
        helpStrList.add("介绍：开启标记模式，在标记模式下可划定物料采集区域");
        helpStrList.add("命令：/mr mark start");
    }

    public static StartSubCommandHandler getInstance() {
        if (instance == null) {
            instance = new StartSubCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        int key = player.hashCode();
        //
        if (args.length > 1) {
            doHelp(player);
            return;
        }
        player.sendMessage("已进入标记模式，请使用左右键分别选取两个点，两个点之间形成立方体为采集区域");
        MarkData data = new MarkData();
        data.setPlayer(player);
        data.setMarkStartTime(LocalDateTime.now());
        MarkListener.getMarkModePlayers().put(key, data);
    }
}
