package me.titan.core.modules.versions;

import lombok.Getter;
import me.titan.core.modules.framework.*;
import me.titan.core.modules.versions.type.*;
import org.bukkit.*;
import me.titan.core.utils.*;
import me.titan.core.*;

import java.lang.reflect.InvocationTargetException;

@Getter
public class VersionManager extends Manager {

    private final Version version;

    public boolean isVer16() {
        return this.version instanceof Version1_16_R3;
    }

    public Version setVersion() {
        String classname = "me.titan.core.modules.versions.type.Version" + Utils.getNMSVer();
        try {
            return (Version)Class.forName(classname).getConstructor(VersionManager.class).newInstance(this);
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            Bukkit.getServer().shutdown();
            Logger.print(Logger.LINE_CONSOLE, "- &dTitan HCF", "- &cThis version is not supported.", Logger.LINE_CONSOLE);
            return null;
        }
    }
    
    public VersionManager(HCF plugin) {
        super(plugin);
        this.version = this.setVersion();
    }
}
