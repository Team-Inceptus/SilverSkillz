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

public final class RemoveProgressCommand implements CommandExecutor {
	
	protected SilverSkillz plugin;
	
	public RemoveProgressCommand(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getCommand("removeprogress").setExecutor(this);
	}

  private static String errorMsg = SilverSkillz.getMessagesFile().getString("Error");
  private static String errorArguments = SilverSkillz.getMessagesFile().getString("ErrorArguments");
  private static String validPlayer = SilverSkillz.getMessagesFile().getString("InvalidPlayer");
  private static String validAmount = SilverSkillz.getMessagesFile().getString("InvalidAmount");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		
		if (args.length < 1) {
			SilverSkillz.sendPluginMessage(sender, validPlayer);
			return false;
		}
		
		if (Bukkit.getPlayer(args[0]) == null) {
			SilverSkillz.sendPluginMessage(sender, validPlayer);
			return false;
		}
		
		SilverPlayer target = new SilverPlayer(Bukkit.getPlayer(args[0]));
		if (args.length < 2) {
			SilverSkillz.sendPluginMessage(sender, validAmount);
			return false;
		}
		
		try {
			if (!(args.length < 3)) {
				target.getSkill(Skill.matchSkill(args[2])).removeProgress(Double.parseDouble(args[1]));
				sender.sendMessage(ChatColor.GREEN + SilverSkillz.getMessagesFile().getString("SuccessDecrease"));
			} else {
				for (Skill s : Skill.values()) {
					target.getSkill(s).removeProgress(Double.parseDouble(args[1]));
				}
				sender.sendMessage(ChatColor.GREEN + SilverSkillz.getMessagesFile().getString("SuccessDecreaseAll"));
			}
		} catch (IllegalArgumentException e) {
			SilverSkillz.sendPluginMessage(sender, errorArguments);
			return false;
		} catch (NullPointerException e) {
			SilverSkillz.sendPluginMessage(sender, errorArguments);
			return false;
		} catch (Exception e) {
			SilverSkillz.sendPluginMessage(sender, errorMsg + ":\n" + e.getLocalizedMessage());
			return false;
		}
		
		return true;
	}

}
