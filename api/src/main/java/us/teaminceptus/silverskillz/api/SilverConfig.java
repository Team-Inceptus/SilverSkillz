package us.teaminceptus.silverskillz.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.silverskillz.api.language.Language;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represents the SilverSkillz Configuration
 */
public interface SilverConfig {

    /**
     * Represents the Plugin Name
     */
    String PLUGIN_NAME = "SilverSkillz";

    /**
     * Fetches the SilverSkillz Plugin Instance.
     * @return Plugin Instance
     */
    static Plugin getPlugin() { return Bukkit.getPluginManager().getPlugin(PLUGIN_NAME); }

    /**
     * Fetches this Configuration Instance.
     * @return Config Instance
     */
    static SilverConfig getConfig() { return (SilverConfig) getPlugin(); }

    /**
     * Fetches the Plugin's Logger.
     * @return Plugin's Logger
     */
    static Logger getLogger() {
        return getPlugin().getLogger();
    }

    /**
     * Fetches the Plugin's Data Folder.
     * @return Data Folder
     */
    static File getDataFolder() {
        return getPlugin().getDataFolder();
    }

    /**
     * Fetches a Value from the current language.
     * @param key Key to use
     * @return found string value, or "Unknown Value" if not found
     */
    @NotNull
    static String getConstant(String key) {
        String lang = ("en".equals(getConfig().getCurrentLanguage()) ? "" : getConfig().getCurrentLanguage());
        Language l = Language.getById(lang);
        return l.getMessage(key);
    }

    /**
     * Fetches a Message from the current language.
     * <p>
     * This method is used for messages because it places {@code plugin.prefix} before fetching the key.
     * @param key Key to fetch
     * @return found message, or "Unknown Value" if not found
     */
    @NotNull
    static String getMessage(String key) {
        return getConstant("plugin.prefix") + getConstant(key);
    }

    // Implementation

    /**
     * Fetch the current Language.
     * @return Current Language
     */
    String getCurrentLanguage();

    /**
     * Whether notifications (display messages) are on.
     * @return true if turned on, else false
     */
    boolean hasNotifications();

}
