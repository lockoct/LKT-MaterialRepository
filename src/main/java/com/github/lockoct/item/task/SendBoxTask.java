package com.github.lockoct.item.task;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.ShulkerBoxPlaceMenuData;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.Static;

import java.util.HashMap;

public class SendBoxTask extends BukkitRunnable {
    private final Player player;
    private final Item itemInfo;
    private final int calcRes;
    private final HashMap<Integer, ShulkerBoxPlaceMenuData> shulkerBoxMap;

    public SendBoxTask(Player player, Item itemInfo, HashMap<Integer, ShulkerBoxPlaceMenuData> shulkerBoxMap, int calcRes) {
        this.player = player;
        this.itemInfo = itemInfo;
        this.shulkerBoxMap = shulkerBoxMap;
        this.calcRes = calcRes;
    }

    @Override
    public void run() {
        Dao dao = DatabaseUtil.getDao();
        if (dao == null) {
            return;
        }
        boolean moreThanRepoAmount;
        // 需要获取当前类型最大堆叠数，不能直接用64
        Material material = Material.getMaterial(itemInfo.getType());
        assert material != null;
        // 倍率
        int times = material.getMaxStackSize() * 27;
        int amount = calcRes * times;
        Item item = dao.query(Item.class, Cnd.where("id", "=", itemInfo.getId())).get(0);
        if (item.getAmount() <= 0) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.noAmount"));
            return;
        }

        int needBox = calcRes;
        // 当需求数量大于库存数量时，对获取物品数量进行修正
        if (amount > item.getAmount()) {
            needBox = item.getAmount() / times; //需要的潜影盒数量
            amount = needBox * times;
            moreThanRepoAmount = true;
        } else {
            moreThanRepoAmount = false;
        }

        // 在update语句中直接对库存做自减
        // 条件为当前库存数量必须大于拿取数量，用于避免并发操作时库存出现负数
        int res = dao.update(
            Item.class,
            Chain.makeSpecial("amount", "-" + amount),
            Cnd.where("id", "=", item.getId())
                .and(new Static("amount - (select count(*) from mr_unstack_item where item_id = '" + item.getId() + "') > " + amount))
        );
        if (res > 0) {
            // 往潜影盒添加物品
            for (int i = 0; i < Math.min(shulkerBoxMap.size(), needBox); i++) {
                PlayerInventory playerInv = player.getInventory();
                shulkerBoxMap.forEach((k, v) -> {
                    ItemStack is = playerInv.getItem(v.getFromPos());
                    if (is != null) {
                        BlockStateMeta bsm = (BlockStateMeta) is.getItemMeta();
                        assert bsm != null;
                        ShulkerBox box = (ShulkerBox) bsm.getBlockState();
                        Inventory boxInv = box.getInventory();

                        // 对有nbt标签的物品进行处理
                        if (StringUtils.isNotBlank(item.getNbtMd5())) {
                            ReadWriteNBT nbt = NBT.parseNBT(item.getNbt());
                            ItemStack tmpIs = NBT.itemStackFromNBT(nbt);
                            assert tmpIs != null;
                            for (int j = 0; j < boxInv.getSize(); j++) {
                                tmpIs.setAmount(tmpIs.getMaxStackSize());
                                boxInv.addItem(tmpIs);
                            }
                        } else {
                            Material m = Material.getMaterial(item.getType());
                            assert m != null;
                            ItemStack tmpIs = new ItemStack(m);
                            for (int j = 0; j < boxInv.getSize(); j++) {
                                tmpIs.setAmount(m.getMaxStackSize());
                                boxInv.addItem(tmpIs);
                            }
                        }

                        bsm.setBlockState(box);
                        is.setItemMeta(bsm);
                    }
                    playerInv.setItem(v.getFromPos(), is);
                });
            }
            if (moreThanRepoAmount) {
                player.sendMessage(ChatColor.YELLOW + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.amountChanged"));
            }
            player.sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.getByBoxSuccessful", needBox));
        } else {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.amountLack"));
        }
    }
}
