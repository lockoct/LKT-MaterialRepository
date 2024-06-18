package com.github.lockoct.item.command;

import com.github.lockoct.Main;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.item.listener.ItemListMenuListener;
import com.github.lockoct.item.menu.ItemListMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemCommandHandler extends BaseCommandHandler {
    private static ItemCommandHandler instance;

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
            doHelp(Main.plugin, player, "cmd.itemCmd.helpMsg");
            return;
        }
        ItemListMenu menu = new ItemListMenu(player);
        menu.open(new ItemListMenuListener(menu));
    }
}
