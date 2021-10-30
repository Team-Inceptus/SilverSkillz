package me.gamercoder215.silverskillz;

import org.bukkit.plugin.java.JavaPlugin;

import me.gamercoder215.silverskillz.skills.SkillAdvancer;
import me.gamercoder215.silverskillz.skills.SkillUtils;

public class SilverSkillz extends JavaPlugin {

	public void onEnable() {
		this.saveDefaultConfig();
		this.saveConfig();
		
		new SkillAdvancer(this);
		new SkillUtils(this);
		
		this.saveConfig();
	}

}