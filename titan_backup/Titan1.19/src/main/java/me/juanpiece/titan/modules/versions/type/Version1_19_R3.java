package me.juanpiece.titan.modules.versions.type;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import lombok.SneakyThrows;
import me.juanpiece.titan.modules.ability.AbilityManager;
import me.juanpiece.titan.modules.ability.type.InvisibilityAbility;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.modules.loggers.Logger;
import me.juanpiece.titan.modules.nametags.Nametag;
import me.juanpiece.titan.modules.nametags.extra.NameVisibility;
import me.juanpiece.titan.modules.pvpclass.type.ghost.GhostClass;
import me.juanpiece.titan.modules.tablist.extra.TablistSkin;
import me.juanpiece.titan.modules.versions.Version;
import me.juanpiece.titan.modules.versions.VersionManager;
import me.juanpiece.titan.utils.ReflectionUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.PlayerChunkMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings("all")
public class Version1_19_R3 extends Module<VersionManager> implements Version {

    private static final Field ENTITY_ID = ReflectionUtils.accessField(PacketPlayOutEntityEquipment.class, "b");
    private static final Field SLOT = ReflectionUtils.accessField(PacketPlayOutEntityEquipment.class, "c");
    private static final Field ACTION = ReflectionUtils.accessField(ClientboundPlayerInfoUpdatePacket.class, "a");
    private static final Field DATA = ReflectionUtils.accessField(ClientboundPlayerInfoUpdatePacket.class, "b");
    private static final Field ENTITY_LOOKUP = ReflectionUtils.accessField(WorldServer.class, "entityLookup");

    private static final Method ENTITY_GET = ReflectionUtils.accessMethod(
            ReflectionUtils.getClass("io.papermc.paper.chunk.system.entity.EntityLookup"), "get", int.class);

    private static final Method SPAWN_PARTICLE = ReflectionUtils.accessMethod(
            CraftWorld.class, "spawnParticle", Particle.class, Location.class, int.class, Object.class
    );

    public Version1_19_R3(VersionManager manager) {
        super(manager);
    }

    @Override
    public CommandMap getCommandMap() {
        try {

            CraftServer server = (CraftServer) Bukkit.getServer();
            Method method = server.getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(server);

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @SneakyThrows
    public Set<Player> getTrackedPlayers(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerChunkMap.EntityTracker tracker = entityPlayer.tracker;

        if (tracker != null) {
            Set<Player> players = new HashSet<>();

            for (ServerPlayerConnection connection : tracker.f) {
                players.add(connection.f().getBukkitEntity());
            }

            return players;
        }

        return Collections.emptySet();
    }

    @Override
    public boolean isNotGapple(ItemStack item) {
        return item.getType() != Material.ENCHANTED_GOLDEN_APPLE;
    }

    @Override
    public int getPing(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        return craftPlayer.getHandle().e;
    }

    @Override
    public ItemStack getItemInHand(Player player) {
        try {

            Method method = player.getInventory().getClass().getMethod("getItemInMainHand");
            return (ItemStack) method.invoke(player.getInventory());

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemStack addGlow(ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public void setItemInHand(Player player, ItemStack item) {
        try {

            Method method = player.getInventory().getClass().getMethod("setItemInMainHand", ItemStack.class);
            method.invoke(player.getInventory(), item);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleLoggerDeath(Logger logger) {
        EntityPlayer player = ((CraftPlayer) logger.getPlayer()).getHandle();
        player.getBukkitEntity().getInventory().clear();
        player.getBukkitEntity().getInventory().setArmorContents(null);
        player.getBukkitEntity().setHealth(0);
        player.getBukkitEntity().setExp(0.0F);
        player.removeAllEffects(EntityPotionEffectEvent.Cause.DEATH);
        player.getBukkitEntity().saveData(); // Save the inventory and everything we just modified
    }

    @Override
    public void playEffect(Location location, String name, Object data) {
        try {

            Particle particle = Particle.valueOf(name);
            SPAWN_PARTICLE.invoke(location.getWorld(), particle, location, 1, data);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Particle " + name + " does not exist.");

        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTPSColored() {
        double tps = MinecraftServer.getServer().recentTps[0];
        String color = (tps > 18 ? "§a" : tps > 16 ? "§e" : "§c");
        String asterisk = (tps > 20 ? "*" : "");
        return color + asterisk + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    @Override
    public void hideArmor(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerChunkMap.EntityTracker tracker = entityPlayer.tracker;

        if (tracker != null) {
            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> items = new ArrayList<>();

            items.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(null)));
            items.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(null)));
            items.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(null)));
            items.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(null)));

            tracker.a(new PacketPlayOutEntityEquipment(player.getEntityId(), items));
        }
    }

    @Override
    public void showArmor(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerChunkMap.EntityTracker tracker = entityPlayer.tracker;

        if (tracker != null) {
            org.bukkit.inventory.PlayerInventory inventory = player.getInventory();
            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> items = new ArrayList<>();

            items.add(new Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(inventory.getBoots())));
            items.add(new Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(inventory.getLeggings())));
            items.add(new Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(inventory.getChestplate())));
            items.add(new Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(inventory.getHelmet())));

