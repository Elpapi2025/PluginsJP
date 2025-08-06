package me.juanpiece.titan.modules.versions;

import lombok.Getter;
import lombok.SneakyThrows;
import me.juanpiece.titan.HCF;
import me.juanpiece.titan.modules.framework.Manager;
import me.juanpiece.titan.utils.Logger;
import me.juanpiece.titan.utils.Utils;
import org.bukkit.Bukkit;

/**
 * Copyright (c) 2023. Juanpiece
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class VersionManager extends Manager {

    @Getter
    private final Version version;

    public VersionManager(HCF instance) {
        super(instance);
        this.version = setVersion();
    }

    @SneakyThrows
    public Version setVersion() {
        String path = "me.juanpiece.titan.modules.versions.type.Version" + Utils.getNMSVer();

        try {

            return (Version) Class.forName(path).getConstructor(VersionManager.class).newInstance(this);

        } catch (ClassNotFoundException e) {
            Bukkit.getServer().shutdown();
            Logger.print(
                    Logger.LINE_CONSOLE,
                    "- &dTitan HCF",
                    "- &cThis version is not supported.",
                    Logger.LINE_CONSOLE
            );
            return null;
        }
    }
}