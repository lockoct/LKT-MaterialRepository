package com.github.lockoct.init;

import com.github.lockoct.Main;
import com.github.lockoct.utils.DatabaseUtil;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.resource.Scans;
import org.nutz.resource.impl.JarResourceLocation;

import java.io.IOException;

public class Init {
    public static void initTables() {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            Main plugin = Main.plugin;
            // String[] pkgNames = PathUtil.getEntityParentPackageNames(plugin, plugin.getJarFile());
            try {
                Scans.me().addResourceLocation(new JarResourceLocation(plugin.getJarFile().getAbsolutePath()));
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
}
