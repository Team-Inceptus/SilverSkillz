package us.teaminceptus.silverskillz.commands.ReloadConfigCommand;

public class ReloadConfigCommand implements CommandExecutor {
  protected SilverSkillz plugin;

  public ReloadConfigCommand(SilverSkillz plugin) {
    this.plugin = plugin;
    plugin.getCommand("skreload").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    sender.sendMessage(ChatColor.GREEN + "Reloading...");
    plugin.saveConfig();
  }
}