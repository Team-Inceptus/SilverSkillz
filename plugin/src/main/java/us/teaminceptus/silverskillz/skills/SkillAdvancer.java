package us.teaminceptus.silverskillz.skills;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.Skill;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Represents Advancer and Skill Effects for the plugin
 * @author Team Inceptus
 *
 */
public final class SkillAdvancer implements Listener {
	
	protected SilverSkillz plugin;
	
	public SkillAdvancer(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	private static Random r = new Random();
	
	@EventHandler
	public void incrementSkill(PlayerAdvancementDoneEvent e) {
		SilverPlayer p = new SilverPlayer(e.getPlayer());
		
		int amount = r.nextInt(1) + 1;

		awardLevelUp(p, Skill.ADVANCER, p.getSkill(Skill.ADVANCER).addProgress(amount), amount);
	}
	
	@EventHandler
	public void skillEffect(PlayerItemConsumeEvent e) {
		SilverPlayer sp = new SilverPlayer(e.getPlayer());
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
	public void incrementSkill(PlayerMoveEvent e) {
		if (e.getFrom().equals(e.getTo())) return;
		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);
		
		if (r.nextInt(100) < 5) {
			sp.getSkill(Skill.TRAVELER).addProgress(r.nextInt(3) + 1 + r.nextDouble());
		}
	}

	@EventHandler
	public void incrementSkill(VehicleMoveEvent e) {
		if (e.getFrom().equals(e.getTo())) return;
		if (e.getVehicle().getPassengers().size() < 1) return;
		List<Player> players = new ArrayList<>();

		for (Entity en : e.getVehicle().getPassengers()) if (en instanceof Player p) players.add(p);
		if (players.size() < 1) return;

		for (Player p : players) {
			SilverPlayer sp = new SilverPlayer(p);

			if (r.nextInt(100) < 5)
				sp.getSkill(Skill.TRAVELER).addProgress(r.nextInt(4) + 1 + r.nextDouble());
		}
	}
	
	// Cleaner Skill - Unbreaking
	@EventHandler
	public void skillEffect(PlayerItemDamageEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);

		int cleanerPercentage = (int) Math.floor(sp.getSkill(Skill.CLEANER).getLevelDouble() / 10);	

		if (r.nextInt(100) < cleanerPercentage) {
			e.setCancelled(true);
		}
	}
	
	// Map Cache for Enchant Offers
	private Map<UUID, EnchantmentOffer[]> offers = new HashMap<>();
	
	@EventHandler
	public void skillEffect(PrepareItemEnchantEvent e) {
		Player p = e.getEnchanter();
		SilverPlayer sp = new SilverPlayer(p);
		if (e.getOffers().length == 0) return;
		
		int plusLevel = (int) Math.floor(sp.getSkill(Skill.ENCHANTER).getLevelDouble() / 20);
		double costRemove = Math.floor(sp.getSkill(Skill.ENCHANTER).getLevelDouble() / 5) * 0.05;
		
		for (EnchantmentOffer offer : e.getOffers()) {
			if (offer == null) continue;
			int newCost = (int) Math.floor(offer.getCost() * (1 - costRemove));
			
			if (newCost < 1) newCost = 1;
			offer.setCost(newCost);
			offer.setEnchantmentLevel(offer.getEnchantmentLevel() + plusLevel);
			
			offers.put(p.getUniqueId(), e.getOffers());
		}

	}
	
