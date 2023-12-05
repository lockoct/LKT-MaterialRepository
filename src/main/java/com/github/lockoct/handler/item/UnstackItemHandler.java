package com.github.lockoct.handler.item;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.UnstackItem;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.NBTItem;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
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

        Dao dao = DatabaseUtil.getDao();
        // 先查找item表有没有该物品的入口
        List<Item> tmpList = dao.query(Item.class, Cnd.where("type", "=", itemStack.getType()));
        Item item;
        if (tmpList.isEmpty()) {
            item = new Item();
            item.setType(itemStack.getType().toString());
            item.setStack(false);
        } else {
            item = tmpList.get(0);
        }

        UnstackItem unstackItem = new UnstackItem();

        // 获取物品nbt标签
        ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
        NBTItem nbtItem = new NBTItem(itemStack);

        // 判断当前物品是否有禁止收集nbt属性
        boolean flag = nbtItem.getBoolean("NoCollect");
        if (flag) {
            return false;
        }

        // 实体对象设置nbt标签
        unstackItem.setNbt(nbt.toString());

        NutTxDao tx = new NutTxDao(dao);
        try {
            // 开启事务
            tx.beginRC();

            item.setAmount(item.getAmount() + 1);
            tx.insertOrUpdate(item);

            unstackItem.setItemId(item.getId());
            tx.insert(unstackItem);

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
