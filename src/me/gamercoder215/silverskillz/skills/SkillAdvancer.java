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
		
		int amount = r.nextInt(1) + 1;

		Skill.awardLevelUp(p, Skill.ADVANCER, p.getSkill(Skill.ADVANCER).addProgress(amount), amount);
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
	public void skillEffect(PlayerItemDamageEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);

		int cleanerPercentage = (int) Math.floor(sp.getSkill(Skill.CLEANER).getLevel() / 10);	

		if (r.nextInt(100) < cleanerPercentage) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void skillEffect(PrepareItemEnchantEvent e) {
		Player p = e.getEnchanter();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);
		if (e.getOffers().length == 0) return;

		int plusLevel = (int) Math.floor(sp.getSkill(Skill.ENCHANTER) / 20);
		double costRemove = Math.floor(sp.getSkill(Skill.ENCHANTER) / 5) * 0.05;

		for (EnchantmentOffer offer : e.getOffers()) {
			int newCost = (int) Math.floor(offer.getCost() * (1 - costRemove));
			offer.setCost(newCost);

			offer.setEnchantmentLevel(offer.getEnchantmentLevel() + plusLevel);
		}

		return;
	}

	private List<ItemStack> generateItems(double luck) {
		List<ItemStack> items = new ArrayList<>();

		double newLuck = luck < 0 ? Math.abs(luck) / 100 : luck;

		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.DIAMOND, r.nextInt((int)(Math.floor(newLuck) + 1))));
		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.NETHERITE_SCRAP, r.nextInt((int)(Math.floor(newLuck / 1.5) + 1)));
		if (r.nextInt(100) < 5 * newLuck) items.add(new ItemStack(Material.NETHERITE_INGOT, r.nextInt((int)(Math.floor(newLuck / 2) + 1))));
		if (r.nextInt(100) < 20 * newLuck) items.add(new ItemStack(Material.EMERALD, r.nextInt((int)(Math.floor(newLuck * 3) + 1))));
		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.IRON_INGOT, r.nextInt((int)(Math.floor(newLuck * 4.5) + 2))));
		if (r.nextInt(100) < 15 * newLuck) items.add(new ItemStack(Material.GOLDEN_APPLE, r.nextInt((int)(Math.floor(newLuck * 1.5) + 1))));
		if (r.nextInt(1000) < 5 * newLuck) items.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, r.nextInt((int)(Math.floor(newLuck / 10) + 1))));
		if (r.nextInt(100) < 1 * newLuck) items.add(new ItemStack(Material.TOTEM_OF_UNDYING)));
		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.DIAMOND, r.nextInt((int)(Math.floor(newLuck) + 1))));
		if (r.nextInt(1000) < 1 * newLuck) items.add(new ItemStack(Material.NETHER_STAR, r.nextInt((int)(Math.floor(newLuck / 2.5) + 1))));
		if (r.nextInt(100) < 50 * newLuck) items.add(new ItemStack(Material.COAL, r.nextInt((int)(Math.floor(newLuck * 3) + 3))));
		if (r.nextInt(100) < 15 * newLuck) items.add(new ItemStack(Material.GOLD_INGOT, r.nextInt((int)(Math.floor(newLuck * 1.25) + 1))));

		return items;
	}
	
	@EventHandler
	public void skillEffect(LootGenerateEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		if (e.isPlugin()) return;
		Player p = (Player) e.getEntity();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);

		double luck = (1 + (Math.floor(sp.getSkill(Skill.ADVANCER).getLevel() / 5) * 0.05)) * sp.getOnlinePlayer().getAttribute(Attribute.GENERIC_LUCK).getValue();

		List<ItemStack> loot = new ArrayList<>();
		loot.addAll(e.getLoot());

		for (ItemStack i : generateItems(luck)) {
			if (loot.length() > e.getInventoryHolder().getInventory().getSize()) break;
			loot.add(i);
		}

		e.setLoot(loot);
		return;
	}

	@EventHandler
	public void incrementSkill(ProjectileHitEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) return;
		if (e.getHitEntity() == null) return;

		Player p = (Player) e.getEntity().getShooter();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);
		
		try {
			int experience = (p.getLocation().distance(e.getHitEntity().getLocation())) + (Math.floor(Skill.matchMinCombatExperience((e.getHitEntity() instanceof LivingEntity ? (LivingEntity) e.getHitEntity() : e.getHitEntity().getType()))));

			if (r.nextInt(100) < (25 * sp.getSkill(Skill.ARCHERY).getLevel())) {
				sp.getSkill(Skill.ARCHERY).addProgress(experience);
			}
			return;
		} catch (IllegalArgumentException e) {
			return;
		}
	}

	@EventHandler
	public void skillEffect(FoodLevelChangeEvent e) {
		
	}

	@EventHandler
	public void skillEffect(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() == null) return;
		if (!(e.getEntity().getShooter() instanceof Player)) return;

		Player p = (Player) e.getEntity().getShooter();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);

		double increase = 1 + (Math.floor(sp.getSkill(Skill.ARCHERY).getLevel() / 5) * 0.05);
		Vector newVelocity = e.getEntity().getVelocity().multiply(increase);

		e.getEntity().setVelocity(newVelocity);
		return;		
 	}

	@EventHandler
	public void incrementSkill(FurnaceExtractEvent e) {
		if (e.getPlayer() == null) return;
		if (e.getItemType() == null) return;

		Player p = e.getPlayer();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);

		double xp = e.getExpToDrop() + (r.nextInt(4) * 1.2);

		sp.getSkill(Skill.SMITHING).addProgress(xp);
	}

	@EventHandler
	public void skillEffect(BlockDamageEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);

		Material m = e.getBlock().getType();

		if (sp.getSkill(Skill.SMITHING).getLevel() >= 30) {
			if (Tag.LOGS.isTagged(m) || Tag.PLANKS.isTagged(m)) {
				e.setInstaBreak(true);
			}
		}
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
