package com.github.lockoct.handler.item;

import org.bukkit.inventory.ItemStack;

public abstract class ItemHandler {
    protected ItemStack itemStack;

    public abstract boolean execute();
}
