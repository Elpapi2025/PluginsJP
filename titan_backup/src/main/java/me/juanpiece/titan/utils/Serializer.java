package me.juanpiece.titan.utils;

import me.juanpiece.titan.modules.deathban.Deathban;
import me.juanpiece.titan.modules.deathban.DeathbanManager;
import me.juanpiece.titan.modules.events.conquest.ConquestManager;
import me.juanpiece.titan.modules.events.conquest.extra.Capzone;
import me.juanpiece.titan.modules.events.conquest.extra.ConquestType;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.teams.claims.Claim;
import me.juanpiece.titan.modules.teams.player.Member;
import me.juanpiece.titan.modules.teams.player.Role;
import me.juanpiece.titan.modules.teams.task.BaseTask;
import me.juanpiece.titan.modules.teams.task.FalltrapTask;
import me.juanpiece.titan.modules.teams.task.SkybaseTask;
import me.juanpiece.titan.modules.teams.TeamManager;
import me.juanpiece.titan.modules.teams.task.SkybaseTask;
import me.juanpiece.titan.utils.cuboid.Cuboid;
import me.juanpiece.titan.utils.extra.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Date;
import java.util.UUID;

public class Serializer {

    public static String serializeClaim(Claim claim) {
        return claim.getTeam().toString() + ", " +
                claim.getWorldName() + ", " +
                claim.getX1() + ", " + claim.getY1() + ", " + claim.getZ1() + ", " +
                claim.getX2() + ", " + claim.getY2() + ", " + claim.getZ2();
    }

    public static Claim deserializeClaim(String string) {
        String[] split = string.split(", ");
        return new Claim(
                UUID.fromString(split[0]),
                new Location(Bukkit.getWorld(split[1]), parseInt(split[2]), parseInt(split[3]), parseInt(split[4])),
                new Location(Bukkit.getWorld(split[1]), parseInt(split[5]), parseInt(split[6]), parseInt(split[7]))
        );
    }

    public static String serializeCapzone(Capzone capzone) {
        return serializeCuboid(capzone.getZone()) + ":" + capzone.getType().toString();
    }

    public static Capzone deserializeCapzone(ConquestManager manager, String string) {
        String[] split = string.split(":");
        return new Capzone(manager, deserializeCuboid(split[0]), ConquestType.valueOf(split[1]));
    }

    public static String serializeCuboid(Cuboid cuboid) {
        return cuboid.getWorldName() + ", " +
                cuboid.getX1() + ", " + cuboid.getY1() + ", " + cuboid.getZ1() + ", " +
                cuboid.getX2() + ", " + cuboid.getY2() + ", " + cuboid.getZ2();
    }

    public static Cuboid deserializeCuboid(String string) {
        String[] split = string.split(", ");
        return new Cuboid(
                new Location(Bukkit.getWorld(split[0]), parseInt(split[1]), parseInt(split[2]), parseInt(split[3])),
                new Location(Bukkit.getWorld(split[0]), parseInt(split[4]), parseInt(split[5]), parseInt(split[6]))
        );
    }

    public static String serializeMountainBlock(Location location, Material material) {
        return serializeLoc(location) + ";" + material.name();
    }

    public static Pair<Location, Material> deserializeMountainBlock(String string) {
        String[] split = string.split(";");
        return new Pair<>(deserializeLoc(split[0]), ItemUtils.getMat(split[1]));
    }

    public static String serializeDeathban(Deathban deathban) {
        return deathban.getUniqueID().toString() + ";" + deathban.getTime() + ";" + deathban.getReason() + ";" +
                serializeLoc(deathban.getLocation()) + ";" + deathban.getDate().getTime();
    }

    public static Deathban deserializeDeathban(DeathbanManager manager, String string) {
        String[] split = string.split(";");

        Deathban deathban = new Deathban(
                manager,
                UUID.fromString(split[0]),
                Long.parseLong(split[1]),
                split[2],
                deserializeLoc(split[3])
        );

        deathban.setDate(new Date(Long.parseLong(split[4])));

        return deathban;
    }

    public static String serializeFalltrapTask(FalltrapTask task) {
        String claim = serializeClaim(task.getClaim());
        String item = BukkitSerialization.itemStackArrayToBase64(new ItemStack[]{task.getWall()});
        String toChangeIndex = String.valueOf(task.getOutlineIndex());
        String toChangeWallIndex = String.valueOf(task.getWallIndex());
        return claim + ";" + task.getPlayer() + ";" + item + ";" + toChangeIndex + ";" + toChangeWallIndex;
    }

