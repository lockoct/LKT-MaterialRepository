package com.github.lockoct.item.task;

import com.github.lockoct.entity.Item;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

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
        Player player = this.player;
        boolean moreThanRepoAmount;
        // 需要获取当前类型最大堆叠数，不能直接用64
        Material material = Material.getMaterial(this.itemInfo.getType());
        assert material != null;
        int maxStackSize = material.getMaxStackSize();
        // 倍率
        int times = this.mode == 1 ? 1 : maxStackSize;
        int amount = this.calcRes * times;
        Item item = dao.query(Item.class, Cnd.where("id", "=", this.itemInfo.getId())).get(0);
        if (item.getAmount() <= 0) {
            player.sendMessage(ChatColor.RED + "该物品暂无库存");
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
            player.sendMessage(ChatColor.RED + "背包空余格子数量不足，请先清理背包后再领取物料");
            return;
        }

        // 在update语句中直接对库存做自减
        // 条件为当前库存数量必须大于拿取数量，用于避免并发操作时库存出现负数
        int res = dao.update(Item.class, Chain.makeSpecial("amount", "-" + amount), Cnd.where("amount", ">=", amount).and("id", "=", item.getId()));
        if (res > 0) {
            // 给玩家背包发放物品
            for (int i = 0; i < needSlot; i++) {
                Material m = Material.getMaterial(item.getType());
                assert m != null;
                ItemStack sendItem = new ItemStack(m);
                if (i + 1 <= groupCount) {
                    sendItem.setAmount(m.getMaxStackSize());
                } else {
                    sendItem.setAmount(remain);
                }

                playerInv.setItem(emptySlotPos.get(i), sendItem);
            }
            if (moreThanRepoAmount) {
                player.sendMessage(ChatColor.YELLOW + "物料库存出现变化，实际领取数量将少于请求数量");
            }
            player.sendMessage(ChatColor.GREEN + "物料领取成功，共计" + groupCount + "组" + remain + "个物品");
        } else {
            player.sendMessage(ChatColor.RED + "该物品库存不足");
        }
    }
}
