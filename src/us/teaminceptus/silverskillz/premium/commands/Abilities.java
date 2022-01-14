package us.teaminceptus.silverskillz.premium.commands;

class Abilities implements CommandExecutor {

	protected SilverSkillz plugin;
	
	protected Abilities(SilverSkillz plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player p)) return false;

		SilverPlayer sp = new SilverPlayer(p);
			
		return true;
	}
	
}