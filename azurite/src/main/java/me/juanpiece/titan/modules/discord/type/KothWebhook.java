package me.juanpiece.titan.modules.discord.type;

import me.juanpiece.titan.modules.discord.Discord;
import me.juanpiece.titan.modules.discord.extra.DiscordWebhook;
import me.juanpiece.titan.modules.events.koth.Koth;
import me.juanpiece.titan.modules.framework.Config;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.utils.Formatter;

import java.awt.*;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class KothWebhook extends Discord {

    public KothWebhook(Manager manager, Koth koth) {
        super(manager, Config.DISCORD_KOTH_WEBHOOKURL);
        discordWebhook.setContent(Config.DISCORD_KOTH_CONTENT
                .replace("%koth%", koth.getName())
                .replace("%time%", Formatter.getRemaining(koth.getMinutes(), false))
                .replace("%points%", String.valueOf(koth.getPointsReward()))
        );
        discordWebhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setAuthor(Config.DISCORD_KOTH_AUTHOR
                        .replace("%koth%", koth.getName())
                        .replace("%time%", Formatter.getRemaining(koth.getMinutes(), false))
                        .replace("%points%", String.valueOf(koth.getPointsReward())), Config.DISCORD_KOTH_AUTHOR_URL, Config.DISCORD_KOTH_AUTHOR_ICON)
                .setDescription(Config.DISCORD_KOTH_DESCRIPTION
                        .replace("%koth%", koth.getName())
                        .replace("%time%", Formatter.getRemaining(koth.getMinutes(), false))
                        .replace("%points%", String.valueOf(koth.getPointsReward())))
                .setColor(Color.decode(Config.DISCORD_KOTH_COLOR))
                .setFooter(Config.DISCORD_KOTH_FOOTER, Config.DISCORD_KOTH_FOOTER_ICON)
                .setThumbnail(Config.DISCORD_KOTH_THUMBNAIL));
    }
}