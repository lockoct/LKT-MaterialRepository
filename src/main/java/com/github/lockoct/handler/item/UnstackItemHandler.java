package com.github.lockoct.handler.item;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.UnstackItem;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import org.bukkit.inventory.ItemStack;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutTxDao;

import java.util.List;

public class UnstackItemHandler extends ItemHandler {
    @Override
    public boolean execute() {
        // 判断当前物品是否在物品黑名单中
        if (Main.excludedItems.contains(itemStack.getType())) {
            return false;
        }

        // 判断当前物品是否有禁止收集属性
        if (getNoCollect()) {
            return false;
        }

        Dao dao = DatabaseUtil.getDao();
        // 先查找item表有没有该物品的入口
        List<Item> tmpList = dao.query(Item.class, Cnd.where("type", "=", itemStack.getType()));

        UnstackItem unstackItem = new UnstackItem();

        // 获取物品nbt标签
        ReadWriteNBT rwNBT = NBT.itemStackToNBT(itemStack);

        // 实体对象设置nbt标签
        unstackItem.setNbt(rwNBT.toString());

        NutTxDao tx = new NutTxDao(dao);
        try {
            // 开启事务
            tx.beginRC();

            Item item;
            if (tmpList.isEmpty()) {
                item = new Item();
                item.setType(itemStack.getType().toString());
                item.setStack(false);
                item.setAmount(itemStack.getAmount());
                tx.insert(item);
            } else {
                item = tmpList.get(0);
                tx.update(Item.class, Chain.makeSpecial("amount", "+" + 1), Cnd.where("id", "=", item.getId()));
            }

            // 检查当前物品nbt是否与默认物品一致
            if (!new ItemStack(itemStack.getType()).isSimilar(itemStack)) {
                unstackItem.setItemId(item.getId());
                tx.insert(unstackItem);
            }

            tx.commit();
        } catch (Throwable e) {
            e.printStackTrace();
            tx.rollback();
            return false;
        } finally {
            tx.close();
        }

        return true;
    }
}
