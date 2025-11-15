package de.skriptlibrary.SkLibrary;

import de.skriptlibrary.SkLibrary.commands.SkLibraryCommand;
import de.skriptlibrary.SkLibrary.skripts.SkriptManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class SkLibrary extends JavaPlugin {

    public static SkLibrary instance;
    public static SkriptManager skriptManager;
    
    @Override
    public void onEnable() {
        
        instance = this;
        
        skriptManager = new SkriptManager();

        try {
            skriptManager.LoadAllSkripts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Metrics metrics = new Metrics(this, 27995);
        Plugin skriptPlugin = Bukkit.getServer().getPluginManager().getPlugin("Skript");
        
        if (skriptPlugin != null) {
            metrics.addCustomChart(new SimplePie("skript_version", () -> skriptPlugin.getPluginMeta().getVersion()));
        }
        

        getCommand("sklibrary").setExecutor(new SkLibraryCommand());
        
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getCommandMap().getKnownCommands().remove("sklibrary");
        Bukkit.getServer().getCommandMap().getKnownCommands().remove("sklibrary:sklibrary");
    }
}
