package com.github.lockoct;

import com.github.lockoct.area.command.area.AreaCommandHandler;
import com.github.lockoct.area.command.mark.MarkCommandHandler;
import com.github.lockoct.command.BaseCommandRouter;
import com.github.lockoct.item.command.ItemCommandHandler;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRouter extends BaseCommandRouter {

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                String[] subCmdArgs = Arrays.copyOfRange(args, 1, args.length);
                switch (args[0]) {
                    case "mark" -> MarkCommandHandler.getInstance().execute(sender, subCmdArgs);
                    case "item" -> ItemCommandHandler.getInstance().execute(sender, subCmdArgs);
                    case "area" -> AreaCommandHandler.getInstance().execute(sender, subCmdArgs);
                    default -> doHelp(Main.plugin, player, "cmd.helpMsg");
                }
            } else {
                doHelp(Main.plugin, player, "cmd.helpMsg");
            }
        } else {
            ColorLogUtil.logError(Main.plugin, I18nUtil.getText(Main.plugin, "pluginMsg.commandNotExecInGame"));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        List<String> cmdList = null;
        switch (args.length) {
            case 1:
                cmdList = new ArrayList<>(Arrays.asList("mark", "item", "area"));
                if (StringUtils.isNotBlank(args[0])) {
                    cmdList.removeIf(e -> !e.startsWith(args[0].toLowerCase()));
                }
                break;
            case 2:
                if ("mark".equals(args[0])) {
                    cmdList = MarkCommandHandler.getSubCommandList();
                    if (StringUtils.isNotBlank(args[1])) {
                        cmdList.removeIf(e -> !e.startsWith(args[1].toLowerCase()));
                    }
                }
                break;
        }
        return cmdList;
    }
}
