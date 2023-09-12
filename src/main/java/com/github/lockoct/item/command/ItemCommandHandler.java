package com.github.lockoct.item.command;

import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.item.listener.ItemListMenuListener;
import com.github.lockoct.menu.ItemListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemCommandHandler extends BaseCommandHandler {
    private static ItemCommandHandler instance;

    public ItemCommandHandler() {
        // 先清除后添加
        helpStrList.clear();
        // 添加帮助信息
        helpStrList.add("物料插件物料菜单帮助：");
        helpStrList.add("/mr item - 打开物料菜单");
    }

    public static ItemCommandHandler getInstance() {
        if (instance == null) {
            instance = new ItemCommandHandler();
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
        ItemListMenu menu = new ItemListMenu("物料选择菜单", player);
        menu.open(new ItemListMenuListener(menu));
    }
}
