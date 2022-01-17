package us.teaminceptus.silverskillz.premium.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import us.teaminceptus.silverskillz.SilverPlayer;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.premium.skills.Ability;
import us.teaminceptus.silverskillz.premium.skills.AbilityType;

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
			
			if (a.getType() != AbilityType.ACTION && a.getType() != AbilityType.ACTION_POTION_COOLDOWN) continue;
			if (!(a.isUnlocked(sp))) return;
			if (a.getAction() != e.getAction()) continue;
			
			
			
		}
	}
	
}