package com.github.lockoct.handler.container;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;

import java.lang.reflect.Constructor;
import java.util.*;

public class ContainerHandlerFactory {
    private static final Map<Material, ContainerHandler> containerStrategies = new HashMap<>();
    private static final ArrayList<Material> supportedContainers = new ArrayList<>();
    private static final String[] supportedContainersName = new String[]{"BARREL", "BLAST_FURNACE", "CHEST", "FURNACE", "HOPPER", "SMOKER", "DECORATED_POT"};

    static {
        try {
            for (String name : supportedContainersName) {
                Material container = Material.getMaterial(name);
                if (container != null) {
                    Class<?> clazz = Class.forName("com.github.lockoct.handler.container." + handlerClassNameTrans(name) + "Handler");
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    containerStrategies.put(container, (ContainerHandler) constructor.newInstance());
                    supportedContainers.add(container);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static String handlerClassNameTrans(String name) {
        String[] word = StringUtils.split(name.toLowerCase(), '_');
        List<String> wordList = Arrays.stream(word).map(StringUtils::capitalize).toList();
        name = StringUtils.join(wordList, StringUtils.EMPTY);
        return StringUtils.capitalize(name);
    }
}
