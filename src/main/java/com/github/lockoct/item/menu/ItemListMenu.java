package com.github.lockoct.item.menu;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.UnstackItem;
import com.github.lockoct.item.listener.KeyboardMenuListener;
import com.github.lockoct.item.listener.UnstackItemListMenuListener;
import com.github.lockoct.menu.BaseMenu;
import com.github.lockoct.menu.PageableMenu;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.ColorLogUtil;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemListMenu extends PageableMenu {
    private List<Item> items;

    public ItemListMenu(Player player) {
        super(I18nUtil.getText(Main.plugin, player, "itemListMenu.title"), new HashMap<>(), player, Main.plugin);
    }

    public ItemListMenu(String title, Player player) {
        super(title, new HashMap<>(), player, Main.plugin);
    }

    public ItemListMenu(int currentPage, Player player) {
        super(currentPage, I18nUtil.getText(Main.plugin, player, "itemListMenu.title"), new HashMap<>(), player, Main.plugin);
    }

    public ItemListMenu(int currentPage, String title, Player player) {
        super(currentPage, title, new HashMap<>(), player, Main.plugin);
    }


    @Override
    protected void setPageContent(int page) {
        Dao dao = DatabaseUtil.getDao();
        if (dao != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Pager pager = dao.createPager(page, PAGE_SIZE);
                    items = dao.query(Item.class, Cnd.orderBy().asc("type"), pager);
                    pager.setRecordCount(dao.count(Item.class));
                    setTotalPage(pager.getPageCount());
                    setTotal(pager.getRecordCount());
                    // 设置分页
                    setPageElement();
                    // 填充物品
                    boolean hasItemLoadError = false;
                    for (int i = 0; i < PAGE_SIZE; i++) {
                        Inventory inv = getInventory();
                        if (i < items.size()) {
                            Item tmp = items.get(i);
                            ItemStack is;
                            ItemMeta im;
                            ArrayList<String> loreList = new ArrayList<>();

                            // 对有nbt标签的物品进行处理
                            if (StringUtils.isNotBlank(tmp.getNbtMd5())) {
                                ReadWriteNBT nbt = NBT.parseNBT(tmp.getNbt());
                                is = NBT.itemStackFromNBT(nbt);
                                assert is != null;
                                is.setAmount(1);
                            } else {
                                Material m = Material.getMaterial(tmp.getType());
                                assert m != null;
                                is = new ItemStack(m);
                            }
                            im = is.getItemMeta();
                            if (im == null) {
                                inv.setItem(i, setLoadErrorItem());
                                hasItemLoadError = true;
                                continue;
                            }

                            // 物品数量填在附加信息中
                            int amount = tmp.getAmount();
                            String itemInfo;
                            StringBuilder sb = new StringBuilder(I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.itemInfo.remaining", amount));
                            if (is.getMaxStackSize() == 1) {
                                int specialItemAmount = dao.count(UnstackItem.class, Cnd.where("item_id", "=", tmp.getId()));
                                sb.append(I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.itemInfo.special", amount - specialItemAmount, specialItemAmount));
                                // 这里要重新给库存赋值，为总库存数量 - 特殊物品的库存数量
                                // 是为了进入数量选择菜单后，限制批量获取最大物品数量为常规物品的数量
                                items.get(i).setAmount(amount - specialItemAmount);
                                itemInfo = sb.toString();
                            } else {
                                itemInfo = I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.itemInfo.remaining", amount);
                            }
                            loreList.add(0, itemInfo);
                            im.setLore(loreList);
                            is.setItemMeta(im);
                            inv.setItem(i, is);
                        } else {
                            // 填充空位
                            inv.setItem(i, null);
                        }
                    }
                    // 输出物品加载错误信息
                    if (hasItemLoadError) {
                        ColorLogUtil.logError(Main.plugin, I18nUtil.getText(Main.plugin, "pluginMsg.itemLoadError"));
                    }
                }
            }.runTaskAsynchronously(Main.plugin);
        }
    }

    // 翻页按钮、分页信息
    @Override
    protected void setPageElement() {
        super.setPageElement();

        Inventory inv = getInventory();

        // 获取分页信息元素
        ItemStack is = inv.getItem(49);
        assert is != null;
        ItemMeta im = is.getItemMeta();
        assert im != null;
        // 分页附加信息
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.pageStatisticsInfo", getTotal()));
        im.setLore(loreList);
        is.setItemMeta(im);
        inv.setItem(49, is);
    }

    private ItemStack setLoadErrorItem() {
        ItemStack is = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta im = is.getItemMeta();
        assert im != null;
        im.setDisplayName(I18nUtil.getText(Main.plugin, getPlayer(), "itemListMenu.itemLoadErr", getTotal()));

        NamespacedKey namespacedKey = new NamespacedKey(Main.plugin, "loadErr");
        PersistentDataContainer pdc = im.getPersistentDataContainer();
        // 兼容低版本用Integer
        pdc.set(namespacedKey, PersistentDataType.INTEGER, 1);

        is.setItemMeta(im);
        return is;
    }

    public void toNextMenu(int index) {
        if (index < PAGE_SIZE) {
            HashMap<String, Object> context = getMenuContext();
            Item item = items.get(index);
            // 物品信息
            context.put("itemInfo", item);
            // 列表菜单当前页码
            context.put("fromPage", getCurrentPage());

            BaseMenu menu;
            if (item.isStack()) {
                menu = new KeyboardMenu(context, getPlayer());
                close();
                menu.open(new KeyboardMenuListener(menu));
            } else {
                menu = new UnstackItemListMenu(context, getPlayer());
                close();
                menu.open(new UnstackItemListMenuListener(menu));
            }
        }
    }
}
