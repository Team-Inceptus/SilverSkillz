package me.gamercoder215.silverskillz.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gamercoder215.silverskillz.SilverPlayer;
import me.gamercoder215.silverskillz.SilverSkillz;
import me.gamercoder215.silverskillz.skills.Skill;

public final class AddProgressCommand implements CommandExecutor {
	
	protected SilverSkillz plugin;
	
	public AddProgressCommand(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getCommand("addprogress").setExecutor(this);
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		
		if (args.length < 1) {
			SilverSkillz.sendPluginMessage(sender, "Please provide a valid player.");
			return false;
		}
		
		if (Bukkit.getPlayer(args[0]) == null) {
			SilverSkillz.sendPluginMessage(sender, "Please provide a valid player.");
			return false;
		}
		
		SilverPlayer target = new SilverPlayer(Bukkit.getPlayer(args[0]));
		if (args.length < 2) {
			SilverSkillz.sendPluginMessage(sender, "Please provide an increase amount.");
			return false;
		}
		
		try {
			if (!(args.length < 3)) {
				target.getSkill(Skill.matchSkill(args[2])).addProgress(Double.parseDouble(args[1]));
				sender.sendMessage(ChatColor.GREEN + "Increase skill successful.");
			} else {
				for (Skill s : Skill.values()) {
					target.getSkill(s).addProgress(Double.parseDouble(args[1]));
				}
				sender.sendMessage(ChatColor.GREEN + "Increase skills successful.");
			}
		} catch (IllegalArgumentException e) {
			SilverSkillz.sendPluginMessage(sender, "There was an error parsing arguments.");
			return false;
		} catch (NullPointerException e) {
			SilverSkillz.sendPluginMessage(sender, "There was an error parsing arguments.");
			return false;
		} catch (Exception e) {
			SilverSkillz.sendPluginMessage(sender, "There was an error:\n" + e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}

}
