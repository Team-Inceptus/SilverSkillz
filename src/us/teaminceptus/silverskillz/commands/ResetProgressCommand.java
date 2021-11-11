package us.teaminceptus.silverskillz.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.teaminceptus.silverskillz.SilverPlayer;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.skills.Skill;

public final class ResetProgressCommand implements CommandExecutor {
	
	protected SilverSkillz plugin;
	
	public ResetProgressCommand(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getCommand("resetprogress").setExecutor(this);
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
		
		try {
			if (args.length < 2) {
				for (Skill s : Skill.values()) {
					target.getSkill(s).setProgress(0);
				}
				sender.sendMessage(ChatColor.GREEN + "Successfully reset progress for each skill.");
			} else {
				target.getSkill(Skill.matchSkill(args[1])).setProgress(0);
				sender.sendMessage(ChatColor.GREEN + "Successfully reset progress.");
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
