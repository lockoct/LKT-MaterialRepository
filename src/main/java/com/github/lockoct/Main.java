package com.github.lockoct;

import com.github.lockoct.area.listener.MarkListener;
import com.github.lockoct.cronjob.AutoCollectJob;
import com.github.lockoct.entity.BasePlugin;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.CronJobUtil;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.JarResourceLocation;
import org.quartz.CronExpression;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public final class Main extends BasePlugin {
    public static Main plugin;
    public static final ArrayList<Material> excludedItems = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        // corePlugin = (BasePlugin) getServer().getPluginManager().getPlugin("LKT-Core");

        // 初始化语言配置
        I18nUtil.init(this);

        if (DatabaseUtil.getDao() == null) {
            ColorLogUtil.logError(this, I18nUtil.getText(this, "pluginMsg.dbConnectFailed"));
            setEnabled(false);
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
            ColorLogUtil.logError(this, I18nUtil.getText(this, "pluginMsg.commandConfigNotFound"));
        }

        // 监听器注册
        Bukkit.getPluginManager().registerEvents(new MarkListener(), this);

        // 数据库表初始化
        initTables();

        // 加载黑名单物品列表
        loadExcludedItems();

        // 添加定时任务
        FileConfiguration config = getConfig();
        String collectItemsJobCornTab = config.getString("cornTab.collectItems");
        if (StringUtils.isNotBlank(collectItemsJobCornTab)) {
            try {
                new CronExpression(collectItemsJobCornTab);
            } catch (ParseException e) {
                collectItemsJobCornTab = "0 0 4 * * ? *";
                ColorLogUtil.logError(this, I18nUtil.getText(this, "pluginMsg.invalidCornTab"));
            }
        }
        CronJobUtil.setJob(AutoCollectJob.class, "collectItems", "materialRepository", collectItemsJobCornTab);

        ColorLogUtil.logSuccess(this, I18nUtil.getText(this, "pluginMsg.enableSuccess"));
    }

    private void initTables() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            // String[] pkgNames = PathUtil.getEntityParentPackageNames(plugin, jarFile);
            try {
                Scans.me().addResourceLocation(new JarResourceLocation(jarFile.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Arrays.stream(pkgNames).forEach(e -> {
            //     Daos.createTablesInPackage(dao, "com.github.lockoct" + e + "entity", false);
            // });

            // 自动建表
            Daos.createTablesInPackage(dao, "com.github.lockoct.entity", false);
            // 自动迁移表结构
            Daos.migration(dao, "com.github.lockoct.entity", true, false, false);
        }
    }

    public void loadExcludedItems() {
        FileConfiguration config = getConfig();
        List<String> excludedItems = config.getStringList("excludedItems");

        excludedItems.forEach(e -> {
            Material m = Material.matchMaterial(e);
            if (m != null) {
                Main.excludedItems.add(m);
            }
        });
    }
}
