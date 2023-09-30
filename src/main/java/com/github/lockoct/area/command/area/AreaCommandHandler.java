package com.github.lockoct.area.command.area;

import com.github.lockoct.Main;
import com.github.lockoct.area.listener.AreaListMenuListener;
import com.github.lockoct.command.BaseCommandHandler;
import com.github.lockoct.menu.AreaListMenu;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaCommandHandler extends BaseCommandHandler {
    private static AreaCommandHandler instance;

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
            doHelp(Main.plugin, player, "cmd.areaCmd.helpMsg");
            return;
        }

        // 打开菜单
        AreaListMenu menu = new AreaListMenu(I18nUtil.getText(Main.plugin, player, "areaListMenu.title"), player);
        menu.open(new AreaListMenuListener(menu));
    }
}
