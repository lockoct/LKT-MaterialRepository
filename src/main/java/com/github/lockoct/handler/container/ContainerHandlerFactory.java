package com.github.lockoct.handler.container;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ContainerHandlerFactory {
    private static final Map<Material, ContainerHandler> containerStrategies = new HashMap<>();
    private static final ArrayList<Material> supportedContainers = new ArrayList<>(Arrays.asList(Material.BARREL, Material.BLAST_FURNACE, Material.CHEST, Material.FURNACE, Material.HOPPER, Material.SMOKER));

    static {
        containerStrategies.put(Material.BARREL, new BarrelHandler());
        containerStrategies.put(Material.BLAST_FURNACE, new BlastFurnaceHandler());
        containerStrategies.put(Material.CHEST, new ChestHandler());
        containerStrategies.put(Material.FURNACE, new FurnaceHandler());
        containerStrategies.put(Material.HOPPER, new HopperHandler());
        containerStrategies.put(Material.SMOKER, new SmokerHandler());
    }

    public static ContainerHandler getHandler(Material material) {
        if (material == null || !containerStrategies.containsKey(material)) {
            return null;
        }
        return containerStrategies.get(material);
    }

    public static ArrayList<Material> getSupportedContainers() {
        return supportedContainers;
    }
}
