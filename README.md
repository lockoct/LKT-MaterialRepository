# LKT-MaterialRepository

一个使用数据库存取Minecraft物品的Spigot插件

### 功能演示

介绍视频：https://www.bilibili.com/video/BV1uu4y1y7BV

### 特色

* 自动分类：你再也不需要给生电机器搭建分类装置了
* 自动打包：拿取物品时提供潜影盒，插件自动将物品打包到潜影盒中，再也不用搭建打包机了
* 超大容量：不用担心产物箱子爆满，再多的数量，对数据库来说只是一串数字
* 随时随地拿取物品：无需前往不同的生电机器所在地拿取物品，你甚至直接可以在家里拿到远在天边的机器的产物

### 基本功能

* 定时将指定区域内的箱子中的物品存储到数据库中
* 以GUI菜单的方式选择并获取物品
* 以GUI菜单的方式对划定的区域进行管理

> 功能使用细节请参考[插件wiki](https://github.com/lockoct/LKT-MaterialRepository/wiki/%E4%BD%BF%E7%94%A8%E6%89%8B%E5%86%8C)

### 安装

1. 下载本插件前置插件 [LKT-Core](https://github.com/lockoct/LKT-Core/releases)
2. 将前置插件和本插件放入服务端 **plugins** 文件夹
3. 启动服务端
4. 前置插件配置数据库连接
5. 重新加载服务器

> 具体安装步骤请见[插件wiki](https://github.com/lockoct/LKT-MaterialRepository/wiki/%E5%AE%89%E8%A3%85)

### 基本指令

* 标记功能

```text
/mr mark start：开启标记模式
/mr mark cancel：退出标记模式（不保存区域）
/mr mark clear：清除已标记的选区
/mr mark save 区域名称：保存区域
```

* 物品获取功能

```text
/mr item：打开物品列表菜单
```

* 区域管理功能

```text
/mr area：打开区域列表菜单
```

### 支持版本

1.20.X、1.19.X
