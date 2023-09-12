package com.github.lockoct.area.command.area;

import com.github.lockoct.area.listener.AreaListMenuListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.menu.AreaListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaCommandHandler extends BaseCommandHandler {
    private static AreaCommandHandler instance;

    public AreaCommandHandler() {
        // 先清除后添加
        helpStrList.clear();
        // 添加帮助信息
        helpStrList.add("物料插件区域管理菜单帮助：");
        helpStrList.add("/mr area - 打开区域管理菜单");
    }

    public static AreaCommandHandler getInstance() {
        if (instance == null) {
            instance = new AreaCommandHandler();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        // 输出帮助
        if (args.length > 0) {
            doHelp(player);
            return;
        }

        // 打开菜单
        AreaListMenu menu = new AreaListMenu("区域管理菜单", player);
        menu.open(new AreaListMenuListener(menu));
    }
}
