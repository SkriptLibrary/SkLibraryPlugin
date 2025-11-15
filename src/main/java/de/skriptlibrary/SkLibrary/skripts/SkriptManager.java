package de.skriptlibrary.SkLibrary.skripts;

import de.skriptlibrary.SkLibrary.SkLibrary;
import lombok.Getter;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SkriptManager {
    public final Path SkriptsPath = Path.of("plugins", "Skript", "scripts");
    public final Path SkLibrarySkriptsPath = Path.of(SkriptsPath.toString(), ".SkLibrary");
    public final String SkLibraryRepoBaseUrl = "https://raw.githubusercontent.com/SkriptLibrary/SkLibrary/refs/heads/main/";
    public final String SkLibraryRepoSkriptsUrl = SkLibraryRepoBaseUrl + "skripts/";
//    public final String SkLibraryRepoBaseUrl = "http://localhost:5000/";
//    public final String SkLibraryRepoSkriptsUrl = "http://localhost:5000/skripts/";
    
    @Getter
    private final List<Skript> skripts = new ArrayList<>();
    
    @Getter
    private List<String> AvailableSkripts = new ArrayList<>();
    
    public SkriptManager() {
        
        if (!this.SkriptsFolderExists()) {
            SkriptsPath.toFile().mkdirs();
        }
        
        if (!this.SkLibrarySkriptsFolderExists()) {
            SkLibrarySkriptsPath.toFile().mkdirs();
        }

        try {
            this.LoadAvailableSkripts();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void LoadAvailableSkripts() throws IOException, URISyntaxException {
        URI uri = new URI(SkLibraryRepoBaseUrl + "index");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()))) {
            this.AvailableSkripts = reader.lines().collect(Collectors.toList());
        }
    }
    
    public void LoadAllSkripts() throws IOException, RuntimeException {
        skripts.clear();
        
        Files.list(SkLibrarySkriptsPath).filter(Files::isRegularFile).forEach(path -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(path.toUri().toURL().openStream()))) {
                String id = this.GetFileNameOfPath(path);
                Skript skript = this.readSkriptMetaData(reader.lines().collect(Collectors.toList()), id);
                this.AddSkriptIfNotExists(skript);
                
                skript.DownloadSkriptDependencies();
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    private Skript readSkriptMetaData(List<String> lines, String id) throws IOException {
        String name = null;
        String version = null;
        EnumSet<SkriptFlag> flags = EnumSet.noneOf(SkriptFlag.class);
        Map<String, String> addonDependencies = new HashMap<>();
        Map<String, String> skriptDependencies = new HashMap<>();

        for (String line : lines) {
            if (!line.startsWith("#")) break;

            line = line.replaceFirst("#", "").trim();

            if (line.isEmpty()) continue;

            if (!line.startsWith("@")) continue;

            String[] parts = line.split(" ");
            String option = parts[0].replaceFirst("@", "");
            String value = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));

            switch (option) {
                case "name":
                    name = value;
                    break;
                case "version":
                    version = value;
                    break;
                case "addonDependency":
                    addonDependencies.put(parts[1], parts[2]);
                    break;
                case "skriptDependency":
                    skriptDependencies.put(parts[1], parts[2]);
                default:
                    try {
                        SkriptFlag flag = SkriptFlag.fromString(option);
                        flags.add(flag);
                    } catch (IllegalArgumentException e) {
                        SkLibrary.instance.getLogger().warning("Invalid option: " + option);
                    }
            }

        }
        
        return new Skript(id, name, version, flags, addonDependencies, skriptDependencies);
    }
    
    public void DownloadSkript(String id, String version) throws IOException, URISyntaxException {
        
        if (this.SkriptExists(id, version)) {
            return;
        }
        
        List<String> lines = new ArrayList<>();
        URI url = new URI(SkLibraryRepoSkriptsUrl + id + "/" + id + ".sk");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.toURL().openStream()));
        BufferedWriter writer = Files.newBufferedWriter(Path.of(SkLibrarySkriptsPath.toString(), id + ".sk"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
                
                lines.add(line);
            }
        }
        
        Skript skript = this.readSkriptMetaData(lines, id);

        this.skripts.add(skript);

        skript.DownloadSkriptDependencies();
    }
    
    public Boolean SkriptExists(String id) {
        return skripts.stream().anyMatch(s -> s.id.equals(id));
    } 
    
    public Boolean SkriptExists(String id, String version) {
        return skripts.stream().anyMatch(s -> s.id.equals(id) && s.Version.equals(version));
    }
    
    private void AddSkriptIfNotExists(Skript skript) {
        if (this.SkriptExists(skript.id, skript.Version)) return;
        skripts.add(skript);
    }
    
    public Boolean SkriptsFolderExists() {
        return Files.exists(SkriptsPath);
    }
    
    public Boolean SkLibrarySkriptsFolderExists() {
        return Files.exists(SkLibrarySkriptsPath);
    }
    
    private String GetFileNameOfPath(Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }
}
