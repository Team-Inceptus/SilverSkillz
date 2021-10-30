package me.gamercoder215.silverskillz.skills;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Skill {

	COMBAT("combat", new Statistic[] {
		Statistic.DAMAGE_ABSORBED,
		Statistic.DAMAGE_TAKEN,
		Statistic.DAMAGE_RESISTED,
		Statistic.RAID_WIN,
		Statistic.RAID_TRIGGER,
		Statistic.DEATHS,		
	}, getModifier("combat"), Material.DIAMOND_SWORD),
	SOCIAL("social", new Statistic[] {
		Statistic.TRADED_WITH_VILLAGER,
		Statistic.ANIMALS_BRED
	}, getModifier("social"), Material.EMERALD),
	AQUATICS("aquatics", new Statistic[] {
		Statistic.FISH_CAUGHT,
		Statistic.WALK_ON_WATER_ONE_CM,
		Statistic.WALK_UNDER_WATER_ONE_CM,
	}, getModifier("aquatics"), Material.HEART_OF_THE_SEA),
	TRAVELER("traveler", new Statistic[] {
		Statistic.FLY_ONE_CM,
		Statistic.HORSE_ONE_CM,
		Statistic.MINECART_ONE_CM,
		Statistic.CLIMB_ONE_CM,
		Statistic.CROUCH_ONE_CM,
		Statistic.BOAT_ONE_CM,
		Statistic.AVIATE_ONE_CM,
		Statistic.SWIM_ONE_CM,
		Statistic.PIG_ONE_CM,
		Statistic.SPRINT_ONE_CM,
		Statistic.STRIDER_ONE_CM,
		Statistic.WALK_ONE_CM,
		Statistic.WALK_ON_WATER_ONE_CM,
		Statistic.WALK_UNDER_WATER_ONE_CM,
	}, getModifier("traveler"), Material.IRON_BOOTS),
	MINING("mining", new Statistic[] {
		Statistic.MINE_BLOCK
	}, getModifier("mining"), Material.STONE_PICKAXE),
	HUSBANDRY("husbandry", new Statistic[] {
		Statistic.CAULDRON_FILLED,
		Statistic.BELL_RING,
		Statistic.CRAFT_ITEM,
		Statistic.BREAK_ITEM,
		Statistic.FISH_CAUGHT,
		Statistic.FLOWER_POTTED,
		Statistic.RECORD_PLAYED,
		Statistic.TARGET_HIT,
		Statistic.USE_ITEM
	}, getModifier("husbandry"), Material.WHEAT),
	CLEANER("cleaner", new Statistic[]{
		Statistic.ARMOR_CLEANED,
		Statistic.BANNER_CLEANED,
		Statistic.CLEAN_SHULKER_BOX
	}, getModifier("cleaner"), Material.WATER_BUCKET),
	ENCHANTER("enchanter", new Statistic[] {
		Statistic.ITEM_ENCHANTED
	}, getModifier("enchanter"), Material.ENCHANTING_TABLE),
	COLLECTOR("collector", new Statistic[] {
		Statistic.PICKUP,
		Statistic.DROP,
		Statistic.DROP_COUNT
	}, getModifier("collector"), Material.BUNDLE),
	ARCHERY("archery", new Statistic[] {
		Statistic.TARGET_HIT
	}, getModifier("archery"), Material.BOW),
	BUILDER("builder", new Statistic[] {
		Statistic.MINE_BLOCK,
	}, getModifier("builder"), Material.OAK_PLANKS),
	ADVANCER("advancer", null, getModifier("advancer"), Material.GOLDEN_APPLE),
	FARMING("farming", new Statistic[] {
		Statistic.ANIMALS_BRED
	}, getModifier("farming"), Material.GOLDEN_HOE),
	BREWER("brewer", null, getModifier("brewer"), Material.BREWING_STAND),
	SMITHING("smithing", null, getModifier("smithing"), Material.IRON_AXE);

	public static final double MAX_PROGRESS_VALUE = 1000000000;

	private final String name;
	private Statistic[] increases = new Statistic[] {};
	private final Map<Attribute, Double> modifiers;
	private final Material icon;
	private final Inventory inv;

	private Skill(String name, Statistic[] supported, Map<Attribute, Double> modifiers, Material icon) {
		this.name = name;
		this.increases = supported;
		this.modifiers = modifiers;
		this.icon = icon;
		
		this.inv = null;
	}
	
	protected static final Map<Attribute, Double> getModifier(String name) {
		Map<Attribute, Double> modifiers = new HashMap<>();
		
		for (Attribute s : Attribute.values()) {
			modifiers.put(s, 1d);
		}
		
		if (name.equalsIgnoreCase("combat")) {
			modifiers.put(Attribute.GENERIC_ATTACK_DAMAGE, 1.25);
			modifiers.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 1.1);
			
			return modifiers;
		} else if (name.equalsIgnoreCase("")) {
			return modifiers;
		} else return modifiers;
	}

	
	public final Inventory getSkillInventory() {
		return this.inv;
	}
	
	public final Material getIcon() {
		return this.icon;
	}

	public final Statistic[] getSupportedStatistics() {
		return this.increases;
	}

	public final Map<Attribute, Double> getModifiers() {
		return this.modifiers;
	}

	public ItemStack getIconAsStack() {
		ItemStack icon = new ItemStack(this.icon);
		ItemMeta iMeta = icon.getItemMeta();
		iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
		iMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		icon.setItemMeta(iMeta);

		return icon;
	}

	public final String getName() {
		return this.name;
	}
	
	public String toString() {
		return this.name;
	}
	
	public final String getCapitalizedName() {
		return (getName().substring(0, 1).toUpperCase() + getName().substring(1));
	}
	
	// Public Static Methods
	
	
	public static final double matchMinCombatExperience(EntityType t) {
		LivingEntity entity = (LivingEntity) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), 0, 0, 0), t);
		double defaultHP = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		entity.remove();
		
		return (defaultHP / 5);
	}
	
	public static final double matchMinCombatExperience(LivingEntity e) {
		double hp = e.getHealth();
		
		return (hp / 5);
	}
	
	@Nullable
	public static Skill matchSkill(String name) {
		for (Skill s : Skill.values()) {
			if (s.getName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		
		return null;
	}
	
	public static final double toMinimumProgress(byte level) {
		if (level < 0 || level > 100) {
			throw new IllegalArgumentException("Level cannot be less than 0 or bigger than 100");
		}

		if (level <= 10) {
			return (50 + (150 * level));
		} else if (level > 10 && level <= 25) {
			return (1000 + (400 * level));
		} else if (level > 25 && level <= 50) {
			return (90000 + (1000 * level));
		} else if (level > 50 && level <= 80) {
			return (650000 + (5000 * level));
		} else {
			return (3000000 + (20000 * level));
		}
	}

	public static final byte toLevel(double progress) {
		if (progress < 200) return 0;
		else if (progress <= 1550) {
			return ((byte) Math.floor((progress - 50) / 150));
		}
		else if (progress <= 11000) {
			return ((byte) Math.floor((progress - 1000) / 400));
		}
		else if (progress <= 140000) {
			return ((byte) Math.floor((progress - 90000) / 1000));
		}
		else if (progress <= 1050000) {
			return ((byte) Math.floor((progress - 650000) / 5000));
		}
		else {
			return ((byte) Math.floor((progress - 3000000) / 20000));
		}
	}

}