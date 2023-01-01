package com.github.lockoct.area.command;

import com.github.lockoct.area.listener.AreaListMenuListener;
import com.github.lockoct.command.BaseCommand;
import com.github.lockoct.menu.AreaListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AreaCommand extends BaseCommand {
    private static AreaCommand instance;

    public static AreaCommand getInstance() {
        if (instance == null) {
            instance = new AreaCommand();
        }
        return instance;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length > 0) {
            allHelp(player);
            return;
        }
        AreaListMenu menu = new AreaListMenu("区域管理菜单", player);
        menu.setAreaItems();
        menu.open(new AreaListMenuListener(menu));
    }

    private void allHelp(Player player) {
        ArrayList<String> helpStrList = new ArrayList<>();
        helpStrList.add("物料插件区域管理菜单帮助：");
        helpStrList.add("/mr area - 打开区域管理菜单");
        doHelp(player, helpStrList);
    }
}
