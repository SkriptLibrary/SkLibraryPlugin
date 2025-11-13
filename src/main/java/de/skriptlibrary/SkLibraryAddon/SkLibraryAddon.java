package de.skriptlibrary.SkLibraryAddon;

import de.skriptlibrary.SkLibraryAddon.commands.SkLibraryCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkLibraryAddon extends JavaPlugin {

    public static SkLibraryAddon instance;
    
    @Override
    public void onEnable() {
        
        instance = this;
        
        getCommand("sklibrary").setExecutor(new SkLibraryCommand());
        
    }

    @Override
    public void onDisable() {
    }
}
