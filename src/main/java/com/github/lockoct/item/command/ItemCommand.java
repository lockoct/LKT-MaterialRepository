package com.github.lockoct.item.command;

import com.github.lockoct.command.BaseCommand;
import com.github.lockoct.item.listener.ItemListMenuListener;
import com.github.lockoct.menu.ItemListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ItemCommand extends BaseCommand {

    private static ItemCommand instance;

    public static ItemCommand getInstance() {
        if (instance == null) {
            instance = new ItemCommand();
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
        ItemListMenu menu = new ItemListMenu("物料选择菜单", player);
        menu.setItems();
        menu.open(new ItemListMenuListener(menu));
    }

    private void allHelp(Player player) {
        ArrayList<String> helpStrList = new ArrayList<>();
        helpStrList.add("物料插件物料菜单帮助：");
        helpStrList.add("/mr item - 打开物料菜单");
        doHelp(player, helpStrList);
    }
}
