package us.teaminceptus.silverskillz.premium.events;

public class AbilityEvents implements Listener {

	protected SilverSkillz plugin;

	public AbilityEvents(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);
		
		for (Ability a : Ability.values()) {
			if (a.getAction() == null) continue;
			if (!(a.isUnlocked(sp))) continue;
			if (e.getAction() != a.getAction()) continue;
			
		}
	}
	
}