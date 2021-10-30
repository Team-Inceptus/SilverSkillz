package me.gamercoder215.silverskillz.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import me.gamercoder215.silverskillz.SilverPlayer;
import me.gamercoder215.silverskillz.SilverSkillz;

public class SkillAdvancer implements Listener {
	
	protected SilverSkillz plugin;
	
	public SkillAdvancer(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	static DecimalFormat df = new DecimalFormat("###.#");
	static Random r = new Random();
	
	protected static void awardLevelUp(SilverPlayer p, Skill s, boolean hasLeveled, double increaseBy) {
		if (p.getOnlinePlayer() == null) return;
		
		
		SkillUtils.sendActionBar(p.getOnlinePlayer(), ChatColor.GREEN + "+" + df.format(increaseBy) + " " + s.getCapitalizedName() + " Experience");
		
		if (hasLeveled) {
			Player pl = p.getOnlinePlayer();
			
			pl.sendTitle(ChatColor.GOLD + s.getCapitalizedName() + " Has leveled Up!", ChatColor.GREEN + s.getCapitalizedName() + " Level " + Byte.toString(p.getSkill(s).getLevel()), 5, 100, 10);
		}
	}
	
	@EventHandler
	public void incrementSkill(PlayerAdvancementDoneEvent e) {
		SilverPlayer p = SilverPlayer.fromPlayer(e.getPlayer());
		
		awardLevelUp(p, Skill.ADVANCER, p.getSkill(Skill.ADVANCER).addProgress(), 1);
	}
	
	@EventHandler
	public void incrementSkill(BlockBreakEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);
		
		Block b = e.getBlock();
		
		if (b.getBlockData() instanceof Ageable || b.getType() == Material.PUMPKIN || b.getType() == Material.MELON) {
			if (b.getType() == Material.FROSTED_ICE) return;
			
			Ageable ab = (Ageable) b.getBlockData();
			if (ab.getAge() != ab.getMaximumAge()) return;
			
			double breedModifier = p.getStatistic(Statistic.ANIMALS_BRED) * 1.1;
			
			sp.getSkill(Skill.FARMING).addProgress(1 + (breedModifier * 1.3));
			
			if (b.getType() == Material.NETHER_WART) {
				sp.getSkill(Skill.BREWER).addProgress(4.2);
			}
		}
	}
	
	

	
	@EventHandler
	public void incrementSkill(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) return;
		if (((LivingEntity) e.getEntity()).getHealth() - e.getDamage() > 0) return;
		
		Player p = (Player) e.getDamager();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);
		
		double add = r.nextInt(5) + Skill.matchMinCombatExperience((LivingEntity) e.getEntity());
		
		awardLevelUp(sp, Skill.COMBAT, sp.getSkill(Skill.COMBAT).addProgress(add), add);
	}
	
	@EventHandler
	public void incrementSkill(PlayerStatisticIncrementEvent e) {
		SilverPlayer p = SilverPlayer.fromPlayer(e.getPlayer());
		
		for (Skill s : Skill.values()) {
			for (Statistic st : s.getSupportedStatistics()) {
				if (st == e.getStatistic()) {
					List<Double> modifierList = new ArrayList<>();
					for (Attribute a : Attribute.values()) {
						modifierList.add(Skill.getModifier(s.getName()).get(a));
					}
					
					double increaseBy = Collections.max(modifierList);
					
					p.getSkill(s).addProgress(increaseBy);
					break;
				}
			}
		}
	}
	
}
