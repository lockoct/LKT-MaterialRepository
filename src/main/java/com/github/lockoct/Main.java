package com.github.lockoct;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.cronjob.AutoCollectJob;
import com.github.lockoct.init.Init;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.CronJobUtil;
import com.github.lockoct.utils.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.quartz.CronExpression;

import java.io.File;
import java.text.ParseException;

public final class Main extends JavaPlugin {
    public static Main plugin;
    private File jarFile;

    @Override
    public void onEnable() {
        plugin = this;
        this.jarFile = getFile();

        if (DatabaseUtil.getDao() == null) {
            ColorLogUtil.logError(this, "插件启动失败，未连接数据库，请开启数据库后再重新加载本插件");
            this.setEnabled(false);
            return;
        }

        // 保存配置文件
        saveDefaultConfig();

        // 命令注册
        PluginCommand command = getCommand("materialrepository");
        if (command != null) {
            CommandRouter cr = new CommandRouter();
            command.setExecutor(cr);
            command.setTabCompleter(cr);
        } else {
            ColorLogUtil.logError(this, "插件启动失败，找不到命令配置，请重新下载本插件");
        }

        // 监听器注册
        Bukkit.getPluginManager().registerEvents(new MarkListener(), this);

        // 数据库表初始化
        Init.initTables();

        // 添加定时任务
        FileConfiguration config = Main.plugin.getConfig();
        String collectItemsJobCornTab = config.getString("cornTab.collectItems");
        if (StringUtils.isNotBlank(collectItemsJobCornTab)) {
            try {
                new CronExpression(collectItemsJobCornTab);
            } catch (ParseException e) {
                collectItemsJobCornTab = "0 0 4 * * ? *";
                ColorLogUtil.logError(this, "cornTab表达式存在问题，已使用默认表达式：0 0 4 * * ? *");
            }
        }
        CronJobUtil.setJob(AutoCollectJob.class, "collectItems", "materialRepository", collectItemsJobCornTab);

        ColorLogUtil.logSuccess(this, "插件启动成功");
    }

    public File getJarFile() {
        return jarFile;
    }
}
