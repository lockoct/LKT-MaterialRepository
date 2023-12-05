package com.github.lockoct.area.command.mark.sub;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.entity.MarkData;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class StartSubCommandHandler extends BaseCommandHandler {
    private static StartSubCommandHandler instance;

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
            doHelp(Main.plugin, player, "cmd.markCmd.startCmd.helpMsg");
            return;
        }
        player.sendMessage(I18nUtil.getText(Main.plugin, player, "cmd.markCmd.alreadyInMarkMode"));
        MarkData data = new MarkData();
        data.setPlayer(player);
        data.setMarkStartTime(LocalDateTime.now());
        MarkListener.getMarkModePlayers().put(key, data);
    }
}
