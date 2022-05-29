package us.teaminceptus.silverskillz.api.skills;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    * Default name used internally, getName() can change with messages.yml
    */
    public final String getDefaultName() {
	    return this.name;
    }
    
    public final String getCapitalizedDefaultName() {
	    StringBuilder s = new StringBuilder();
	    for (String piece : getDefaultName().split("\\s")) {
		    s.append(WordUtils.capitalizeFully(piece)).append("\\s");
	    }
	    return s.toString();
    }
	
	/**
	 * The central menu for the /skill command
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
	 * Generate inventory pages for the skill
	 * @param p The player to use.
	 * @return A Map listing the inventories by page (starting at 0)
	 */
	public final Map<Integer, Inventory> generateInventories(SilverPlayer p) {
		Map<Integer, Inventory> pages = new HashMap<>();
		
		Map<Integer, ItemStack> panels = getInventoryIcons(this, p);
		
		ItemStack headInfo = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) headInfo.getItemMeta();
		headMeta.setOwningPlayer(p.getPlayer());
		headMeta.setDisplayName(String.format(SilverConfig.getConstant("constants.gui.stats"), p.getName()));

		List<String> info = new ArrayList<>();
		info.add(String.format(SilverConfig.getConstant("constants.skill.progress"), SkillUtils.withSuffix(p.getSkill(this).getProgress())));
		info.add(String.format(SilverConfig.getConstant("constants.skill.level"), p.getSkill(this).getLevel()));
		headMeta.setLore(info);
		headInfo.setItemMeta(headMeta);
		
		ItemStack arrowForward = new ItemStack(Material.ARROW);
		ItemMeta forwardMeta = arrowForward.getItemMeta();
		forwardMeta.setDisplayName(SilverConfig.getConstant("constants.gui.next_page"));
		arrowForward.setItemMeta(forwardMeta);
		
		ItemStack arrowBack = new ItemStack(Material.ARROW);
		ItemMeta backMeta = arrowForward.getItemMeta();
		backMeta.setDisplayName(SilverConfig.getConstant("constants.gui.previous_page"));
		arrowBack.setItemMeta(backMeta);
		
		ItemStack menuBack = new ItemStack(Material.BEACON);
		ItemMeta menuMeta = menuBack.getItemMeta();
		menuMeta.setDisplayName(SilverConfig.getConstant("constants.gui.back"));
		menuBack.setItemMeta(menuMeta);
		// First Page
		Inventory firstPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill");
		
		firstPage.setItem(9, getIconAsStack());
		firstPage.setItem(4, headInfo);
		firstPage.setItem(49, menuBack);
		firstPage.setItem(50, arrowForward);
		
		firstPage.setItem(10, panels.get(0));
		firstPage.setItem(19, panels.get(1));
		firstPage.setItem(28, panels.get(2));
		firstPage.setItem(37, panels.get(3));
		firstPage.setItem(38, panels.get(4));
		firstPage.setItem(39, panels.get(5));
		firstPage.setItem(30, panels.get(6));
		firstPage.setItem(21, panels.get(7));
		firstPage.setItem(12, panels.get(8));
		firstPage.setItem(13, panels.get(9));
		firstPage.setItem(14, panels.get(10));
		firstPage.setItem(23, panels.get(11));
		firstPage.setItem(32, panels.get(12));
		firstPage.setItem(41, panels.get(13));
		firstPage.setItem(42, panels.get(14));
		firstPage.setItem(43, panels.get(15));
		firstPage.setItem(34, panels.get(16));
		firstPage.setItem(25, panels.get(17));
		firstPage.setItem(16, panels.get(18));
		firstPage.setItem(17, panels.get(19));
		
		pages.put(0, firstPage);
		// Second Page
		Inventory secondPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 2");
		
		secondPage.setItem(9, getIconAsStack());
		secondPage.setItem(4, headInfo);
		secondPage.setItem(50, arrowForward);
		secondPage.setItem(48, arrowBack);
		
		secondPage.setItem(10, panels.get(20));
		secondPage.setItem(19, panels.get(21));
		secondPage.setItem(28, panels.get(22));
		secondPage.setItem(37, panels.get(23));
		secondPage.setItem(38, panels.get(24));
		secondPage.setItem(39, panels.get(25));
		secondPage.setItem(30, panels.get(26));
		secondPage.setItem(21, panels.get(27));
		secondPage.setItem(12, panels.get(28));
		secondPage.setItem(13, panels.get(29));
		secondPage.setItem(14, panels.get(30));
		secondPage.setItem(23, panels.get(31));
		secondPage.setItem(32, panels.get(32));
		secondPage.setItem(41, panels.get(33));
		secondPage.setItem(42, panels.get(34));
		secondPage.setItem(43, panels.get(35));
		secondPage.setItem(34, panels.get(36));
		secondPage.setItem(25, panels.get(37));
		secondPage.setItem(16, panels.get(38));
		secondPage.setItem(17, panels.get(39));
		
		pages.put(1, secondPage);
		// Third Page
		Inventory thirdPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 3");
		
		thirdPage.setItem(9, getIconAsStack());
		thirdPage.setItem(4, headInfo);
		thirdPage.setItem(50, arrowForward);
		thirdPage.setItem(48, arrowBack);
		
		thirdPage.setItem(10, panels.get(40));
		thirdPage.setItem(19, panels.get(41));
		thirdPage.setItem(28, panels.get(42));
		thirdPage.setItem(37, panels.get(43));
		thirdPage.setItem(38, panels.get(44));
		thirdPage.setItem(39, panels.get(45));
		thirdPage.setItem(30, panels.get(46));
		thirdPage.setItem(21, panels.get(47));
		thirdPage.setItem(12, panels.get(48));
		thirdPage.setItem(13, panels.get(49));
		thirdPage.setItem(14, panels.get(50));
		thirdPage.setItem(23, panels.get(51));
		thirdPage.setItem(32, panels.get(52));
		thirdPage.setItem(41, panels.get(53));
		thirdPage.setItem(42, panels.get(54));
		thirdPage.setItem(43, panels.get(55));
		thirdPage.setItem(34, panels.get(56));
		thirdPage.setItem(25, panels.get(57));
		thirdPage.setItem(16, panels.get(58));
		thirdPage.setItem(17, panels.get(59));
		
		pages.put(2, thirdPage);
		
		// Fourth Page
		Inventory fourthPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 4");
		
		fourthPage.setItem(9, getIconAsStack());
		fourthPage.setItem(4, headInfo);
		fourthPage.setItem(50, arrowForward);
		fourthPage.setItem(48, arrowBack);
		
		fourthPage.setItem(10, panels.get(60));
		fourthPage.setItem(19, panels.get(61));
		fourthPage.setItem(28, panels.get(62));
		fourthPage.setItem(37, panels.get(63));
		fourthPage.setItem(38, panels.get(64));
		fourthPage.setItem(39, panels.get(65));
		fourthPage.setItem(30, panels.get(66));
		fourthPage.setItem(21, panels.get(67));
		fourthPage.setItem(12, panels.get(68));
		fourthPage.setItem(13, panels.get(69));
		fourthPage.setItem(14, panels.get(70));
		fourthPage.setItem(23, panels.get(71));
		fourthPage.setItem(32, panels.get(72));
		fourthPage.setItem(41, panels.get(73));
		fourthPage.setItem(42, panels.get(74));
		fourthPage.setItem(43, panels.get(75));
		fourthPage.setItem(34, panels.get(76));
		fourthPage.setItem(25, panels.get(77));
		fourthPage.setItem(16, panels.get(78));
		fourthPage.setItem(17, panels.get(79));
		
		pages.put(3, fourthPage);
		// Fifth Page
		Inventory fifthPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 5");
		
		fifthPage.setItem(9, getIconAsStack());
		fifthPage.setItem(4, headInfo);
		fifthPage.setItem(48, arrowBack);
		
		fifthPage.setItem(10, panels.get(80));
		fifthPage.setItem(19, panels.get(81));
		fifthPage.setItem(28, panels.get(82));
		fifthPage.setItem(37, panels.get(83));
		fifthPage.setItem(38, panels.get(84));
		fifthPage.setItem(39, panels.get(85));
		fifthPage.setItem(30, panels.get(86));
		fifthPage.setItem(21, panels.get(87));
		fifthPage.setItem(12, panels.get(88));
		fifthPage.setItem(13, panels.get(89));
		fifthPage.setItem(14, panels.get(90));
		fifthPage.setItem(23, panels.get(91));
		fifthPage.setItem(32, panels.get(92));
		fifthPage.setItem(41, panels.get(93));
		fifthPage.setItem(42, panels.get(94));
		fifthPage.setItem(43, panels.get(95));
		fifthPage.setItem(34, panels.get(96));
		fifthPage.setItem(25, panels.get(97));
		fifthPage.setItem(16, panels.get(98));
		fifthPage.setItem(17, panels.get(99));
		
		pages.put(4, fifthPage);

		return pages;
	}
	
	private static final DecimalFormat df = new DecimalFormat("###.#");
	
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
	
	protected static Map<Integer, ItemStack> getInventoryIcons(Skill s, SilverPlayer p) {
		df.setRoundingMode(RoundingMode.FLOOR);
		Map<Integer, ItemStack> icons = new HashMap<>();
		
		for (int i = 1; i <= 100; i++) {
			int nextLevel = i + 1;
			boolean comp = p.getSkill(s).getProgress() >= Skill.toMinimumProgress(s.isBasic(), i);
			Material m = comp ? Material.LIME_STAINED_GLASS_PANE : Material.YELLOW_STAINED_GLASS_PANE;
			ChatColor nameCC = comp ? ChatColor.GREEN : ChatColor.YELLOW;
			String name = String.format(SilverConfig.getConstant("skills.gui.panel_name"), nameCC, i, nameCC, comp ? SilverConfig.getConstant("constants.complete") : SilverConfig.getConstant("constants.incomplete"));
			
			List<String> completedLore = new ArrayList<>();
			List<String> incompleteLore = new ArrayList<>();
			
			if (i != 100) incompleteLore.add(ChatColor.YELLOW + SkillUtils.withSuffix(Double.parseDouble(df.format(p.getSkill(s).getProgress()))) + " / " + SkillUtils.withSuffix(Double.parseDouble(df.format(toMinimumProgress(s.isBasic(), nextLevel)))));
			else incompleteLore.add(ChatColor.YELLOW + SkillUtils.withSuffix(Double.parseDouble(df.format(p.getSkill(s).getProgress()))) + " / " + SkillUtils.withSuffix(Double.parseDouble(df.format(toMinimumProgress(s.isBasic(), 100)))));

			for (Ability a : Ability.values()) {
				if (a.getSkill() == s && i == a.getLevelUnlocked()) {
					completedLore.add(String.format(SilverConfig.getConstant("skills.gui.ability"), ChatColor.GREEN, a.getName()));
					incompleteLore.add(" ");
					incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.ability"), ChatColor.RED, a.getName()));
				}
			}

			switch (s) {
				case COMBAT -> {
					completedLore.add(String.format(SilverConfig.getConstant("skills.gui.combat.buff"), ChatColor.BLUE, i, df.format((Math.pow(i, 1.9)) + i * 3.7)));
					incompleteLore.add(" ");
					incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.combat.buff"), ChatColor.AQUA, i, df.format((Math.pow(i, 1.9)) + i * 3.7)));
				}
				case FARMING -> {
					if (i % 20 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.farming.drops"), ChatColor.GOLD));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.farming.drops"), ChatColor.YELLOW));
					}
				}
				case MINING -> {
					if (i % 5 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.mining.fortune"), ChatColor.AQUA));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.mining.fortune"), ChatColor.DARK_AQUA));
					}
				}
				case BREWER -> {
					completedLore.add(String.format(SilverConfig.getConstant("skills.gui.brewer.time"), ChatColor.LIGHT_PURPLE));
					incompleteLore.add(" ");
					incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.brewer.time"), ChatColor.DARK_PURPLE));
				}
				case HUSBANDRY -> {
					String id = switch (i) {
						case 25 -> "I";
						case 50 -> "II";
						case 75 -> "III";
						default -> "IV";
					};

					completedLore.add(String.format(SilverConfig.getConstant("skills.gui.husbandry.hero"), ChatColor.GREEN, id));
					incompleteLore.add(" ");
					incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.husbandry.hero"), ChatColor.DARK_GREEN, id));
				}
				case AQUATICS -> {
					if (i == 50) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.aquatics.breathing"), ChatColor.AQUA));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.aquatics.breathing"), ChatColor.DARK_AQUA));
					}
				}
				case CLEANER -> {
					if (i % 10 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.cleaner.unbreaking"), ChatColor.BLUE));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.cleaner.unbreaking"), ChatColor.DARK_AQUA));
					}
				}
				case ENCHANTER -> {
					if (i % 20 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.level"), ChatColor.LIGHT_PURPLE));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.level"), ChatColor.DARK_PURPLE));
					}

					if (i % 5 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.offer"), ChatColor.AQUA));
						if (!(incompleteLore.contains(" "))) incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.offer"), ChatColor.BLUE));
					}
				}
				case ADVANCER -> {
					if (i % 5 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.advancer.loot"), ChatColor.YELLOW));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.advancer.loot"), ChatColor.GOLD));
					}
				}
				case SMITHING -> {
					if (i == 30) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.insta"), ChatColor.LIGHT_PURPLE));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.insta"), ChatColor.DARK_PURPLE));
					}

					if (i % 4 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.resistance"), ChatColor.GREEN));
						if (!(incompleteLore.contains(" "))) incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.resistance"), ChatColor.DARK_GREEN));
					}
				}
				case ARCHERY -> {
					if (i % 5 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.archery.velocity"), ChatColor.YELLOW));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.archery.velocity"), ChatColor.GOLD));
					}

					completedLore.add(SilverConfig.getConstant("skills.gui.archery.damage"));
					if (!(incompleteLore.contains(" "))) incompleteLore.add(" ");
					incompleteLore.add(SilverConfig.getConstant("skills.gui.archery.damage"));
				}
				case TRAVELER -> {
					if (i % 10 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.traveler.saturation"), ChatColor.WHITE));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.traveler.saturation"), ChatColor.GRAY));
					}
				}
				case BUILDER -> {
					if (i % 5 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.builder.knockback"), ChatColor.RED));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.builder.knockback"), ChatColor.DARK_RED));
					}
				}
				case COLLECTOR -> {
					if (i % 2 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.collector.statistic"), ChatColor.GREEN));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.collector.statistic"), ChatColor.DARK_GREEN));
					}
				}
				case SOCIAL -> {
					if (i % 6 == 0) {
						completedLore.add(String.format(SilverConfig.getConstant("skills.gui.social.ignore"), ChatColor.RED));
						incompleteLore.add(" ");
						incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.social.ignore"), ChatColor.DARK_RED));
					}
				}
			}

			ItemStack stack = new ItemStack(m);
			ItemMeta stackMeta = stack.getItemMeta();
			stackMeta.setDisplayName(name);
			stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
			if (comp) stackMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
			stackMeta.setLore(comp ? completedLore : incompleteLore);
			stack.setItemMeta(stackMeta);
			
			icons.put(i - 1, stack);
		}
		
		return icons;
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
		iMeta.setDisplayName(ChatColor.AQUA + this.getCapitalizedName());
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
		return SilverConfig.getConstant("skill." + getDefaultName().toLowerCase());
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
		for (String piece : getName().split("\\s")) {
			s.append(WordUtils.capitalizeFully(piece)).append("\\s");
		}
		return s.toString();
	}
	
	// Public Static Methods
	
	/**
	 * Match the minimum combat experience for this entity type
	 * @param t Entity Type to use
	 * @return A double containing the minimum amount of combat experience a player will receive when killed
	 */
	public static double matchMinCombatExperience(EntityType t) {
		LivingEntity entity = (LivingEntity) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), 0, 0, 0), t);
		double defaultHP = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
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
	 * A static method to convert names from getName() to skill
	 * @param name Skill name
	 * @return Skill enum from name, may be null
	 */
	public static Skill matchSkill(String name) {
		for (Skill s : Skill.values()) {
			if (s.getName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		
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