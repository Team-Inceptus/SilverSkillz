package me.gamercoder215.silverskillz.skills;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import me.gamercoder215.silverskillz.SilverPlayer;
import me.gamercoder215.silverskillz.SilverSkillz;

public final class SkillAdvancer implements Listener {
	
	protected SilverSkillz plugin;
	
	public SkillAdvancer(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	static DecimalFormat df = new DecimalFormat("###.#");
	static Random r = new Random();
	
	@EventHandler
	public void incrementSkill(PlayerAdvancementDoneEvent e) {
		SilverPlayer p = SilverPlayer.fromPlayer(e.getPlayer());
		
		Skill.awardLevelUp(p, Skill.ADVANCER, p.getSkill(Skill.ADVANCER).addProgress(), 1);
	}
	
	@EventHandler
	public void skillEffect(PlayerItemConsumeEvent e) {
		SilverPlayer sp = SilverPlayer.fromPlayer(e.getPlayer());
		if (!(e.getItem().getType() == Material.POTION)) return;
		if (sp.getSkill(Skill.BREWER).getLevel() < 1) return;
		
		new BukkitRunnable() {
			public void run() {

				int secsAdded = 0;
				
				for (int i = 0; i < sp.getSkill(Skill.BREWER).getLevel(); i++) secsAdded += 5;
				
				PotionEffect effect = e.getPlayer().getPotionEffect(((PotionMeta) e.getItem().getItemMeta()).getBasePotionData().getType().getEffectType());
				PotionEffect newEffect = new PotionEffect(effect.getType(), effect.getDuration() + (20 * secsAdded), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
				
				e.getPlayer().removePotionEffect(((PotionMeta) e.getItem().getItemMeta()).getBasePotionData().getType().getEffectType());
				e.getPlayer().addPotionEffect(newEffect);
			}
		}.runTask(plugin);
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
			
			int farmingDuplicator = (int) Math.floor(sp.getSkill(Skill.FARMING).getLevel() / 5);
			
			if (farmingDuplicator != 0) {
				for (int i = farmingDuplicator; i > 0; i--) {
					for (ItemStack it : b.getDrops()) {
						b.getWorld().dropItemNaturally(b.getLocation(), it);
					}
				}
			}
		} else if (b.getType() == Material.COAL_ORE || b.getType() == Material.DEEPSLATE_COAL_ORE
				|| b.getType() == Material.IRON_ORE || b.getType() == Material.DEEPSLATE_IRON_ORE
				|| b.getType() == Material.COPPER_ORE || b.getType() == Material.DEEPSLATE_COPPER_ORE
				|| b.getType() == Material.LAPIS_ORE || b.getType() == Material.DEEPSLATE_LAPIS_ORE
				|| b.getType() == Material.GOLD_ORE || b.getType() == Material.DEEPSLATE_GOLD_ORE
				|| b.getType() == Material.REDSTONE_ORE || b.getType() == Material.DEEPSLATE_REDSTONE_ORE
				|| b.getType() == Material.DIAMOND_ORE || b.getType() == Material.DEEPSLATE_DIAMOND_ORE
				|| b.getType() == Material.EMERALD_ORE || b.getType() == Material.DEEPSLATE_EMERALD_ORE
				|| b.getType() == Material.NETHER_QUARTZ_ORE || b.getType() == Material.NETHER_GOLD_ORE) {
				
				if (sp.getSkill(Skill.MINING).getLevel() % 5 == 0) {
					double oreChance = (sp.getSkill(Skill.MINING).getLevel() / 25) * 100;
					
					if (r.nextInt(100) < oreChance && oreChance <= 100) {
						for (ItemStack it : b.getDrops()) {
							b.getWorld().dropItemNaturally(b.getLocation(), it);
						}
					} else if ((r.nextInt(100) + 100) < oreChance && oreChance > 100) {
						for (ItemStack it : b.getDrops()) {
							b.getWorld().dropItemNaturally(b.getLocation(), it);
							b.getWorld().dropItemNaturally(b.getLocation(), it);
						}
					}
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
		
		Skill.awardLevelUp(sp, Skill.COMBAT, sp.getSkill(Skill.COMBAT).addProgress(add), add);
	}
	
	@EventHandler
	public void incrementSkill(PlayerStatisticIncrementEvent e) {
		SilverPlayer p = SilverPlayer.fromPlayer(e.getPlayer());
		
		if (r.nextInt(100) > 25) return;
		
		for (Skill s : Skill.values()) {
			for (Statistic st : s.getSupportedStatistics()) {
				if (st == e.getStatistic()) {
					List<Double> modifierList = new ArrayList<>();
					for (Attribute a : Attribute.values()) {
						modifierList.add(Skill.getModifier(s.getName()).get(a));
					}
					
					double random = s.isBasic() ? 1 : r.nextInt(5);
					
					double increaseBy = random + Collections.max(modifierList);
					
					p.getSkill(s).addProgress(increaseBy);
					break;
				}
			}
		}
	}
	
}
