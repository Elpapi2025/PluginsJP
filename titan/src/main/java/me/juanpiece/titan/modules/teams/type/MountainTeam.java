package me.juanpiece.titan.modules.teams.type;

import lombok.Getter;
import lombok.Setter;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.teams.Team;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.claims.Claim;
import me.juanpiece.titan.modules.teams.enums.MountainType;
import me.juanpiece.titan.modules.teams.enums.TeamType;
import me.juanpiece.titan.modules.teams.extra.TeamChest;
import me.juanpiece.titan.utils.ItemBuilder;
import me.juanpiece.titan.utils.ItemUtils;
import me.juanpiece.titan.utils.Serializer;
import me.juanpiece.titan.utils.Utils;
import me.juanpiece.titan.utils.extra.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class MountainTeam extends Team {

    private final Map<Location, Material> resets;

    private final List<TeamChest> randomItems;
    private final List<Material> allowedBreak;
    private final List<Location> chests;

    private final MountainType mountainType;

    public MountainTeam(TeamManager manager, Map<String, Object> map) {
        super(
                manager,
                map,
                true,
                TeamType.MOUNTAIN
        );

        this.randomItems = new ArrayList<>();
        this.allowedBreak = new ArrayList<>();
        this.mountainType = MountainType.valueOf((String) map.get("mountainType"));

        this.resets = Utils.createList(map.get("resets"), String.class)
                .stream().map(Serializer::deserializeMountainBlock).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        this.chests = Utils.createList(map.get("chests"), String.class)
                .stream().map(Serializer::deserializeLoc).collect(Collectors.toList());

        this.load();
    }

    public MountainTeam(TeamManager manager, String name, MountainType mountainType) {
        super(
                manager,
                name,
                UUID.randomUUID(),
                true,
                TeamType.MOUNTAIN
        );

        this.mountainType = mountainType;

        this.resets = new HashMap<>();
        this.chests = new ArrayList<>();
        this.randomItems = new ArrayList<>();
        this.allowedBreak = new ArrayList<>();

        this.load();
    }

    @Override
    public String getDisplayName(Player player) {
        return (mountainType == MountainType.GLOWSTONE ?
                Config.SYSTEAM_COLOR_GLOWSTONE.replace("%team%", super.getDisplayName(player)) :
                Config.SYSTEAM_COLOR_ORE_MOUNTAIN.replace("%team%", super.getDisplayName(player)));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        map.put("mountainType", mountainType.toString());

        map.put("resets", resets.entrySet()
                .stream()
                .map(entry -> Serializer.serializeMountainBlock(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));

        map.put("chests", chests
                .stream()
                .map(Serializer::serializeLoc)
                .collect(Collectors.toList())); // no need to save chests aswell since bukkit saves items for us.

        return map;
    }

    private void load() {
        allowedBreak.addAll(getConfig().getStringList("MOUNTAINS." + mountainType.name() + ".ALLOWED_BREAK")
                .stream()
                .map(ItemUtils::getMat)
                .collect(Collectors.toList()));

        this.loadRandomItems();
    }

    private void loadRandomItems() {
        for (String s : getConfig().getStringList("MOUNTAINS." + mountainType.toString() + ".CHEST_CONFIG.RANDOM_ITEMS")) {
            String[] split = s.split(", ");

            if (s.startsWith("ABILITY")) {
                String abilityName = split[0].split(":")[1].toUpperCase();
                ItemStack ability = getInstance().getAbilityManager().getAbility(abilityName).getItem().clone();

                ability.setAmount(Integer.parseInt(split[1]));
                randomItems.add(new TeamChest(
                        ability,
                        Double.parseDouble(split[2].replaceAll("%", "")))
                );
                continue;
            }

            ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(split[0]), Integer.parseInt(split[1]))
                    .data(getManager(), Short.parseShort(split[2]));

            if (!split[3].equalsIgnoreCase("NONE")) {
                builder.setName(split[3]);
            }

            if (!split[4].equalsIgnoreCase("NONE")) {
                String[] furtherSplit = split[4].split(";"); // split the enchants

                for (String value : furtherSplit) {
                    String[] levelSplit = value.split(":"); // split to enchant:level

                    builder.addUnsafeEnchantment(Enchantment.getByName(levelSplit[0]), Integer.parseInt(levelSplit[1]));
                }
            }

            if (!split[5].equalsIgnoreCase("NONE")) {
                String[] furtherSplit = split[5].split(";"); // split the lore

                for (String value : furtherSplit) {
                    builder.addLoreLine(value);
                }
            }

            randomItems.add(new TeamChest(
                    builder.toItemStack(),
                    Double.parseDouble(split[6].replaceAll("%", "")))
            );
        }
    }

    private void addRandomItem(Chest chest) {
        if (randomItems.isEmpty()) return;

        chest.getInventory().clear(); // clear old contents.

        int min = getConfig().getInt("MOUNTAINS." + mountainType.name() + ".CHEST_CONFIG.MIN_ITEM_AMOUNT");
        int max = getConfig().getInt("MOUNTAINS." + mountainType.name() + ".CHEST_CONFIG.MAX_ITEM_AMOUNT");

        // the amount of items we are adding.
        int amount = ThreadLocalRandom.current().nextInt(max);
        int amountFinal = Math.max(amount, min);

        List<TeamChest> counted = new ArrayList<>();

        // Add them all together with the percentage amount
        for (TeamChest randomItem : randomItems) {

            // so when we use the random - for example 30% will have 30 items in it thus 30% of a chance.
            for (int i = 0; i < randomItem.getPercentage(); i++) {
                counted.add(randomItem);
            }
        }

        // now just add it all with a random position and random item.
        for (int i = 0; i < amountFinal; i++) {
            int random = ThreadLocalRandom.current().nextInt(counted.size());
            int position = ThreadLocalRandom.current().nextInt(chest.getInventory().getSize());
            ItemStack atPos = chest.getInventory().getItem(position);

            if (atPos != null) {
                i -= 1; // we loop again.
                continue;
            }

            chest.getInventory().setItem(position, counted.get(random).getItemStack());
        }

        // clear to decrease ram
        counted.clear();
        chest.update(true);
    }

    public void resetBlocks() {
        Iterator<Location> iterator = chests.iterator();

        while (iterator.hasNext()) {
            Location location = iterator.next();

            if (!(location.getBlock().getState() instanceof Chest)) {
                iterator.remove();
                continue;
            }

            Chest chest = (Chest) location.getBlock().getState();
            this.addRandomItem(chest);
        }

        for (Map.Entry<Location, Material> entry : resets.entrySet()) {
            Block block = entry.getKey().getBlock();
            Material material = entry.getValue();

            // don't set the type again if it hasn't been mined yet
            if (block.getType() == material) continue;

            block.setType(material);
        }
    }

    public void saveBlocks() {
        resets.clear(); // Override the old ones.

        for (Claim claim : claims) {
            // loop through blocks
            for (Block block : claim) {
                if (block.getType() == Material.AIR) continue; // no use checking List#contains

                if (block.getType().name().contains("CHEST") && !chests.contains(block.getLocation())) {
                    chests.add(block.getLocation());
                    continue;
                }

                // Store the blocks that are allowed to be broken, e.g. glowstone
                if (allowedBreak.contains(block.getType())) {
                    resets.put(block.getLocation(), block.getType());
                }
            }

            this.save(); // save everything when done checking the claim - not inside the loop.
        }
    }
}