package me.juanpiece.titan.modules.discord.type;

import me.juanpiece.titan.modules.discord.Discord;
import me.juanpiece.titan.modules.discord.extra.DiscordWebhook;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Manager;
import org.bukkit.entity.Player;

import java.awt.*;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ReportWebhook extends Discord {

    public ReportWebhook(Manager manager, Player player, Player target, String report) {
        super(manager, Config.DISCORD_REPORT_WEBHOOKURL);
        discordWebhook.setContent(Config.DISCORD_REPORT_CONTENT
                .replace("%player%", player.getName())
                .replace("%target%", target.getName())
                .replace("%report%", report)
        );
        discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setAuthor(Config.DISCORD_REPORT_AUTHOR
                        .replace("%player%", player.getName())
                        .replace("%target%", target.getName())
                        .replace("%report%", report), Config.DISCORD_REPORT_AUTHOR_URL, Config.DISCORD_REPORT_AUTHOR_ICON)
                .setDescription(Config.DISCORD_REPORT_DESCRIPTION
                        .replace("%player%", player.getName())
                        .replace("%target%", target.getName())
                        .replace("%report%", report))
                .setColor(Color.decode(Config.DISCORD_REPORT_COLOR))
                .setFooter(Config.DISCORD_REPORT_FOOTER, Config.DISCORD_REPORT_FOOTER_ICON)
                .setThumbnail(Config.DISCORD_REPORT_THUMBNAIL));
    }
}