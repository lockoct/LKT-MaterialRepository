package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.command.BaseCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelSubCommandHandler extends BaseCommandHandler {
    private static CancelSubCommandHandler instance;

    public static CancelSubCommandHandler getInstance() {
        if (instance == null) {
            instance = new CancelSubCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        boolean res = MarkListener.clearMarkData(player);
        if (res) {
            player.sendMessage("已取消并退出标记模式");
        }
    }
}