	// Increment Enchanting Skill & Enchanting Effect
	@EventHandler
	public void incrementSkill(EnchantItemEvent e) {
		Player p = e.getEnchanter();
		SilverPlayer sp = new SilverPlayer(p);
		
		// Increment
		sp.getSkill(Skill.ENCHANTER).addProgress(r.nextInt(4) + 1);
		
		// Managing Offers Cache & Offers
		if (offers.containsKey(p.getUniqueId())) {
			EnchantmentOffer offer = offers.get(p.getUniqueId())[e.whichButton()];
			
			e.setExpLevelCost(offer.getCost());
			offers.remove(p.getUniqueId());
			
			new BukkitRunnable() {
				public void run() {
					ItemMeta newMeta = e.getItem().getItemMeta();
					e.getEnchantsToAdd().forEach((enchantment, level) -> newMeta.removeEnchant(enchantment));
					e.getEnchantsToAdd().forEach((enchantment, level) -> newMeta.addEnchant(enchantment, offer.getEnchantmentLevel(), true));
					e.getItem().setItemMeta(newMeta);
				}
			}.runTask(plugin);
			
		}
	}
	
	// Utilities for handling cache
	@EventHandler
	public void util(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player p)) return;
		
		if (e.getInventory() instanceof EnchantingInventory) {
			offers.remove(p.getUniqueId());
		}
	}

	private List<ItemStack> generateItems(double luck) {
		List<ItemStack> items = new ArrayList<>();

		double newLuck = luck < 0 ? Math.abs(luck) / 100 : luck;

		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.DIAMOND, r.nextInt((int)(Math.floor(newLuck) + 1))));
		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.NETHERITE_SCRAP, r.nextInt((int)(Math.floor(newLuck / 1.5) + 1))));
		if (r.nextInt(100) < 5 * newLuck) items.add(new ItemStack(Material.NETHERITE_INGOT, r.nextInt((int)(Math.floor(newLuck / 2) + 1))));
		if (r.nextInt(100) < 20 * newLuck) items.add(new ItemStack(Material.EMERALD, r.nextInt((int)(Math.floor(newLuck * 3) + 1))));
		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.IRON_INGOT, r.nextInt((int)(Math.floor(newLuck * 4.5) + 2))));
		if (r.nextInt(100) < 15 * newLuck) items.add(new ItemStack(Material.GOLDEN_APPLE, r.nextInt((int)(Math.floor(newLuck * 1.5) + 1))));
		if (r.nextInt(1000) < 5 * newLuck) items.add(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, r.nextInt((int)(Math.floor(newLuck / 10) + 1))));
		if (r.nextInt(100) < 1 * newLuck) items.add(new ItemStack(Material.TOTEM_OF_UNDYING));
		if (r.nextInt(100) < 10 * newLuck) items.add(new ItemStack(Material.DIAMOND, r.nextInt((int)(Math.floor(newLuck) + 1))));
		if (r.nextInt(1000) < 1 * newLuck) items.add(new ItemStack(Material.NETHER_STAR, r.nextInt((int)(Math.floor(newLuck / 2.5) + 1))));
		if (r.nextInt(100) < 50 * newLuck) items.add(new ItemStack(Material.COAL, r.nextInt((int)(Math.floor(newLuck * 3) + 3))));
		if (r.nextInt(100) < 15 * newLuck) items.add(new ItemStack(Material.GOLD_INGOT, r.nextInt((int)(Math.floor(newLuck * 1.25) + 1))));

		return items;
	}
	
	@EventHandler
	public void skillEffect(EntityTargetEvent e) {
		if (e.getTarget() == null) return;
		if (!(e.getEntity() instanceof LivingEntity en)) return;
		if (!(en instanceof Monster) && en.getType() != EntityType.SLIME && en.getType() != EntityType.MAGMA_CUBE) return;
		if (!(e.getTarget() instanceof Player p)) return;
		SilverPlayer sp = new SilverPlayer(p);

		int chance = (int) (100 * (Math.floor(sp.getSkill(Skill.SOCIAL).getLevelDouble() / 6) * 0.05));

		if (r.nextInt(100) < chance) {
			e.setTarget(null);
		}
	}

	@EventHandler
	public void skillEffect(PlayerStatisticIncrementEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);

		int increase = (int) Math.floor(sp.getSkill(Skill.COLLECTOR).getLevelDouble() / 20);
		if (increase != 0) { 
			for (int i = 0; i < increase; i++) {
				if (e.getMaterial() != null) {
					p.incrementStatistic(e.getStatistic(), e.getMaterial(), e.getNewValue() - e.getPreviousValue());
				} else if (e.getEntityType() != null) {
					p.incrementStatistic(e.getStatistic(), e.getEntityType(), e.getNewValue() - e.getPreviousValue());
				} else {
					p.incrementStatistic(e.getStatistic(), e.getNewValue() - e.getPreviousValue());
				}
			}
		}
	}

	@EventHandler
	public void skillEffect(LootGenerateEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		if (e.isPlugin()) return;
		SilverPlayer sp = new SilverPlayer(p);

		double luck = 1 + Math.floor((sp.getSkill(Skill.ADVANCER).getLevelDouble() / 5) * 0.05) * sp.getOnlinePlayer().getAttribute(Attribute.GENERIC_LUCK).getValue();

		List<ItemStack> loot = new ArrayList<>(e.getLoot());

		for (ItemStack i : generateItems(luck)) {
			if (loot.size() > e.getInventoryHolder().getInventory().getSize()) break;
			loot.add(i);
		}

		e.setLoot(loot);
	}

	@EventHandler
	public void skillEffect(EntityDamageByEntityEvent e) {
		if (e.getEntity() == null) return;
		if (!(e.getEntity() instanceof LivingEntity en)) return;
		if (!(e.getDamager() instanceof Player p)) return;
		SilverPlayer sp = new SilverPlayer(p);

		switch (e.getCause()) {
			case PROJECTILE -> {
				int damageIncrease = sp.getSkill(Skill.ARCHERY).getLevel() * 5;
				e.setDamage(e.getDamage() + damageIncrease);
			}
			case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> {
				if (en.getCategory() == EntityCategory.UNDEAD) {
					double multiplier = 1 + (Math.floor(sp.getSkill(Skill.CLEANER).getLevelDouble() / 7) * 0.15);
					e.setDamage(e.getDamage() * multiplier);
				}

				if (p.getEquipment().getItemInMainHand().getType().name().endsWith("HOE")) {
					double damageIncrease = Math.floor(sp.getSkill(Skill.FARMING).getLevelDouble() / 3) * 3;
					e.setDamage(e.getDamage() + damageIncrease);
				}
			}
		}
	}

	@EventHandler
	public void incrementSkill(ProjectileHitEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player p)) return;
		if (e.getHitEntity() == null) return;

		SilverPlayer sp = new SilverPlayer(p);
		
		try {
			if (e.getHitEntity() instanceof LivingEntity en) {
				double experience = (p.getLocation().distance(e.getHitEntity().getLocation())) + (Math.floor(Skill.matchMinCombatExperience(en)));
	
				if (r.nextInt(100) < (25 * sp.getSkill(Skill.ARCHERY).getLevel())) {
					sp.getSkill(Skill.ARCHERY).addProgress(experience);
				}
			} else {
				double experience = (p.getLocation().distance(e.getHitEntity().getLocation())) + (Math.floor(Skill.matchMinCombatExperience(e.getHitEntity().getType())));
			
				if (r.nextInt(100) < (25 * sp.getSkill(Skill.ARCHERY).getLevel())) {
					sp.getSkill(Skill.ARCHERY).addProgress(experience);
				}
			}
		} catch (IllegalArgumentException ignored) {
		}
	}

	@EventHandler
	public void skillEffect(FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player p)) return;
		if (e.getItem() != null) return;
		SilverPlayer sp = new SilverPlayer(p);
		
		double chance = Math.floor(sp.getSkill(Skill.TRAVELER).getLevelDouble() / 10) * 10;
		
		if (r.nextInt(100) < chance) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void skillEffect(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() == null) return;
		if (!(e.getEntity().getShooter() instanceof Player p)) return;

		SilverPlayer sp = new SilverPlayer(p);

		double increase = 1 + (Math.floor(sp.getSkill(Skill.ARCHERY).getLevelDouble() / 5) * 0.05);
		Vector newVelocity = e.getEntity().getVelocity().multiply(increase);

		e.getEntity().setVelocity(newVelocity);
	}

	@EventHandler
	public void incrementSkill(FurnaceExtractEvent e) {
		if (e.getPlayer() == null) return;
		if (e.getItemType() == null) return;

		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);

		double xp = e.getExpToDrop() + (r.nextInt(4) * 1.2);

		sp.getSkill(Skill.SMITHING).addProgress(xp);
	}

	@EventHandler
	public void skillEffect(BlockDamageEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);

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
		SilverPlayer sp = new SilverPlayer(p);
		
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
			
			int farmingDuplicator = (int) Math.floor(sp.getSkill(Skill.FARMING).getLevelDouble() / 5);
			
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
					double oreChance = (sp.getSkill(Skill.MINING).getLevelDouble() / 25) * 100;
					
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
		if (!(e.getDamager() instanceof Player p)) return;
		if (((LivingEntity) e.getEntity()).getHealth() - e.getDamage() > 0) return;

		SilverPlayer sp = new SilverPlayer(p);
		
		double add = r.nextInt(5) + Skill.matchMinCombatExperience((LivingEntity) e.getEntity());
		
		awardLevelUp(sp, Skill.COMBAT, sp.getSkill(Skill.COMBAT).addProgress(add), add);
	}

	private static void awardLevelUp(SilverPlayer p, Skill s, boolean hasLeveled, double increaseBy) throws IllegalArgumentException {
		Validate.notNull(p, "Player is null");
		Validate.notNull(s, "Skill is null");

		if (p.getOnlinePlayer() == null) return;
		if (s == Skill.TRAVELER) return;
		if (!(SilverConfig.getConfig().hasNotifications())) return;
		if (!(p.hasNotifications())) return;

		sendActionBar(p.getOnlinePlayer(), String.format(SilverConfig.getConstant("response.actionbar.experience"), df.format(increaseBy), s.getCapitalizedName()));

		if (hasLeveled) {
			Player pl = p.getOnlinePlayer();
			pl.sendTitle(String.format(SilverConfig.getConstant("constants.skill.level"), p.getSkill(s).getLevel() + 1), String.format(SilverConfig.getConstant("response.level_up"), s.getCapitalizedName()),  5, 100, 10);
			pl.playSound(pl.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 3F, 1F);
		}
	}

	private static void sendActionBar(Player p, final String message) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	}

	private static final DecimalFormat df = new DecimalFormat("###.#");

	@EventHandler
	public void incrementSkill(PlayerStatisticIncrementEvent e) {
		SilverPlayer p = new SilverPlayer(e.getPlayer());
		
		if (r.nextInt(100) > 25) return;
		
		for (Skill s : Skill.values()) {
			if (s.getSupportedStatistics() == null) return;
			for (Statistic st : s.getSupportedStatistics()) {
				if (st == e.getStatistic()) {
					List<Double> modifierList = new ArrayList<>();
					for (Attribute a : Attribute.values()) {
						modifierList.add(getModifier(s.getName()).get(a));
					}
					
					double random = s.isBasic() ? 1 : r.nextInt(5);
					
					double increaseBy = random + Collections.max(modifierList);
					
					p.getSkill(s).addProgress(increaseBy);
					break;
				}
			}
		}
	}

	private static Map<Attribute, Double> getModifier(String name) {
		Map<Attribute, Double> modifiers = new HashMap<>();
		modifiers.put(Attribute.GENERIC_LUCK, 1.25);

		for (Attribute s : Attribute.values()) {
			modifiers.put(s, 1d);
		}

		if (name.equalsIgnoreCase("combat")) {
			modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, 1.25);
			modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 1.1);

			return modifiers;
		} else if (name.equalsIgnoreCase("farming") || name.equalsIgnoreCase("mining")) {
			modifiers.put(Attribute.GENERIC_ATTACK_SPEED, 1.2);
			return modifiers;
		} else return modifiers;
	}
	
}
