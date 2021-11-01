package me.gamercoder215.silverskillz.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gamercoder215.silverskillz.SilverSkillz;
import me.gamercoder215.silverskillz.skills.Skill;

public final class SkillsCommand implements CommandExecutor {
	
	protected SilverSkillz plugin;
	
	public SkillsCommand(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getCommand("skills").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		
		Player p = (Player) sender;
		
		p.openInventory(Skill.getMenu());
		
		return true;
	}

}
