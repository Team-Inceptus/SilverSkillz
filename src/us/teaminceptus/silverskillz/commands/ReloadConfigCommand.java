package us.teaminceptus.silverskillz.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import us.teaminceptus.silverskillz.SilverSkillz;

public class ReloadConfigCommand implements CommandExecutor {
  protected SilverSkillz plugin;

  public ReloadConfigCommand(SilverSkillz plugin) {
    this.plugin = plugin;
    plugin.getCommand("skreload").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	sender.sendMessage(ChatColor.GREEN + "Reloading...");
	plugin.reloadConfig();
	SilverSkillz.reloadMessagesFile();
	plugin.saveConfig();
	sender.sendMessage(ChatColor.GREEN + "Reloaded! This will update all files related to the SilverSkillz plugin, except for player files. The JAR is not updated; you may need to restart or reload your server to update the JAR.");
	
	return true;
  }
}