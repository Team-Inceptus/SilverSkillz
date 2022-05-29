package us.teaminceptus.silverskillz.api.language;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.silverskillz.api.SilverConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Represents an API Language
 */
public enum Language {

    /**
     * Represents English
     */
    ENGLISH(""),
    /**
     * Represents Spanish / Español
     */
    SPANISH("_es"),
    /**
     * Represents German / Deutsch
     */
    GERMAN("_de"),
    /**
     * Represents French / Français
     */
    FRENCH("_fr")
    ;

    private final String id;

    Language(String id) {
        this.id = id;
    }

    /**
     * Fetches the identifier for this Language.
     * @return Identifier Language
     */
    @NotNull
    public String getIdentifier() {
        return this.id.replace("_", "");
    }

    /**
     * Fetches the Message for this Language.
     * @param key Key message
     * @return Message if found, or null if not found
     */
    @Nullable
    public String getMessage(String key) {
        if (key == null) return null;

        try {
            Properties p = new Properties();
            p.load(Files.newInputStream(new File(SilverConfig.getDataFolder(), "silverskillz" + id + ".properties").toPath()));

            return ChatColor.translateAlternateColorCodes('&', p.getProperty(key, "Unknown Value"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Fetches the Language associated with this id.
     * @param id ID to fetch
     * @return Language found, or null if not found
     */
    @Nullable
    public static Language getById(String id) {
        for (Language l : values()) {
            if (l.getIdentifier().equalsIgnoreCase(id)) return l;
        }

        return null;
    }



}
