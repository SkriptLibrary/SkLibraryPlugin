package de.skriptlibrary.SkLibrary.commands;

import de.skriptlibrary.SkLibrary.SkLibrary;
import de.skriptlibrary.SkLibrary.skripts.Skript;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class SkLibraryCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        String usageMessage = "Usage: /SkLibrary <download/info/list/reload>";
        
        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return true;
        }
        
        switch(args[0].toLowerCase()) {
            case "download":
                if (args.length == 1) {
                    sender.sendMessage("Please specify a Skript id!");
                    return true;
                }
                
                if (!SkLibrary.skriptManager.getAvailableSkripts().contains(args[1])) {
                    sender.sendMessage(ChatColor.RED + "There is no Skript with that id!");
                    return true;
                }
                
                String id = args[1];
                String version = args.length >= 3 ? args[2] : "latest";

                if (SkLibrary.skriptManager.SkriptExists(id)) {
                    sender.sendMessage(ChatColor.RED + "There is already a Skript with that id!");
                    return true;
                }
                
                try {
                    SkLibrary.skriptManager.DownloadSkript(id, version);
                } catch (IOException | URISyntaxException e) {
                    sender.sendMessage(ChatColor.RED + "Failed to download the Skript with that id and version! (" + e.getMessage() + ")");
                    return true;
                }
                break;
            /*case "update":
                sender.sendMessage("Update");
                break;*/
            case "list":
                List<Skript> skripts = SkLibrary.skriptManager.getSkripts();
                
                TextComponent tc = Component.text("Loaded Skripts: \n");

                for (int i = 0; i < skripts.size(); i++) {
                    
                    Skript skript = skripts.get(i);
                    
                    TextComponent htc = Component.text(skript.Name).color(TextColor.fromHexString("#00cc00"));
                    
                    htc = htc.append(Component.text( " (" + skript.id + ") - " + skript.Version));
                    
                    if (!skript.SkriptDependencies.isEmpty()) {
                        htc = htc.append(Component.text("\n\nskript dependencies:\n"));

                        for (Map.Entry<String, String> dep : skript.SkriptDependencies.entrySet()) {
                            htc = htc.append(Component.text("- " + dep.getKey()/* + ": " + dep.getValue()*/));
                        }
                    }
                    
                    tc = tc.append(Component.text(skript.Name).hoverEvent(HoverEvent.showText(htc)).color(TextColor.fromHexString("#00cc00")));

                    if (i != skripts.size() - 1) {
                        tc = tc.append(Component.text(", "));
                    }
                }

                sender.sendMessage(tc);
                
                break;
            case "reload":
                try {
                    SkLibrary.skriptManager.LoadAllSkripts();
                    SkLibrary.skriptManager.LoadAvailableSkripts();
                    sender.sendMessage(ChatColor.GREEN + "Sucessfully loaded SkLibrary!");
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "info":
                sender.sendMessage("Version: " + SkLibrary.instance.getPluginMeta().getVersion());
                break;
            default:
                sender.sendMessage(usageMessage);
                break;
                
        }
        
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        
        return switch (args.length) {
            case 1 -> List.of("download", /*"update", */"info", "list", "reload");
            case 2 -> switch (args[0].toLowerCase()) {
                case "download" -> SkLibrary.skriptManager.getAvailableSkripts();
                default -> List.of();
            };
            default -> List.of();
        };
    }
}
