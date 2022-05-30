package us.teaminceptus.silverskillz.api.skills;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.artifact.Artifact;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;

/**
 * Enum containing skills for the plugin
 * @author GamerCoder215
 *
 */
public enum Skill {
	/**
	 * Skill related to combat and fighting
	 */
	COMBAT("combat", new Statistic[] {
		Statistic.DAMAGE_ABSORBED,
		Statistic.DAMAGE_TAKEN,
		Statistic.DAMAGE_RESISTED,
		Statistic.RAID_WIN,
		Statistic.RAID_TRIGGER,
		Statistic.DEATHS,		
	}, getModifier("combat"), Material.DIAMOND_SWORD),
	/**
	 * Skill related to social interactions
	 */
	SOCIAL("social", new Statistic[] {
		Statistic.TRADED_WITH_VILLAGER,
		Statistic.ANIMALS_BRED
	}, getModifier("social"), Material.EMERALD),
	/**
	 * Skill related to water activities
	 */
	AQUATICS("aquatics", new Statistic[] {
		Statistic.FISH_CAUGHT,
		Statistic.WALK_ON_WATER_ONE_CM,
		Statistic.WALK_UNDER_WATER_ONE_CM,
	}, getModifier("aquatics"), Material.HEART_OF_THE_SEA),
	/**
	 * Skill related to large-scale player movement
	 */
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
	/**
	 * Skill related to the breaking of blocks
	 */
	MINING("mining", new Statistic[] {
		Statistic.MINE_BLOCK
	}, getModifier("mining"), Material.STONE_PICKAXE),
	/**
	 * Skill related to miscellaneous activities
	 */
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
	/**
	 * Skill related to housekeeping and tidiness
	 */
	CLEANER("cleaner", new Statistic[]{
		Statistic.ARMOR_CLEANED,
		Statistic.BANNER_CLEANED,
		Statistic.CLEAN_SHULKER_BOX
	}, getModifier("cleaner"), Material.WATER_BUCKET),
	/**
	 * Skill related to enchanting various items
	 */
	ENCHANTER("enchanter", new Statistic[] {
		Statistic.ITEM_ENCHANTED
	}, getModifier("enchanter"), Material.ENCHANTING_TABLE),
	/**
	 * Skill related to picking up and collecting various items
	 */
	COLLECTOR("collector", new Statistic[] {
		Statistic.PICKUP
	}, getModifier("collector"), Material.BUNDLE),
	/**
	 * Skill related to ranged attacks
	 */
	ARCHERY("archery", new Statistic[] {
		Statistic.TARGET_HIT
	}, getModifier("archery"), Material.BOW),
	/**
	 * Skill related to the placing and removing of blocks
	 */
	BUILDER("builder", new Statistic[] {
		Statistic.MINE_BLOCK,
	}, getModifier("builder"), Material.OAK_PLANKS),
	/**
	 * Skill related to completing advancements
	 */
	ADVANCER("advancer", null, getModifier("advancer"), Material.GOLDEN_APPLE),
	/**
	 * Skill related to farming and its activities
	 */
	FARMING("farming", new Statistic[] {
		Statistic.ANIMALS_BRED
	}, getModifier("farming"), Material.GOLDEN_HOE),
	/**
	 * Skill related to brewery
	 */
	BREWER("brewer", null, getModifier("brewer"), Material.BREWING_STAND),
	/**
	 * Skill related to the using of various furnaces and creating weapons through them
	 */
	SMITHING("smithing", null, getModifier("smithing"), Material.IRON_AXE);

	/**
	 * Max amount of Progress for a Player
	 */
	public static final double MAX_PROGRESS_VALUE = 1000000000;

	private final String name;
	private final Statistic[] increases;
	private final Map<Attribute, Double> modifiers;
	private final Material icon;
	
	Skill(String name, Statistic[] supported, Map<Attribute, Double> modifiers, Material icon) {
		this.name = name;
		this.increases = supported;
		this.modifiers = modifiers;
		this.icon = icon;
	}

    /**
     * Fetches the name used internally, getName() can change with language set
	 * @return Internal Name
     */
    public final String getDefaultName() {
	    return this.name;
    }

	/**
	 * Fetches the capitalized version of {@link #getDefaultName()}.
	 * @return Capitalized Default Name
	 */
	public final String getCapitalizedDefaultName() {
	    StringBuilder s = new StringBuilder();
		String[] arr = getDefaultName().split("\\s");
		for (int i = 0; i < arr.length; i++) {
			String piece = arr[i];
			if (i == arr.length - 1) s.append(WordUtils.capitalizeFully(piece));
			else s.append(WordUtils.capitalizeFully(piece)).append("\\s");
		}
	    return s.toString();
    }
	
