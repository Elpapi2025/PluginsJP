package me.juanpiece.titan.modules.discord;

import me.juanpiece.titan.modules.discord.extra.DiscordWebhook;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.modules.framework.Module;
import me.juanpiece.titan.utils.Tasks;

import java.io.IOException;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class Discord extends Module<Manager> {

    protected final DiscordWebhook discordWebhook;

    public Discord(Manager manager, String webhookURL) {
        super(manager);
        this.discordWebhook = new DiscordWebhook(webhookURL);
    }

    public void execute() {
        try {

            discordWebhook.execute();

        } catch (IOException e) {
            throw new IllegalArgumentException("[Titan] Your discord webhook is incorrect!");
        }
    }

    public void executeAsync() {
        Tasks.executeAsync(getManager(), () -> {
            try {

                discordWebhook.execute();

            } catch (IOException e) {
                throw new IllegalArgumentException("[Titan] Your discord webhook is incorrect!");
            }
        });
    }
}