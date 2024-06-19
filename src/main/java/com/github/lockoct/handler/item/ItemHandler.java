package com.github.lockoct.handler.item;

import com.github.lockoct.nbtapi.NBT;
import com.github.lockoct.nbtapi.NbtApiException;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public abstract class ItemHandler {
    protected ItemStack itemStack;

    public abstract boolean execute();

    protected boolean getNoCollect() {
        try {
            // 适配 1.20.5+ 堆叠组件
            return NBT.modifyComponents(itemStack, nbt -> {
                return Optional.ofNullable(nbt.getCompound("minecraft:custom_data"))
                    .map(customData -> customData.getBoolean("NoCollect"))
                    .orElse(false);
            });
        } catch (NbtApiException e) {
            // 适配 1.20.5- nbt标签
            return NBT.get(itemStack, nbt -> {
                return nbt.getBoolean("NoCollect");
            });
        }
    }
}
