package com.github.lockoct.handler.item;

import com.github.lockoct.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

public class ItemHandlerFactory {
    public static ItemHandler getHandler(ItemStack itemStack) {
        ItemHandler handler;
        if (itemStack.getMaxStackSize() == 1) {
            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasNBTData()) {
                handler = new UnstackItemHandler();
            } else {
                handler = new StackItemHandler();
            }
        } else {
            handler = new StackItemHandler();
        }
        handler.itemStack = itemStack;
        return handler;
    }
}
