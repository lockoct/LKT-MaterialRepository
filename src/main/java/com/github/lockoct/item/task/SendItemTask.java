package com.github.lockoct.item.task;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.Static;

import java.util.ArrayList;

public class SendItemTask extends BukkitRunnable {
    private final Player player;
    private final Item itemInfo;
    private final int mode;
    private final int calcRes;

    public SendItemTask(Player player, Item itemInfo, int mode, int calcRes) {
        this.player = player;
        this.itemInfo = itemInfo;
        this.mode = mode;
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
        int maxStackSize = material.getMaxStackSize();
        // 倍率
        int times = mode == 1 ? 1 : maxStackSize;
        int amount = calcRes * times;
        Item item = dao.query(Item.class, Cnd.where("id", "=", itemInfo.getId())).get(0);
        if (item.getAmount() <= 0) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.noAmount"));
            return;
        }

        if (amount > item.getAmount()) {
            amount = item.getAmount();
            moreThanRepoAmount = true;
        } else {
            moreThanRepoAmount = false;
        }

        // 检查玩家背包空位
        ArrayList<Integer> emptySlotPos = new ArrayList<>();
        Inventory playerInv = player.getInventory();
        for (int i = 0; i < 36; i++) { // 36及其之后的位置都是装备栏
            if (playerInv.getItem(i) == null) {
                // 排除装备栏
                emptySlotPos.add(i);
            }
        }

        int needSlot = amount / maxStackSize; //需要的背包格子数量
        int groupCount = needSlot;
        int remain = amount % maxStackSize; // 剩余凑不成一组的
        if (remain > 0) {
            needSlot++;
        }

        if (needSlot > emptySlotPos.size()) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.freeSlotLack"));
            return;
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
            // 给玩家背包发放物品
            ItemStack sendItem;
            // 对有nbt标签的物品进行处理
            if (StringUtils.isNotBlank(item.getNbtMd5())) {
                ReadWriteNBT nbt = NBT.parseNBT(item.getNbt());
                sendItem = NBT.itemStackFromNBT(nbt);
            } else {
                Material m = Material.getMaterial(item.getType());
                assert m != null;
                sendItem = new ItemStack(m);
            }

            // 开始发放
            assert sendItem != null;
            for (int i = 0; i < needSlot; i++) {
                if (i + 1 <= groupCount) {
                    sendItem.setAmount(sendItem.getMaxStackSize());
                } else {
                    sendItem.setAmount(remain);
                }
                playerInv.setItem(emptySlotPos.get(i), sendItem);
            }
            if (moreThanRepoAmount) {
                player.sendMessage(ChatColor.YELLOW + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.amountChanged"));
            }
            player.sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.getSuccessful", groupCount, remain));
        } else {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.amountLack"));
        }
    }
}
