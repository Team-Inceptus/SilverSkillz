package us.teaminceptus.silverskillz;

import java.io.File;
import java.io.IOException;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.silverskillz.skills.Skill;
import us.teaminceptus.silverskillz.skills.SkillInstance;

/**
 * Represents a Player in the SilverSkillz
 * @author Team Inceptus
 *
 */
public class SilverPlayer {

	private final OfflinePlayer player;

	private final File file;
	private static final File directory = new File(JavaPlugin.getPlugin(SilverSkillz.class).getDataFolder().getPath() + "/players");;
	private final FileConfiguration playerConfig;
	
	/**
	 * Generate a SilverPlayer from an OfflinePlayer
	 * @param p The Player to Use
	 */
	public SilverPlayer(OfflinePlayer p) {
		this.player = p;

		if (!(SilverPlayer.directory.exists())) {
			SilverPlayer.directory.mkdir();
		}

		this.file = new File(SilverPlayer.directory, p.getUniqueId().toString() + ".yml");

		if (!(this.file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		this.playerConfig = YamlConfiguration.loadConfiguration(this.file);
		
		reloadValues();
	}
	
	/**
	 * Gets the directory of all of the player files
	 * @return File of player directory
	 */
	public static final File getPlayerDirectory() {
		return SilverPlayer.directory;
	}
	
	/**
	 * Gets the data file for this player
	 * @return File of this player
	 */
	public final File getPlayerFile() {
		return this.file;
	}
	
	/**
	 * Reloads values for this player
	 */
	public final void reloadValues() {
		SilverSkillz.reloadMessagesFile();
		
		OfflinePlayer p = player;
		// Settings

		if (playerConfig.get("settings") == null) {
			playerConfig.createSection("settings");
		}

		if (!(playerConfig.isConfigurationSection("settings"))) {
			playerConfig.set("settings", null);
			playerConfig.createSection("settings");
		}

		ConfigurationSection settings = playerConfig.getConfigurationSection("settings");

		if (settings.get("messages") == null) {
			settings.set("messages", true);
		}

		if (!(settings.isBoolean("messages"))) {
			settings.set("messages", true);
		}
		
		if (settings.get("potion-effects") == null) {
			settings.set("potion-effects", true);
		}
		
		if (!(settings.isBoolean("custom-messages"))) {
			settings.set("custom-messages", true);
		}

		if (!(settings.isBoolean("abilities"))) {
			settings.set("abilities", true);
		}

		// Other

		if (playerConfig.get("uuid") == null) {
			playerConfig.set("uuid", p.getUniqueId().toString());
		}

		if (!(playerConfig.isString("uuid"))) {
			playerConfig.set("uuid", p.getUniqueId().toString());
		}

		if (playerConfig.get("operator") == null) {
			playerConfig.set("operator", p.isOp());
		}

		if (!(playerConfig.isBoolean("operator"))) {
			playerConfig.set("operator", p.isOp());
		}

		if (playerConfig.get("name") == null) {
			playerConfig.set("name", p.getName());
		}

		if (!(playerConfig.isString("name"))) {
			playerConfig.set("name", p.getName());
		}

		if (playerConfig.get("skills") == null) {
			playerConfig.createSection("skills");
		}

		if (!(playerConfig.isConfigurationSection("skills"))) {
			playerConfig.set("skills", null);
			playerConfig.createSection("skills");
		}

		ConfigurationSection skills = playerConfig.getConfigurationSection("skills");

		for (Skill s : Skill.values()) {
			if (skills.get(s.getName()) == null) {
				skills.createSection(s.getName());
			}
		}

		for (Skill s: Skill.values()) {
			if (!(skills.isConfigurationSection(s.getName()))) {
				skills.set(s.getName(), null);
				skills.createSection(s.getName());
			}
		}

		for (Skill s : Skill.values()) {
			ConfigurationSection skill = skills.getConfigurationSection(s.getName());

			if (skill.get("name") == null) {
				skill.set("name", s.getName());
			}

			if (!(skill.isString("name"))) {
				skill.set("name", s.getName());
			}

			if (skill.get("progress") == null) {
				skill.set("progress", 0);
			}

			if (!(skill.isDouble("progress"))) {
				skill.set("progress", 0);
			}

			if (skill.get("level") == null) {
				skill.set("level", 0);
			}

			if (!(skill.isInt("level"))) {
				skill.set("level", 0);
			}
		}
	}
	
	/**
	 * The OfflinePlayer version of this player
	 * @return OfflinePlayer for this player
	 */
	public OfflinePlayer getPlayer() {
		return this.player;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SilverPlayer)) return false;
		
		SilverPlayer other = (SilverPlayer) obj;
		
		return (other.getPlayer().getUniqueId().equals(this.getPlayer().getUniqueId()));
	}
	
	/**
	 * The Player version of this player
	 * @return Player for this player, may be null
	 */
	public Player getOnlinePlayer() {
		if (getPlayer().isOnline()) {
			return ((Player) getPlayer());
		} else return null;
	}
	
	/**
	 * Get the instance of this player's configuration file
	 * @return FileConfiguration of this player
	 */
	public final FileConfiguration getPlayerConfig() {
		return this.playerConfig;
	}

	// Actions
	/**
	 * If player has skill messages on
	 * @return true if skill messages are on
	 */
	public final boolean canSeeSkillMessages() {
		return this.playerConfig.getConfigurationSection("settings").getBoolean("messages");
	}
	
	/**
	 * If player has potion effects on
	 * @return true if potion effects are enabled
	 */
	public final boolean hasPotionEffects() {
		return this.playerConfig.getConfigurationSection("settings").getBoolean("potion-effects");
	}
	
	/**
	 * If player is using the messages in messages.yml
	 * @return true if on, else false
	 */
	public final boolean hasMessagesOn() {
		return this.playerConfig.getConfigurationSection("settings").getBoolean("custom-messages");
	}
	
	/**
	 * Get a skill instance for this player
	 * @param skill Skill to get instance from
	 * @return SkillInstance for this skill, for this player
	 */
	public final SkillInstance getSkill(Skill skill) {
		return new SkillInstance(skill, this);
	}

	public final boolean hasAbilities() throws IllegalStateException {
		if (!(SilverSkillz.isPremium())) throw new IllegalStateException("Please buy the Premium Version to use this method");
		return this.playerConfig.getConfigurationSection("settings").getBoolean("abilities");
	}
}