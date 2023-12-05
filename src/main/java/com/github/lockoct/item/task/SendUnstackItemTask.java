package com.github.lockoct.item.task;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.UnstackItem;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import com.github.lockoct.utils.I18nUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutTxDao;

import java.util.ArrayList;
import java.util.List;

public class SendUnstackItemTask extends BukkitRunnable {
    private final Player player;
    private final List<UnstackItem> selectedItemList;

    public SendUnstackItemTask(Player player, List<UnstackItem> selectedItemList) {
        this.player = player;
        this.selectedItemList = selectedItemList;
    }

    @Override
    public void run() {
        Dao dao = DatabaseUtil.getDao();
        if (dao == null) {
            return;
        }

        // 检查玩家背包空位
        ArrayList<Integer> emptySlotPos = new ArrayList<>();
        Inventory playerInv = player.getInventory();
        for (int i = 0; i < 36; i++) { // 36及其之后的位置都是装备栏
            if (playerInv.getItem(i) == null) {
                emptySlotPos.add(i);
            }
        }

        if (selectedItemList.size() > emptySlotPos.size()) {
            player.sendMessage(ChatColor.RED + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.freeSlotLack"));
            return;
        }

        int getFailItemCount = 0;

        // 给玩家背包发放物品
        for (UnstackItem e : selectedItemList) {
            Cnd cnd = Cnd.where("id", "=", e.getId());
            int count = dao.count(UnstackItem.class, cnd);
            if (count == 0) {
                getFailItemCount++;
                continue;
            }

            NutTxDao tx = new NutTxDao(dao);
            int res1;
            try {
                tx.beginRC();
                res1 = dao.delete(e);
                if (res1 > 0) {
                    // 更新库存数量
                    dao.update(Item.class, Chain.makeSpecial("amount", "-" + 1), Cnd.where("amount", ">=", 1).and("id", "=", e.getItemId()));
                }
            } catch (Throwable t) {
                t.printStackTrace();
                tx.rollback();
                return;
            } finally {
                tx.close();
            }

            if (res1 > 0) {
                ReadWriteNBT nbt = NBT.parseNBT(e.getNbt());
                ItemStack sendItem = NBT.itemStackFromNBT(nbt);

                // 开始发放
                playerInv.setItem(emptySlotPos.get(0), sendItem);
                emptySlotPos.remove(0);

            } else {
                getFailItemCount++;
            }
        }

        if (getFailItemCount > 0) {
            player.sendMessage(ChatColor.YELLOW + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.amountChanged"));
        }
        player.sendMessage(ChatColor.GREEN + I18nUtil.getText(Main.plugin, player, "cmd.itemCmd.getSuccessful", 0, selectedItemList.size() - getFailItemCount));
    }
}
