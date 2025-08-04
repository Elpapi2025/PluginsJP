package me.titan.core.modules.teams.type;

import lombok.Getter;
import me.titan.core.modules.framework.Config;
import me.titan.core.modules.teams.Team;
import me.titan.core.modules.teams.TeamManager;
import me.titan.core.modules.teams.claims.Claim;
import me.titan.core.modules.teams.enums.MountainType;
import me.titan.core.modules.teams.enums.TeamType;
import me.titan.core.modules.teams.extra.TeamChest;
import me.titan.core.utils.ItemBuilder;
import me.titan.core.utils.ItemUtils;
import me.titan.core.utils.Serializer;
import me.titan.core.utils.Utils;
import me.titan.core.utils.extra.Pair;
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

@Getter
public class MountainTeam extends Team {
    private final MountainType mountainType;
    private final List<TeamChest> randomItems;
    private final List<Location> chests;
    private final List<Material> allowedBreak;
    private final Map<Location, Material> resets;
    
    public MountainTeam(TeamManager manager, Map<String, Object> map) {
        super(manager, map, true, TeamType.MOUNTAIN);
        this.randomItems = new ArrayList<>();
        this.allowedBreak = new ArrayList<>();
        this.mountainType = MountainType.valueOf((String) map.get("mountainType"));
        this.resets = Utils.createList(map.get("resets"), String.class).stream().map(Serializer::deserializeMountainBlock).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        this.chests = Utils.createList(map.get("chests"), String.class).stream().map(Serializer::deserializeLoc).collect(Collectors.toList());
        this.load();
    }
    
    @Override
    public String getDisplayName(Player player) {
        return ((this.mountainType == MountainType.GLOWSTONE) ? Config.SYSTEAM_COLOR_GLOWSTONE : Config.SYSTEAM_COLOR_ORE_MOUNTAIN) + super.getDisplayName(player);
    }
    
    private void load() {
        this.allowedBreak.addAll(this.getConfig().getStringList("MOUNTAINS." + this.mountainType.name() + ".ALLOWED_BREAK").stream().map(ItemUtils::getMat).collect(Collectors.toList()));
        this.loadRandomItems();
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("mountainType", this.mountainType.toString());
        map.put("resets", this.resets.entrySet().stream().map(r -> Serializer.serializeMountainBlock(r.getKey(), r.getValue())).collect(Collectors.toList()));
        map.put("chests", this.chests.stream().map(Serializer::serializeLoc).collect(Collectors.toList()));
        return map;
    }
    
    public MountainTeam(TeamManager manager, String name, MountainType type) {
        super(manager, name, UUID.randomUUID(), true, TeamType.MOUNTAIN);
        this.mountainType = type;
        this.resets = new HashMap<>();
        this.chests = new ArrayList<>();
        this.randomItems = new ArrayList<>();
        this.allowedBreak = new ArrayList<>();
        this.load();
    }
    
    private void addRandomItem(Chest chest) {
        if (this.randomItems.isEmpty()) {
            return;
        }
        chest.getInventory().clear();
        int min = this.getConfig().getInt("MOUNTAINS." + this.mountainType.name() + ".CHEST_CONFIG.MIN_ITEM_AMOUNT");
        int max = this.getConfig().getInt("MOUNTAINS." + this.mountainType.name() + ".CHEST_CONFIG.MAX_ITEM_AMOUNT");
        int rand = ThreadLocalRandom.current().nextInt(max);
        int selected = Math.max(rand, min);
        List<TeamChest> chests = new ArrayList<>();
        for (TeamChest teamChest : this.randomItems) {
            for (int i = 0; i < teamChest.getPercentage(); ++i) {
                chests.add(teamChest);
            }
        }
        for (int i = 0; i < selected; ++i) {
            int size = ThreadLocalRandom.current().nextInt(chests.size());
            int inv = ThreadLocalRandom.current().nextInt(chest.getInventory().getSize());
            ItemStack stack = chest.getInventory().getItem(inv);
            if (stack != null) {
                --i;
            }
            else {
                chest.getInventory().setItem(inv, chests.get(size).getItemStack());
            }
        }
        chests.clear();
        chest.update(true);
    }
    
    public void resetBlocks() {
        Iterator<Location> chests = this.chests.iterator();
        while (chests.hasNext()) {
            Location location = chests.next();
            if (!(location.getBlock().getState() instanceof Chest)) {
                chests.remove();
            }
            else {
                Chest chest = (Chest)location.getBlock().getState();
                this.addRandomItem(chest);
            }
        }
        for (Map.Entry<Location, Material> resets : this.resets.entrySet()) {
            Block block = resets.getKey().getBlock();
            Material material = resets.getValue();
            if (block.getType() == material) {
                continue;
            }
            block.setType(material);
            block.getState().update(true);
        }
    }
    
    private void loadRandomItems() {
        for (String s : this.getConfig().getStringList("MOUNTAINS." + this.mountainType.toString() + ".CHEST_CONFIG.RANDOM_ITEMS")) {
            String[] abilities = s.split(", ");
            if (s.startsWith("ABILITY")) {
                String ability = abilities[0].split(":")[1].toUpperCase();
                ItemStack stack = this.getInstance().getAbilityManager().getAbility(ability).getItem().clone();
                stack.setAmount(Integer.parseInt(abilities[1]));
                this.randomItems.add(new TeamChest(stack, Double.parseDouble(abilities[2].replaceAll("%", ""))));
            }
            else {
                ItemBuilder builder = new ItemBuilder(ItemUtils.getMat(abilities[0]), Integer.parseInt(abilities[1])).data(this.getManager(), Short.parseShort(abilities[2]));
                if (!abilities[3].equalsIgnoreCase("NONE")) {
                    builder.setName(abilities[3]);
                }
                if (!abilities[4].equalsIgnoreCase("NONE")) {
                    for (String ss : abilities[4].split(";")) {
                        String[] enchantments = ss.split(":");
                        builder.addUnsafeEnchantment(Enchantment.getByName(enchantments[0]), Integer.parseInt(enchantments[1]));
                    }
                }
                if (!abilities[5].equalsIgnoreCase("NONE")) {
                    for (String lore : abilities[5].split(";")) {
                        builder.addLoreLine(lore);
                    }
                }
                this.randomItems.add(new TeamChest(builder.toItemStack(), Double.parseDouble(abilities[6].replaceAll("%", ""))));
            }
        }
    }
    
    public void saveBlocks() {
        this.resets.clear();
        for (Claim claim : this.claims) {
            for (Block block : claim) {
                if (block.getType() == Material.AIR) {
                    continue;
                }
                if (block.getType().name().contains("CHEST") && !this.chests.contains(block.getLocation())) {
                    this.chests.add(block.getLocation());
                }
                else {
                    if (!this.allowedBreak.contains(block.getType())) {
                        continue;
                    }
                    this.resets.put(block.getLocation(), block.getType());
                }
            }
            this.save();
        }
    }
}