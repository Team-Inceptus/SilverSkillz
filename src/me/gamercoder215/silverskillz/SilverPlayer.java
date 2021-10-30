package me.gamercoder215.silverskillz;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import me.gamercoder215.silverskillz.skills.Skill;
import me.gamercoder215.silverskillz.skills.SkillInstance;

public class SilverPlayer {

	private final OfflinePlayer player;
	private final Plugin plugin;

	private final File file;
	private final File directory;
	private final FileConfiguration playerConfig;

	private SilverPlayer(OfflinePlayer p) {
		this.plugin = JavaPlugin.getPlugin(SilverSkillz.class);
		
		this.player = p;

		this.directory = new File(plugin.getDataFolder().getPath() + "players");

		if (!(this.directory.exists())) {
			this.directory.mkdir();
		}

		this.file = new File(this.directory, p.getUniqueId().toString() + ".yml");

		if (!(this.file.exists())) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		this.playerConfig = YamlConfiguration.loadConfiguration(this.file);

	}

	public void reloadValues() {
		OfflinePlayer p = this.player;
		
		if (this.playerConfig.get("uuid") == null) {
			this.playerConfig.set("uuid", p.getUniqueId().toString());
		}

		if (!(this.playerConfig.get("uuid") instanceof UUID)) {
			this.playerConfig.set("uuid", p.getUniqueId().toString());
		}

		if (this.playerConfig.get("operator") == null) {
			this.playerConfig.set("operator", p.isOp());
		}

		if (!(this.playerConfig.get("operator") instanceof Boolean)) {
			this.playerConfig.set("operator", p.isOp());
		}

		if (this.playerConfig.get("name") == null) {
			this.playerConfig.set("name", p.getName());
		}

		if (!(this.playerConfig.get("name") instanceof String)) {
			this.playerConfig.set("name", p.getName());
		}

		if (this.playerConfig.get("skills") == null) {
			this.playerConfig.createSection("skills");
		}

		if (!(this.playerConfig.get("skills") instanceof ConfigurationSection)) {
			this.playerConfig.set("skills", null);
			this.playerConfig.createSection("skills");
		}

		ConfigurationSection skills = this.playerConfig.getConfigurationSection("skills");

		for (Skill s : Skill.values()) {
			if (skills.get(s.getName()) == null) {
				skills.createSection(s.getName());
			}
		}

		for (Skill s: Skill.values()) {
			if (!(skills.get(s.getName()) instanceof ConfigurationSection)) {
				skills.set(s.getName(), null);
				skills.createSection(s.getName());
			}
		}

		for (Skill s : Skill.values()) {
			ConfigurationSection skill = skills.getConfigurationSection(s.getName());

			if (skill.get("name") == null) {
				skill.set("name", s.getName());
			}

			if (!(skill.get("name") instanceof String)) {
				skill.set("name", s.getName());
			}

			if (skill.get("progress") == null) {
				skill.set("progress", 0);
			}

			if (!(skill.get("progress") instanceof Integer)) {
				skill.set("progress", 0);
			}

			if (skill.get("level") == null) {
				skill.set("level", 0);
			}

			if (!(skill.get("level") instanceof Integer)) {
				skill.set("level", 0);
			}
		}

		try {
			this.playerConfig.save(this.file);
		} catch (IOException e) {
			plugin.getLogger().info("Error reloading config");
			e.printStackTrace();
		}
	}
	
	public OfflinePlayer getPlayer() {
		return this.player;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof SilverPlayer)) return false;
		
		SilverPlayer other = (SilverPlayer) obj;
		
		return (other.getPlayer().getUniqueId().equals(this.getPlayer().getUniqueId()));
	}
	
	@Nullable
	public Player getOnlinePlayer() {
		if (getPlayer().isOnline()) {
			return ((Player) getPlayer());
		} else return null;
	}

	public FileConfiguration getPlayerConfig() {
		return this.playerConfig;
	}

	public static SilverPlayer fromPlayer(OfflinePlayer p) {
		return new SilverPlayer(p);
	}

	// Actions

	public SkillInstance getSkill(Skill skill) {
		return new SkillInstance(skill, this);
	}

}