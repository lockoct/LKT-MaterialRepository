package com.github.lockoct.handler.item;

import org.bukkit.inventory.ItemStack;

public class ItemHandlerFactory {
    public static ItemHandler getHandler(ItemStack itemStack) {
        ItemHandler handler;
        if (itemStack.getMaxStackSize() == 1) {
            handler = new UnstackItemHandler();
        } else {
            handler = new StackItemHandler();
        }
        handler.itemStack = itemStack;
        return handler;
    }
}
