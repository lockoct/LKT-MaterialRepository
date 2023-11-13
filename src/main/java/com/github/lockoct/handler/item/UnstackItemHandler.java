package com.github.lockoct.handler.item;

import com.github.lockoct.entity.Item;
import com.github.lockoct.entity.UnstackItem;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutTxDao;

import java.util.List;

public class UnstackItemHandler extends ItemHandler {
    @Override
    public boolean execute() {
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

        // 设置nbt标签
        ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
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
