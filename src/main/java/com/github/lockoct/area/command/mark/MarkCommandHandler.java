package com.github.lockoct.area.command.mark;

import com.github.lockoct.Main;
import com.github.lockoct.area.command.mark.sub.CancelSubCommandHandler;
import com.github.lockoct.area.command.mark.sub.ClearSubCommandHandler;
import com.github.lockoct.area.command.mark.sub.SaveSubCommandHandler;
import com.github.lockoct.area.command.mark.sub.StartSubCommandHandler;
import com.github.lockoct.command.BaseCommandHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkCommandHandler extends BaseCommandHandler {
    private static MarkCommandHandler instance;
    private static final List<String> subCommandList = new ArrayList<>(Arrays.asList("start", "cancel", "clear", "save"));

    public static MarkCommandHandler getInstance() {
        if (instance == null) {
            instance = new MarkCommandHandler();
        }
        return instance;
    }

    public static List<String> getSubCommandList() {
        return new ArrayList<>(subCommandList);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length > 0) {
            switch (args[0]) {
                case "start" -> StartSubCommandHandler.getInstance().execute(sender, args);
                case "cancel" -> CancelSubCommandHandler.getInstance().execute(sender, args);
                case "clear" -> ClearSubCommandHandler.getInstance().execute(sender, args);
                case "save" -> SaveSubCommandHandler.getInstance().execute(sender, args);
                default -> doHelp(Main.plugin, player, "cmd.markCmd.helpMsg");
            }
        } else {
            doHelp(Main.plugin, player, "cmd.markCmd.helpMsg");
        }
    }
}