	/**
	 * Fetches the central menu for the /skill command
	 * @return Inventory containing menu
	 */
	public static Inventory getMenu() {
		Inventory menu = SkillUtils.generateGUI(54, SilverConfig.getConstant("title.skills"));
		
		menu.setItem(19, COMBAT.getIconAsStack());
		menu.setItem(21, ARCHERY.getIconAsStack());
		menu.setItem(22, BUILDER.getIconAsStack());
		menu.setItem(23, BREWER.getIconAsStack());
		menu.setItem(25, FARMING.getIconAsStack());
		menu.setItem(31, SOCIAL.getIconAsStack());
		menu.setItem(28, TRAVELER.getIconAsStack());
		menu.setItem(29, AQUATICS.getIconAsStack());
		menu.setItem(30, MINING.getIconAsStack());
		menu.setItem(32, ENCHANTER.getIconAsStack());
		menu.setItem(33, SMITHING.getIconAsStack());
		menu.setItem(34, ADVANCER.getIconAsStack());
		menu.setItem(39, COLLECTOR.getIconAsStack());
		menu.setItem(40, HUSBANDRY.getIconAsStack());
		menu.setItem(41, CLEANER.getIconAsStack());
		
		return menu;
	}
	

	/**
	 * Whether or not this skill is basic
	 * @return true if basic, else false
	 */
	public final boolean isBasic() {
		return switch (this) {
			case ADVANCER -> true;
			default -> false;
		};
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
	
	/**
	 * Returns icon as material
	 * @return Icon as Material
	 */
	public final Material getIcon() {
		return this.icon;
	}
	
	/**
	 * Statistics that will increase the progress of this skill
	 * @return Supported Statistics of skill
	 */
	public final Statistic[] getSupportedStatistics() {
		return this.increases;
	}
	
	/**
	 * Map of modifiers that will increase skill growth
	 * @return Map of modifiers by Attribute
	 */
	public final Map<Attribute, Double> getModifiers() {
		return this.modifiers;
	}
	
	/**
	 * Get skill icon used in its inventory as an ItemStack
	 * @return ItemStack of icon
	 */
	public ItemStack getIconAsStack() {
		ItemStack icon = new ItemStack(this.icon);
		ItemMeta iMeta = icon.getItemMeta();
		iMeta.setDisplayName(AQUA + this.getCapitalizedName());
		iMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);
		iMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
		icon.setItemMeta(iMeta);

		return icon;
	}
	
	/**
	 * Gets the name of the skill used in messages.yml
	 * @return Skill name
	 */
	public final String getName() {
		return SilverConfig.getConstant("skills." + getDefaultName().toLowerCase());
	}
	
	/**
	 * Duplicate of getName()
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Capitalized Name
	 * @return String containing capitalized name
	 */
	public final String getCapitalizedName() {
		StringBuilder s = new StringBuilder();
		String[] arr = getName().split("\\s");
		for (int i = 0; i < arr.length; i++) {
			String piece = arr[i];
			if (i == arr.length - 1) s.append(WordUtils.capitalizeFully(piece));
			else s.append(WordUtils.capitalizeFully(piece)).append("\\s");
		}
		return s.toString();
	}
	
	// Public Static Methods
	
	/**
	 * Match the minimum combat experience for this entity type
	 * @param t Entity Type to use
	 * @return A double containing the minimum amount of combat experience a player will receive when killed
	 * @throws IllegalArgumentException if EntityType is not valid
	 */
	public static double matchMinCombatExperience(EntityType t) throws IllegalArgumentException {
		Entity entity = Bukkit.getWorld("world").spawn(new Location(Bukkit.getWorld("world"), 0, 0, 0), t.getEntityClass());
		if (!(entity instanceof LivingEntity len)) throw new IllegalArgumentException("Invalid EntityType");
		double defaultHP = len.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		entity.remove();
		return defaultHP / 5;
	}
	
	/**
	 * Match the minimum combat experience for this living    entity
	 * @param e Entity to match off of
	 * @return A double containing the minimum amount of combat experience a player will receive when killed
	 */
	public static double matchMinCombatExperience(LivingEntity e) {
		double hp = e.getHealth();
		return (hp / 5);
	}
	
	/**
	 * Matches a Skill by its messages name (from language) or default name.
	 * @param name Skill name
	 * @return Skill found, or null if not found
	 */
	public static Skill matchSkill(String name) {
		if (name == null) return null;
		for (Skill s : values()) if (s.getName().equalsIgnoreCase(name) || s.getDefaultName().equalsIgnoreCase(name)) return s;
		return null;
	}

	/**
	 * Matches a Skill by its Icon.
	 * @param m Icon to match
	 * @return Matched skill, or null if not found
	 */
	public static Skill matchSkill(Material m) {
		if (m == null) return null;
		for (Skill s : values()) if (s.getIcon() == m) return s;
		return null;
	}
	
	/**
	 * A static method to convert levels to its minimum required progress
	 * @param level The level to convert
	 * @param basic Whether or not to use basic conversion
	 * @return double containing progress
	 */
	public static double toMinimumProgress(boolean basic, int level) {
		if (level < 0 || level > 100) {
			throw new IllegalArgumentException("Level cannot be less than 0 or bigger than 100");
		}
		
		if (!(basic)) {
			return (150 * (Math.pow(level, 2.4)));
		} else {
			return (level * 2);
		}
	}
	
	/**
	 * A static method to convert progress to its level
	 * @param progress The progress amount a player has
	 * @param basic Whether or not to use basic conversion
	 * @return int containing its level
	 */
	public static int toLevel(boolean basic, double progress) {
		if (!(basic)) {
			if (progress < 791.7) return 0;
			return (int) (Math.floor(Math.pow((progress / 150), (1 / 2.4))));
		} else {
			return (int) Math.floor(progress / 2);
		}
	}

}