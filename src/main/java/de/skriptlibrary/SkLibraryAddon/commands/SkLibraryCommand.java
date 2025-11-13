package de.skriptlibrary.SkLibraryAddon.commands;

import de.skriptlibrary.SkLibraryAddon.SkLibraryAddon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SkLibraryCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /SkLibraryAddon <download/update/info>");
            return true;
        }
        
        switch(args[0].toLowerCase()) {
            case "download":
                sender.sendMessage("Download");
                break;
            case "update":
                sender.sendMessage("Update");
                break;
            case "info":
                sender.sendMessage("Version: " + SkLibraryAddon.instance.getPluginMeta().getVersion());
                break;
            default:
                sender.sendMessage("Usage: /SkLibraryAddon <download/update/info>");
                break;
                
        }
        
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of("download", "update", "info");
    }
}
