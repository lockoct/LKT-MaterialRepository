package com.github.lockoct;

import com.github.lockoct.command.BaseCommandExecutor;
import com.github.lockoct.area.command.MarkCommand;
import com.github.lockoct.item.command.ItemCommand;
import com.github.lockoct.utils.ColorLogUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRouter extends BaseCommandExecutor implements TabExecutor {
    private static final ArrayList<String> helpStrList;

    static {
        helpStrList = new ArrayList<>();
        helpStrList.add("物料插件命令帮助：");
        helpStrList.add("/mr mark - 标记采集区域命令");
        helpStrList.add("/mr item - 打开物料菜单");
        helpStrList.add("/mr area - 打开区域管理菜单");
        helpStrList.add("选择输入任意子命令，可查看详细用法。");
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (sender instanceof Player player) {
            if (args.length > 0) {
                String[] subCmdArgs = Arrays.copyOfRange(args, 1, args.length);
                switch (args[0]) {
                    case "mark":
                        MarkCommand.getInstance().execute(sender, subCmdArgs);
                        break;
                    case "item":
                        ItemCommand.getInstance().execute(sender, subCmdArgs);
                        break;
                    case "area":
                        break;
                    default:
                        doHelp(player, helpStrList);
                }
            } else {
                doHelp(player, helpStrList);
            }
        } else {
            ColorLogUtil.logError(Main.plugin, "该命令必须在游戏中执行");
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        return null;
    }
}
