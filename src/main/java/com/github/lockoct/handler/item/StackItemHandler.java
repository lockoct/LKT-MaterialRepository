package com.github.lockoct.handler.item;

import com.github.lockoct.Main;
import com.github.lockoct.entity.Item;
import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.NbtApiException;
import com.github.lockoct.nbtapi.iface.ReadWriteNBT;
import com.github.lockoct.utils.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.nutz.dao.Chain;
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

        // 判断当前物品是否有禁止收集属性
        if (getNoCollect()) {
            return false;
        }

        Dao dao = DatabaseUtil.getDao();
        Cnd cnd = Cnd.where("type", "=", itemStack.getType());

        // 检测物品是否有nbt标签
        ReadWriteNBT rwNBT = NBT.itemStackToNBT(itemStack);
        String nbtStr = null;
        String nbtMd5 = null;

        // 检查当前物品nbt是否与原版物品一致
        if (!new ItemStack(itemStack.getType()).isSimilar(itemStack)) {
            nbtStr = rwNBT.toString();

            // 获取nbt字符串md5加密值
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                // 计算哈希值
                // md5加密必须用这个字符串，只包含tag/components里的信息
                String nbtMd5Ori;
                try {
                    // 适配 1.20.5+ 堆叠组件
                    nbtMd5Ori = NBT.modifyComponents(itemStack, Object::toString);
                } catch (NbtApiException e) {
                    // 适配 1.20.5- nbt标签
                    nbtMd5Ori = NBT.get(itemStack, Object::toString);
                }

                byte[] hash = digest.digest(nbtMd5Ori.getBytes(StandardCharsets.UTF_8));
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
        } else {
            cnd.and("nbt_md5", "is", null);
        }

        List<Item> tmpList = dao.query(Item.class, cnd);
        Item item;
        if (tmpList.isEmpty()) {
            item = new Item();
            item.setType(itemStack.getType().toString());
            item.setStack(true);
            item.setAmount(itemStack.getAmount());
            if (StringUtils.isNotBlank(nbtStr)) {
                item.setNbt(nbtStr);
                item.setNbtMd5(nbtMd5);
            }
            dao.insert(item);
        } else {
            item = tmpList.get(0);
            dao.update(Item.class, Chain.makeSpecial("amount", "+" + itemStack.getAmount()), Cnd.where("id", "=", item.getId()));
        }
        return true;
    }
}
