package com.github.lockoct.handler.item;

import org.bukkit.inventory.ItemStack;

public class ItemHandlerFactory {
    public static ItemHandler getHandler(ItemStack itemStack) {
        ItemHandler handler = itemStack.getMaxStackSize() > 1 ? new StackItemHandler() : new UnstackItemHandler();
        handler.itemStack = itemStack;
        return handler;
    }
}
