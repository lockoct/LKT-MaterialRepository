package com.github.lockoct;

import com.github.lockoct.area.command.area.AreaCommandHandler;
import com.github.lockoct.area.command.mark.MarkCommandHandler;
import com.github.lockoct.command.BaseCommandRouter;
import com.github.lockoct.item.command.ItemCommandHandler;
import com.github.lockoct.utils.ColorLogUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRouter extends BaseCommandRouter {

    public CommandRouter() {
        // 先清除后添加
        helpStrList.clear();
        // 添加帮助信息
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
                    case "mark" -> MarkCommandHandler.getInstance().execute(sender, subCmdArgs);
                    case "item" -> ItemCommandHandler.getInstance().execute(sender, subCmdArgs);
                    case "area" -> AreaCommandHandler.getInstance().execute(sender, subCmdArgs);
                    default -> doHelp(player);
                }
            } else {
                doHelp(player);
            }
        } else {
            ColorLogUtil.logError(Main.plugin, "该命令必须在游戏中执行");
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
