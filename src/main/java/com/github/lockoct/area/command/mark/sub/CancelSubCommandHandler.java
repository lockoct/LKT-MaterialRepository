package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.utils.I18nUtil;
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
            player.sendMessage(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.cancelCmd.exitMarkMode"));
        }
    }
}
