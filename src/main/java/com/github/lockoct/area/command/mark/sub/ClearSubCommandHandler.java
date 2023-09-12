package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.entity.MarkData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearSubCommandHandler extends BaseCommandHandler {
    private static ClearSubCommandHandler instance;

    public static ClearSubCommandHandler getInstance() {
        if (instance == null) {
            instance = new ClearSubCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        int key = player.hashCode();
        MarkData data = MarkListener.getMarkModePlayers().get(key);
        if (data != null) {
            data.setMarkPoint1(null);
            data.setMarkPoint2(null);
            player.sendMessage("已清除选区");
        } else {
            player.sendMessage(ChatColor.RED + "未进入标记模式，请先使用 /mr mark start 进入标记模式标记采集区域");
        }
    }
}
