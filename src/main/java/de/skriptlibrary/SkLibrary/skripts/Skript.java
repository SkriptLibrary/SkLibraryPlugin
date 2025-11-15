package de.skriptlibrary.SkLibrary.skripts;

import de.skriptlibrary.SkLibrary.SkLibrary;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Skript {

    public String id;
    public String Name;
    public String Version;
    public EnumSet<SkriptFlag> Flags;
    public Map<String, String> AddonDependencies;
    public Map<String, String> SkriptDependencies;

    public void DownloadSkriptDependencies() {
        this.SkriptDependencies.forEach((key, value) -> {
            try {
                if (!Files.exists(Path.of(SkLibrary.skriptManager.SkLibrarySkriptsPath.toString(), key, key + ".sk"))) {
                    SkLibrary.skriptManager.DownloadSkript(key, value);
                }
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public Skript(
            String id,
            String name,
            String version
    ) {
        this(id, name, version, EnumSet.noneOf(SkriptFlag.class), new HashMap<>(), new HashMap<>());
    }

    public Skript(
            String id,
            String name,
            String version,
            EnumSet<SkriptFlag> flags
    ) {
        this(id, name, version, flags, new HashMap<>(), new HashMap<>());
    }

    public Skript(
            String id,
            String name,
            String version,
            EnumSet<SkriptFlag> flags,
            Map<String, String> addonDependencies
    ) {
        this(id, name, version, flags, addonDependencies, new HashMap<>());
    }
    
    public Skript(
            String id,
            String name,
            String version,
            EnumSet<SkriptFlag> flags,
            Map<String, String> addonDependencies,
            Map<String, String> skriptDependencies
    ) {
        this.id = id;
        this.Name = name;
        this.Version = version;
        this.Flags = flags;
        this.AddonDependencies = addonDependencies;
        this.SkriptDependencies = skriptDependencies;
    }
}
