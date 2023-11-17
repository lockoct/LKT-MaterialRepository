package com.github.lockoct.handler.item;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.NBTItem;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class StackItemHandler extends ItemHandler {
    @Override
    public boolean execute() {
        // 判断当前物品是否在物品黑名单中
        if (Main.excludedItems.contains(itemStack.getType())) {
            return false;
        }

        Dao dao = DatabaseUtil.getDao();
        Cnd cnd = Cnd.where("type", "=", itemStack.getType());

        // 检测物品是否有nbt标签
        ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
        NBTItem nbtItem = new NBTItem(itemStack);
        String nbtStr = null;
        String nbtMd5 = null;
        if (nbtItem.hasNBTData()) {
            nbtStr = nbt.toString();

            // 判断当前物品是否有禁止收集nbt属性
            boolean flag = nbtItem.getBoolean("NoCollect");
            if (flag) {
                return false;
            }

            // 获取nbt字符串md5加密值
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                // 计算哈希值
                byte[] hash = digest.digest(nbtStr.getBytes(StandardCharsets.UTF_8));
                // 将哈希值转换为十六进制字符串
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
                nbtMd5 = hexString.toString();
                cnd.and("nbt_md5", "=", nbtMd5);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            }
        }

        List<Item> tmpList = dao.query(Item.class, cnd);
        Item item;
        if (tmpList.isEmpty()) {
            item = new Item();
            item.setType(itemStack.getType().toString());
            item.setStack(true);
            if (StringUtils.isNotBlank(nbtStr)) {
                item.setNbt(nbtStr);
                item.setNbtMd5(nbtMd5);
            }
        } else {
            item = tmpList.get(0);
        }
        item.setAmount(item.getAmount() + itemStack.getAmount());
        dao.insertOrUpdate(item);
        return true;
    }
}
