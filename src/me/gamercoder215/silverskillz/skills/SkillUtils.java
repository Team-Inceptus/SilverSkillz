package me.gamercoder215.silverskillz.skills;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.gamercoder215.silverskillz.SilverPlayer;
import me.gamercoder215.silverskillz.SilverSkillz;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SkillUtils implements Listener {
	
	protected SilverSkillz plugin;
	
	public SkillUtils(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public final static void sendActionBar(Player p, final String message) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}
	
	@EventHandler
	public void damageCalculation(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) return;
		
		Player p = (Player) e.getDamager();
		
		byte level = SilverPlayer.fromPlayer(p).getSkill(Skill.COMBAT).getLevel();
		
		e.setDamage(e.getDamage() + (Math.pow(level, 1.7)) + level * 3);
	}
}
