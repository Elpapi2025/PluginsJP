package com.ejemplo.economiaplugin.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class MaterialCompatibility {

    private static final Map<String, String> MATERIAL_MAPPINGS = new HashMap<>();

    static {
        // Mapeos de 1.13+ a 1.8.x para materiales comunes en tiendas
        MATERIAL_MAPPINGS.put("DIAMOND_SWORD", "DIAMOND_SWORD");
        MATERIAL_MAPPINGS.put("GOLD_INGOT", "GOLD_INGOT");
        MATERIAL_MAPPINGS.put("COAL_ORE", "COAL_ORE");
        MATERIAL_MAPPINGS.put("IRON_ORE", "IRON_ORE");
        MATERIAL_MAPPINGS.put("DIAMOND_ORE", "DIAMOND_ORE");
        MATERIAL_MAPPINGS.put("EMERALD_ORE", "EMERALD_ORE");
        MATERIAL_MAPPINGS.put("REDSTONE_ORE", "REDSTONE_ORE");
        MATERIAL_MAPPINGS.put("LAPIS_ORE", "LAPIS_ORE");
        MATERIAL_MAPPINGS.put("QUARTZ_ORE", "QUARTZ_ORE");
        MATERIAL_MAPPINGS.put("GRASS_BLOCK", "GRASS"); // 1.8.x es GRASS, no GRASS_BLOCK
        MATERIAL_MAPPINGS.put("COBBLESTONE", "COBBLESTONE");
        MATERIAL_MAPPINGS.put("OAK_LOG", "LOG"); // En 1.8.x puede necesitar data values
        MATERIAL_MAPPINGS.put("STONE", "STONE");
        MATERIAL_MAPPINGS.put("NETHERRACK", "NETHERRACK");
        MATERIAL_MAPPINGS.put("GOLDEN_APPLE", "GOLDEN_APPLE");
        MATERIAL_MAPPINGS.put("ENCHANTED_GOLDEN_APPLE", "GOLDEN_APPLE:1"); // Para la notch apple antigua en 1.8.x
        MATERIAL_MAPPINGS.put("ENDER_PEARL", "ENDER_PEARL");
        MATERIAL_MAPPINGS.put("POTION", "POTION");
        MATERIAL_MAPPINGS.put("SPLASH_POTION", "POTION"); // Potions en 1.8 son más complejas, esto es solo un placeholder
        MATERIAL_MAPPINGS.put("EXPERIENCE_BOTTLE", "EXP_BOTTLE");
        MATERIAL_MAPPINGS.put("DIRT", "DIRT");
        MATERIAL_MAPPINGS.put("WATER_BUCKET", "WATER_BUCKET");
        MATERIAL_MAPPINGS.put("LAVA_BUCKET", "LAVA_BUCKET");
    }

    public static ItemStack getItemStack(String materialName, int amount) {
        String mappedMaterial = MATERIAL_MAPPINGS.getOrDefault(materialName.toUpperCase(), materialName.toUpperCase());
        Material material;
        byte data = 0;

        if (mappedMaterial.contains(":")) {
            String[] parts = mappedMaterial.split(":");
            material = Material.valueOf(parts[0]);
            data = Byte.parseByte(parts[1]);
        } else {
            material = Material.valueOf(mappedMaterial);
        }

        return new ItemStack(material, amount, data);
    }
}