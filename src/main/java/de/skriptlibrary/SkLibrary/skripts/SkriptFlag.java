package de.skriptlibrary.SkLibrary.skripts;

public enum SkriptFlag {
    Standalone("standalone"),
    Library("library");
    
    public final String id;
    
    SkriptFlag(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
    public static SkriptFlag fromString(String value) {
        for (SkriptFlag skriptFlag : values()) {
            if (skriptFlag.id.equalsIgnoreCase(value)) {
                return skriptFlag;
            }
        }
        
        throw new IllegalArgumentException("Unknown SkriptFlag: " + value);
    }
}
