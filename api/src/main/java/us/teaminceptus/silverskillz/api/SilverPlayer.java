package us.teaminceptus.silverskillz.api;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.teaminceptus.silverskillz.api.skills.Skill;
import us.teaminceptus.silverskillz.api.skills.SkillInstance;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Represents a Player in the SilverSkillz
 *
 */
public final class SilverPlayer {

	private final OfflinePlayer p;

	private final File file;
	private static final File directory = new File(SilverConfig.getDataFolder(), "players");
	private final FileConfiguration pConfig;
	
	/**
	 * Generate a SilverPlayer from an OfflinePlayer
	 * @param p The Player to Use
	 * @throws IllegalArgumentException if player is null
	 */
	public SilverPlayer(@NotNull OfflinePlayer p) throws IllegalArgumentException {
		Validate.notNull(p, "Player is null");
		this.p = p;

		if (!(SilverPlayer.directory.exists())) SilverPlayer.directory.mkdir();

		file = new File(SilverPlayer.directory, p.getUniqueId() + ".yml");

		if (!(file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		pConfig = YamlConfiguration.loadConfiguration(file);
		reloadValues();
	}
	
	/**
	 * Gets the directory of all of the player files
	 * @return File of player directory
	 */
	@NotNull
	public static File getPlayerDirectory() {
		return SilverPlayer.directory;
	}
	
	/**
	 * Gets the data file for this player
	 * @return File of this player
	 */
	@NotNull
	public File getPlayerFile() {
		return file;
	}
	
	/**
	 * Reloads values for this player
	 */
	public void reloadValues() {
		OfflinePlayer p = this.p;
		// Settings

		if (!(pConfig.isConfigurationSection("settings"))) pConfig.createSection("settings");

		ConfigurationSection settings = pConfig.getConfigurationSection("settings");

		if (!(settings.isBoolean("messages"))) settings.set("messages", true);
		if (!(settings.isBoolean("potion-effects"))) settings.set("potion-effects", true);
		if (!(settings.isBoolean("abilities"))) settings.set("abilities", true);

		// Other

		if (!(pConfig.isString("uuid"))) pConfig.set("uuid", p.getUniqueId().toString());
		if (!(pConfig.isBoolean("operator"))) pConfig.set("operator", p.isOp());
		if (!(pConfig.isString("name"))) pConfig.set("name", p.getName());
		if (!(pConfig.isConfigurationSection("skills"))) pConfig.createSection("skills");

		ConfigurationSection skills = pConfig.getConfigurationSection("skills");

		for (Skill s : Skill.values()) {
			if (!(skills.isConfigurationSection(s.getName()))) skills.createSection(s.getName());

			ConfigurationSection skill = skills.getConfigurationSection(s.getName());

			if (!(skill.isString("name"))) skill.set("name", s.getName());
			if (!(skill.isDouble("progress"))) skill.set("progress", 0);
			if (!(skill.isInt("level"))) skill.set("level", 0);
		}
	}
	
	/**
	 * The OfflinePlayer version of this player
	 * @return OfflinePlayer for this player
	 */
	public OfflinePlayer getPlayer() {
		return p;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SilverPlayer other)) return false;
		return (other.getPlayer().getUniqueId().equals(getPlayer().getUniqueId()));
	}
	
	/**
	 * The Player version of this player
	 * @return Player for this player, may be null
	 */
	@Nullable
	public Player getOnlinePlayer() {
		if (getPlayer().isOnline()) return ((Player) getPlayer());
		else return null;
	}

	/**
	 * Whether player has notificatons
	 * @return true if notifications are on, else false
	 */
	public boolean hasNotifications() {
		return pConfig.getConfigurationSection("settings").getBoolean("messages");
	}
	
	/**
	 * Get the instance of this player's configuration file
	 * @return FileConfiguration of this player
	 */
	@NotNull
	public FileConfiguration getPlayerConfig() {
		return pConfig;
	}

	// Actions
	
	/**
	 * Whether player has potion effects on
	 * @return true if potion effects, else false
	 */
	public boolean hasPotionEffects() { return pConfig.getConfigurationSection("settings").getBoolean("potion-effects"); }

	/**
	 * Whether player has abilities on
	 * @return true if abilities on, else false
	 */
	public boolean hasAbilities() {
		return pConfig.getConfigurationSection("settings").getBoolean("abilities");
	}
	
	/**
	 * Get a skill instance for this player
	 * @param skill Skill to get instance from
	 * @return SkillInstance for this skill, for this player
	 * @throws IllegalArgumentException if skill is null
	 */
	public SkillInstance getSkill(@NotNull Skill skill) throws IllegalArgumentException {
		Validate.notNull(skill, "Skill cannot be null");
		return new SkillInstance(skill, this);
	}

	/**
	 * Fetches this player's UUID.
	 * @return UUID
	 */
	public UUID getUUID() {
		return this.p.getUniqueId();
	}

	/**
	 * Fetches this player's name.
	 * @return Name
	 */
	public String getName() {
		return this.p.getName();
	}

}