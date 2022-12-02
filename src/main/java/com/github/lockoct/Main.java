package com.github.lockoct;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.cronjob.AutoCollectJob;
import com.github.lockoct.init.Init;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.CronJobUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Main extends JavaPlugin {
    public static Main plugin;
    private File jarFile;

    @Override
    public void onEnable() {
        plugin = this;
        this.jarFile = getFile();

        // 命令注册
        PluginCommand command = getCommand("materialrepository");
        if (command != null) {
            command.setExecutor(new CommandRouter());
        } else {
            ColorLogUtil.logError(this, "插件启动失败，找不到命令配置，请重新下载本插件");
        }

        // 监听器注册
        Bukkit.getPluginManager().registerEvents(new MarkListener(), this);

        // 数据库表初始化
        Init.initTables();

        // 添加定时任务
        CronJobUtil.setJob(AutoCollectJob.class, "collectItems", "materialRepository", "30 * 19 2 12 ?");

        ColorLogUtil.logSuccess(this, "插件启动成功");
    }

    public File getJarFile() {
        return jarFile;
    }
}