    public static FalltrapTask deserializeFalltrapTask(Manager manager, String string) {
        String[] split = string.split(";");
        String[] secSplit = split[0].split(", ");
        Claim claim = new Claim(
                UUID.fromString(secSplit[0]),
                new Location(Bukkit.getWorld(secSplit[1]), parseInt(secSplit[2]), parseInt(secSplit[3]), parseInt(secSplit[4])),
                new Location(Bukkit.getWorld(secSplit[1]), parseInt(secSplit[5]), parseInt(secSplit[6]), parseInt(secSplit[7]))
        );

        claim.setY1(parseInt(secSplit[3]));
        claim.setY2(parseInt(secSplit[6]));

        return new FalltrapTask(manager,
                UUID.fromString(split[1]), claim, BukkitSerialization.itemStackArrayFromBase64(split[2])[0], Integer.parseInt(split[3]), Integer.parseInt(split[4])
        );
    }

    public static String serializeBaseTask(BaseTask task) {
        String claim = serializeClaim(task.getClaim());
        String item = BukkitSerialization.itemStackArrayToBase64(new ItemStack[]{task.getWall(), task.getOutline()});
        String toChangeOutlineIndex = String.valueOf(task.getOutlineIndex());
        String toChangeWallIndex = String.valueOf(task.getWallIndex());
        return claim + ";" + task.getPlayer() + ";" + item + ";" + toChangeOutlineIndex + ";" + toChangeWallIndex;
    }

    public static BaseTask deserializeBaseTask(Manager manager, String string) {
        String[] split = string.split(";");
        String[] secSplit = split[0].split(", ");
        Claim claim = new Claim(
                UUID.fromString(secSplit[0]),
                new Location(Bukkit.getWorld(secSplit[1]), parseInt(secSplit[2]), parseInt(secSplit[3]), parseInt(secSplit[4])),
                new Location(Bukkit.getWorld(secSplit[1]), parseInt(secSplit[5]), parseInt(secSplit[6]), parseInt(secSplit[7]))
        );

        claim.setY1(parseInt(secSplit[3]));
        claim.setY2(parseInt(secSplit[6]));

        return new BaseTask(manager,
                UUID.fromString(split[1]), claim, BukkitSerialization.itemStackArrayFromBase64(split[2])[0],
                BukkitSerialization.itemStackArrayFromBase64(split[2])[1], Integer.parseInt(split[3]), Integer.parseInt(split[4])
        );
    }

    public static String serializeSkybaseTask(SkybaseTask task) {
        String claim = serializeClaim(task.getClaim());
        String item = BukkitSerialization.itemStackArrayToBase64(new ItemStack[]{task.getWall()});
        return claim + ";" + task.getUniqueId() + ";" + item;
    }

    public static SkybaseTask deserializeSkybaseTask(Manager manager, String string) {
        String[] split = string.split(";");
        String[] secSplit = split[0].split(", ");
        Claim claim = new Claim(
                UUID.fromString(secSplit[0]),
                new Location(Bukkit.getWorld(secSplit[1]), parseInt(secSplit[2]), parseInt(secSplit[3]), parseInt(secSplit[4])),
                new Location(Bukkit.getWorld(secSplit[1]), parseInt(secSplit[5]), parseInt(secSplit[6]), parseInt(secSplit[7]))
        );

        claim.setY1(parseInt(secSplit[3]));
        claim.setY2(parseInt(secSplit[6]));

        return new SkybaseTask((TeamManager) manager,
                UUID.fromString(split[1]), claim, BukkitSerialization.itemStackArrayFromBase64(split[2])[0]
        );
    }

    public static PotionEffect getEffect(String string) {
        if (string.isEmpty()) {
            return null;
        }

        String[] split = string.split(", ");

        try {

            int duration = (split[1].equals("MAX_VALUE") ? (Utils.isModernVer() ? -1 : Integer.MAX_VALUE) : 20 * parseInt(split[1].replaceAll("s", "")));
            return new PotionEffect(PotionEffectType.getByName(split[0]), duration, parseInt(split[2]) - 1);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Effect " + split[0] + " does not exist!");
        }
    }

    public static String serializeMember(Member member) {
        return member.getUniqueID().toString() + ", " + member.getRole().toString();
    }

    public static Member deserializeMember(String string) {
        String[] split = string.split(", ");
        return new Member(UUID.fromString(split[0]), Role.valueOf(split[1]));
    }

    public static String serializeLoc(Location location) {
        if (location == null) {
            return "null";
        }

        return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                ", " + location.getYaw() + ", " + location.getPitch();
    }

    public static Location deserializeLoc(String string) {
        if (string.equals("null")) {
            return null;
        }

        String[] split = string.split(", ");
        return new Location(Bukkit.getWorld(split[0]),
                parseDouble(split[1]), parseDouble(split[2]), parseDouble(split[3]),
                parseFloat(split[4]), parseFloat(split[5]));
    }

    private static Integer parseInt(String string) {
        return Integer.parseInt(string);
    }

    private static Double parseDouble(String string) {
        return Double.parseDouble(string);
    }

    private static Float parseFloat(String string) {
        return Float.parseFloat(string);
    }
}