            tracker.a(new PacketPlayOutEntityEquipment(player.getEntityId(), items));
        }
    }

    @Override
    public void handleNettyListener(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        ChannelPipeline pipeline = entityPlayer.b.h.m.pipeline();
        AbilityManager abilityManager = getInstance().getAbilityManager();
        InvisibilityAbility ability = (InvisibilityAbility) abilityManager.getAbility("Invisibility");
        GhostClass ghostClass = getInstance().getClassManager().getGhostClass();

        if (pipeline.get("packet_handler") == null) {
            player.kickPlayer(Config.COULD_NOT_LOAD_DATA);
            return;
        }

        pipeline.addBefore("packet_handler", "Titan", new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof PacketPlayOutEntityEquipment packet) {
                    WorldServer server = ((CraftWorld) player.getWorld()).getHandle();
                    Entity entity = (Entity) ENTITY_GET.invoke(ENTITY_LOOKUP.get(server), (int) ENTITY_ID.get(packet));

                    if (entity != null) {
                        if (ghostClass != null && ghostClass.getInvisible().contains(entity.getBukkitEntity().getUniqueId())) {
                            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> slot =
                                    (List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>) SLOT.get(packet);

                            Iterator<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> iterator = slot.iterator();

                            while (iterator.hasNext()) {
                                Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair = iterator.next();

                                if (pair.getFirst().e()) {
                                    iterator.remove();
                                }
                            }

                            SLOT.set(packet, slot); // update it in packet

                            if (slot.isEmpty()) {
                                return; // Don't send the packet.
                            }
                        }

                        if (ability.getInvisible().contains(entity.getBukkitEntity().getUniqueId())) {
                            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> slot =
                                    (List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>>) SLOT.get(packet);

                            Iterator<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> iterator = slot.iterator();

                            while (iterator.hasNext()) {
                                Pair<EnumItemSlot, net.minecraft.world.item.ItemStack> pair = iterator.next();

                                if (pair.getFirst().e()) {
                                    iterator.remove();
                                }
                            }

                            SLOT.set(packet, slot); // update it in packet

                            if (slot.isEmpty()) {
                                return; // Don't send the packet.
                            }
                        }
                    }
                } else if (msg instanceof ClientboundPlayerInfoUpdatePacket) {
                    ClientboundPlayerInfoUpdatePacket packet = (ClientboundPlayerInfoUpdatePacket) msg;
                    EnumSet<ClientboundPlayerInfoUpdatePacket.a> actions = (EnumSet<ClientboundPlayerInfoUpdatePacket.a>) ACTION.get(packet);
                    Nametag nametag = getInstance().getNametagManager().getNametags().get(player.getUniqueId());

                    for (ClientboundPlayerInfoUpdatePacket.a action : actions) {
                        if (action == ClientboundPlayerInfoUpdatePacket.a.a && nametag != null) {
                            List<ClientboundPlayerInfoUpdatePacket.b> data = (List<ClientboundPlayerInfoUpdatePacket.b>) DATA.get(packet);

                            for (ClientboundPlayerInfoUpdatePacket.b info : data) {
                                Player targetPlayer = Bukkit.getPlayer(info.a());

                                if (targetPlayer == null) continue;

                                // Make tablist sort instantly
                                nametag.getPacket().create("tablist", "", "", "", false, NameVisibility.ALWAYS);
                                nametag.getPacket().addToTeam(targetPlayer, "tablist");
                            }
                        }
                    }
                }

                super.write(ctx, msg, promise);
            }
        });
    }

    @Override
    public void sendActionBar(Player player, String string) {
        ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(CraftChatMessage.fromJSONOrString(string, true));
        ((CraftPlayer) player).getHandle().b.a(packet);
    }

    @Override
    public void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(getInstance(), "BungeeCord", out.toByteArray());
    }

    @Override
    public void clearArrows(Player player) {
        ((CraftPlayer) player).getHandle().setArrowCount(0, false);
    }

    @Override
    public List<ItemStack> getBlockDrops(Player pl, Block bl, ItemStack hand) {
        EntityPlayer serverPlayer = ((CraftPlayer) pl).getHandle();
        WorldServer serverLevel = serverPlayer.cG().getMinecraftWorld();
        BlockPosition blockPos = new BlockPosition(bl.getX(), bl.getY(), bl.getZ());
        IBlockData blockState = serverLevel.a_(blockPos);
        TileEntity blockEntity = serverLevel.c_(blockPos);

        return net.minecraft.world.level.block.Block.a(blockState, serverLevel, blockPos, blockEntity, serverPlayer, serverPlayer.fJ().f())
                .stream().map(CraftItemStack::asBukkitCopy).collect(Collectors.toList());
    }

    @Override
    public TablistSkin getSkinData(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        GameProfile profile = entityPlayer.fI();

        if (profile.getProperties().get("textures").size() == 0) {
            return null;
        }

        Property property = profile.getProperties().get("textures").iterator().next();
        return new TablistSkin(property.getValue(), property.getSignature());
    }
}