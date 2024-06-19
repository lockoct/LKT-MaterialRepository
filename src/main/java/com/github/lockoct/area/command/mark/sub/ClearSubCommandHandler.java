package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.utils.I18nUtil;
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
        MarkData data = MarkListener.getMarkModePlayers().get(player);
        if (data == null) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.markCmd.clearCmd.selectionCleared"));
            return;
        }
        data.setMarkPoint1(null);
        data.setMarkPoint2(null);
        player.sendMessage(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.clearCmd.selectionCleared"));
    }
}
