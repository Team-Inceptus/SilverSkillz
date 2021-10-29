package me.gamercoder215.silverskillz.skills;

protected class SkillUtils implements Listener {

	protected Main plugin;

	protected SkillUtils(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

}