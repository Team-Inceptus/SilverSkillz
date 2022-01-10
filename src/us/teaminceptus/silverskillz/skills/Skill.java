package us.teaminceptus.silverskillz.skills;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.silverskillz.SilverPlayer;
import us.teaminceptus.silverskillz.SilverSkillz;

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

	public static final double MAX_PROGRESS_VALUE = 1000000000;

	private final String name;
	private final Statistic[] increases;
	private final Map<Attribute, Double> modifiers;
	private final Material icon;
	
	private Skill(String name, Statistic[] supported, Map<Attribute, Double> modifiers, Material icon) {
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
	  return (getDefaultName().substring(0, 1).toUpperCase() + getDefaultName().substring(1));
  }
	
	protected static void awardLevelUp(SilverPlayer p, Skill s, boolean hasLeveled, double increaseBy) {
		if (p.getOnlinePlayer() == null) return;
		if (s == TRAVELER) return;
		if (!(JavaPlugin.getPlugin(SilverSkillz.class).getConfig().getBoolean("DisplayMessages"))) return;
		if (!(p.canSeeSkillMessages())) return;
		SkillUtils.sendActionBar(p.getOnlinePlayer(), ChatColor.GREEN + SilverSkillz.getMessagesFile().getString("ExperienceGain").replaceAll("%exp%", df.format(increaseBy).replaceAll("%skill%", s.getCapitalizedName())).replaceAll("%player%", p.getPlayer().getName()));
		
		if (hasLeveled) {
			Player pl = p.getOnlinePlayer();
			pl.sendTitle(ChatColor.GOLD + SilverSkillz.getMessagesFile().getString("LevelUp").replaceAll("%skill%", s.getCapitalizedName()).replaceAll("%player%", p.getPlayer().getName()).replaceAll("%skill%", s.getCapitalizedName()), "", 5, 100, 10);
		}
	}
	
	/**
	 * The central menu for the /skill command
	 * @return Inventory containing menu
	 */
	public static final Inventory getMenu() {
		Inventory menu = SkillUtils.generateGUI(54, ChatColor.DARK_AQUA + SilverSkillz.getMessagesFile().getConfigurationSection("InventoryTitles").getString("SkillMenu"));
		
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
		String statistics = SilverSkillz.getMessagesFile().getConfigurationSection("InventoryItems").getConfigurationSection("SkillInventory").getString("PlayerStatistics").replaceAll("%player%", p.getPlayer().getName()).replaceAll("%skill%", this.getName()).replaceAll("%capitalskill%", this.getCapitalizedName());
		String nextPage = SilverSkillz.getMessagesFile().getConfigurationSection("InventoryItems").getConfigurationSection("SkillInventory").getString("NextPage").replaceAll("%player%", p.getPlayer().getName()).replaceAll("%skill%", this.getName()).replaceAll("%capitalskill%", this.getCapitalizedName());
		String previousPage = SilverSkillz.getMessagesFile().getConfigurationSection("InventoryItems").getConfigurationSection("SkillInventory").getString("PreviousPage").replaceAll("%player%", p.getPlayer().getName()).replaceAll("%skill%", this.getName()).replaceAll("%capitalskill%", this.getCapitalizedName());
		String back = SilverSkillz.getMessagesFile().getConfigurationSection("InventoryItems").getConfigurationSection("SkillInventory").getString("Back").replaceAll("%player%", p.getPlayer().getName()).replaceAll("%skill%", this.getName()).replaceAll("%capitalskill%", this.getCapitalizedName());
		
		Map<Integer, Inventory> pages = new HashMap<>();
		
		Map<Short, ItemStack> panels = getInventoryIcons(this, p);
		
		ItemStack headInfo = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta headMeta = (SkullMeta) headInfo.getItemMeta();
		headMeta.setOwningPlayer(p.getPlayer());
		headMeta.setDisplayName(ChatColor.GOLD + statistics);
		List<String> info = new ArrayList<>();
		info.add(ChatColor.GREEN + SkillUtils.withSuffix(p.getSkill(this).getProgress()) + " Total Progress");
		info.add(ChatColor.DARK_GREEN + "Level " + Short.toString(p.getSkill(this).getLevel()));
		headMeta.setLore(info);
		headInfo.setItemMeta(headMeta);
		
		ItemStack arrowForward = new ItemStack(Material.ARROW);
		ItemMeta forwardMeta = arrowForward.getItemMeta();
		forwardMeta.setDisplayName(ChatColor.GREEN + nextPage);
		arrowForward.setItemMeta(forwardMeta);
		
		ItemStack arrowBack = new ItemStack(Material.ARROW);
		ItemMeta backMeta = arrowForward.getItemMeta();
		backMeta.setDisplayName(ChatColor.GREEN + previousPage);
		arrowBack.setItemMeta(backMeta);
		
		ItemStack menuBack = new ItemStack(Material.BEACON);
		ItemMeta menuMeta = menuBack.getItemMeta();
		menuMeta.setDisplayName(ChatColor.RED + back);
		menuBack.setItemMeta(menuMeta);
		// First Page
		Inventory firstPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill");
		
		firstPage.setItem(9, getIconAsStack());
		firstPage.setItem(4, headInfo);
		firstPage.setItem(49, menuBack);
		firstPage.setItem(50, arrowForward);
		
		firstPage.setItem(10, panels.get((short) 0));
		firstPage.setItem(19, panels.get((short) 1));
		firstPage.setItem(28, panels.get((short) 2));
		firstPage.setItem(37, panels.get((short) 3));
		firstPage.setItem(38, panels.get((short) 4));
		firstPage.setItem(39, panels.get((short) 5));
		firstPage.setItem(30, panels.get((short) 6));
		firstPage.setItem(21, panels.get((short) 7));
		firstPage.setItem(12, panels.get((short) 8));
		firstPage.setItem(13, panels.get((short) 9));
		firstPage.setItem(14, panels.get((short) 10));
		firstPage.setItem(23, panels.get((short) 11));
		firstPage.setItem(32, panels.get((short) 12));
		firstPage.setItem(41, panels.get((short) 13));
		firstPage.setItem(42, panels.get((short) 14));
		firstPage.setItem(43, panels.get((short) 15));
		firstPage.setItem(34, panels.get((short) 16));
		firstPage.setItem(25, panels.get((short) 17));
		firstPage.setItem(16, panels.get((short) 18));
		firstPage.setItem(17, panels.get((short) 19));
		
		pages.put(0, firstPage);
		// Second Page
		Inventory secondPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 2");
		
		secondPage.setItem(9, getIconAsStack());
		secondPage.setItem(4, headInfo);
		secondPage.setItem(50, arrowForward);
		secondPage.setItem(48, arrowBack);
		
		secondPage.setItem(10, panels.get((short) 20));
		secondPage.setItem(19, panels.get((short) 21));
		secondPage.setItem(28, panels.get((short) 22));
		secondPage.setItem(37, panels.get((short) 23));
		secondPage.setItem(38, panels.get((short) 24));
		secondPage.setItem(39, panels.get((short) 25));
		secondPage.setItem(30, panels.get((short) 26));
		secondPage.setItem(21, panels.get((short) 27));
		secondPage.setItem(12, panels.get((short) 28));
		secondPage.setItem(13, panels.get((short) 29));
		secondPage.setItem(14, panels.get((short) 30));
		secondPage.setItem(23, panels.get((short) 31));
		secondPage.setItem(32, panels.get((short) 32));
		secondPage.setItem(41, panels.get((short) 33));
		secondPage.setItem(42, panels.get((short) 34));
		secondPage.setItem(43, panels.get((short) 35));
		secondPage.setItem(34, panels.get((short) 36));
		secondPage.setItem(25, panels.get((short) 37));
		secondPage.setItem(16, panels.get((short) 38));
		secondPage.setItem(17, panels.get((short) 39));
		
		pages.put(1, secondPage);
		// Third Page
		Inventory thirdPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 3");
		
		thirdPage.setItem(9, getIconAsStack());
		thirdPage.setItem(4, headInfo);
		thirdPage.setItem(50, arrowForward);
		thirdPage.setItem(48, arrowBack);
		
		thirdPage.setItem(10, panels.get((short) 40));
		thirdPage.setItem(19, panels.get((short) 41));
		thirdPage.setItem(28, panels.get((short) 42));
		thirdPage.setItem(37, panels.get((short) 43));
		thirdPage.setItem(38, panels.get((short) 44));
		thirdPage.setItem(39, panels.get((short) 45));
		thirdPage.setItem(30, panels.get((short) 46));
		thirdPage.setItem(21, panels.get((short) 47));
		thirdPage.setItem(12, panels.get((short) 48));
		thirdPage.setItem(13, panels.get((short) 49));
		thirdPage.setItem(14, panels.get((short) 50));
		thirdPage.setItem(23, panels.get((short) 51));
		thirdPage.setItem(32, panels.get((short) 52));
		thirdPage.setItem(41, panels.get((short) 53));
		thirdPage.setItem(42, panels.get((short) 54));
		thirdPage.setItem(43, panels.get((short) 55));
		thirdPage.setItem(34, panels.get((short) 56));
		thirdPage.setItem(25, panels.get((short) 57));
		thirdPage.setItem(16, panels.get((short) 58));
		thirdPage.setItem(17, panels.get((short) 59));
		
		pages.put(2, thirdPage);
		
		// Fourth Page
		Inventory fourthPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 4");
		
		fourthPage.setItem(9, getIconAsStack());
		fourthPage.setItem(4, headInfo);
		fourthPage.setItem(50, arrowForward);
		fourthPage.setItem(48, arrowBack);
		
		fourthPage.setItem(10, panels.get((short) 60));
		fourthPage.setItem(19, panels.get((short) 61));
		fourthPage.setItem(28, panels.get((short) 62));
		fourthPage.setItem(37, panels.get((short) 63));
		fourthPage.setItem(38, panels.get((short) 64));
		fourthPage.setItem(39, panels.get((short) 65));
		fourthPage.setItem(30, panels.get((short) 66));
		fourthPage.setItem(21, panels.get((short) 67));
		fourthPage.setItem(12, panels.get((short) 68));
		fourthPage.setItem(13, panels.get((short) 69));
		fourthPage.setItem(14, panels.get((short) 70));
		fourthPage.setItem(23, panels.get((short) 71));
		fourthPage.setItem(32, panels.get((short) 72));
		fourthPage.setItem(41, panels.get((short) 73));
		fourthPage.setItem(42, panels.get((short) 74));
		fourthPage.setItem(43, panels.get((short) 75));
		fourthPage.setItem(34, panels.get((short) 76));
		fourthPage.setItem(25, panels.get((short) 77));
		fourthPage.setItem(16, panels.get((short) 78));
		fourthPage.setItem(17, panels.get((short) 79));
		
		pages.put(3, fourthPage);
		// Fifth Page
		Inventory fifthPage = SkillUtils.generateGUI(54, ChatColor.AQUA + this.getCapitalizedName() + " Skill - Page 5");
		
		fifthPage.setItem(9, getIconAsStack());
		fifthPage.setItem(4, headInfo);
		fifthPage.setItem(48, arrowBack);
		
		fifthPage.setItem(10, panels.get((short) 80));
		fifthPage.setItem(19, panels.get((short) 81));
		fifthPage.setItem(28, panels.get((short) 82));
		fifthPage.setItem(37, panels.get((short) 83));
		fifthPage.setItem(38, panels.get((short) 84));
		fifthPage.setItem(39, panels.get((short) 85));
		fifthPage.setItem(30, panels.get((short) 86));
		fifthPage.setItem(21, panels.get((short) 87));
		fifthPage.setItem(12, panels.get((short) 88));
		fifthPage.setItem(13, panels.get((short) 89));
		fifthPage.setItem(14, panels.get((short) 90));
		fifthPage.setItem(23, panels.get((short) 91));
		fifthPage.setItem(32, panels.get((short) 92));
		fifthPage.setItem(41, panels.get((short) 93));
		fifthPage.setItem(42, panels.get((short) 94));
		fifthPage.setItem(43, panels.get((short) 95));
		fifthPage.setItem(34, panels.get((short) 96));
		fifthPage.setItem(25, panels.get((short) 97));
		fifthPage.setItem(16, panels.get((short) 98));
		fifthPage.setItem(17, panels.get((short) 99));
		
		pages.put(4, fifthPage);

		return pages;
	}
	
	static DecimalFormat df = new DecimalFormat("###.#");
	
	/**
	 * Whether or not this skill is basic
	 * @return true if basic, else false
	 */
	public final boolean isBasic() {
		switch (this) {
			case ADVANCER:
				return true;
			default:
				return false;
		}
	}
	
	protected static final Map<Short, ItemStack> getInventoryIcons(Skill s, SilverPlayer p) {
		df.setRoundingMode(RoundingMode.FLOOR);
		Map<Short, ItemStack> icons = new HashMap<>();
		
		for (short i = 1; i <= 100; i++) {
			short nextLevel = (short) (i + 1);
			Material m = (p.getSkill(s).getProgress() >= Skill.toMinimumProgress(s.isBasic(), i) ? Material.LIME_STAINED_GLASS_PANE : Material.YELLOW_STAINED_GLASS_PANE);
			String name = (p.getSkill(s).getProgress() >= Skill.toMinimumProgress(s.isBasic(), i) ? ChatColor.GREEN : ChatColor.YELLOW) + "Level " + Short.toString(i) + " | " + (p.getSkill(s).getProgress() >= toMinimumProgress(s.isBasic(), i) ? "Complete" : "Incomplete");
			
			List<String> completedLore = new ArrayList<>();
			
			List<String> incompleteLore = new ArrayList<>();
			
			if (i != 100) {
				incompleteLore.add(ChatColor.YELLOW + SkillUtils.withSuffix(Double.parseDouble(df.format(p.getSkill(s).getProgress()))) + " / " + SkillUtils.withSuffix(Double.parseDouble(df.format(toMinimumProgress(s.isBasic(), nextLevel)))));
			} else {
				incompleteLore.add(ChatColor.YELLOW + SkillUtils.withSuffix(Double.parseDouble(df.format(p.getSkill(s).getProgress()))) + " / " + SkillUtils.withSuffix(Double.parseDouble(df.format(toMinimumProgress(s.isBasic(), (short)100)))));
			}
			if (s == COMBAT) {
				completedLore.add(ChatColor.BLUE + "Level " + Short.toString(i) + " Total Damage Buff: " + ChatColor.GOLD + df.format((Math.pow(i, 1.9)) + i * 3.7));
				incompleteLore.add(" ");
				incompleteLore.add(ChatColor.AQUA + "Level " + Short.toString(i) + " Total Damage Buff: " + ChatColor.GOLD + df.format((Math.pow(i, 1.9)) + i * 3.7));
			} else if (s == FARMING) {
				if (i % 20 == 0) {
					completedLore.add(ChatColor.GOLD + "+1 Farming Drops");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.YELLOW + "+1 Farming Drops");
				}
			} else if (s == MINING) {
				if (i % 5 == 0) {
					completedLore.add(ChatColor.AQUA + "+10% Ore Fortune");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_AQUA + "+10% Ore Fortune");
				}
			} else if (s == BREWER) {
				completedLore.add(ChatColor.LIGHT_PURPLE + "+5s Potion Time");
				incompleteLore.add(" ");
				incompleteLore.add(ChatColor.DARK_PURPLE + "+5s Potion Time");
			} else if (s == HUSBANDRY) {
				if (i == 25) {
					completedLore.add(ChatColor.GREEN + "Hero of the Village I");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_GREEN + "Hero of the Village I");
				} else if (i == 50) {
					completedLore.add(ChatColor.GREEN + "Hero of the Village II");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_GREEN + "Hero of the Village II");
				} else if (i == 75) {
					completedLore.add(ChatColor.GREEN + "Hero of the Village III");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_GREEN + "Hero of the Village III");
				} else if (i == 100) {
					completedLore.add(ChatColor.GREEN + "Hero of the Village IV");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_GREEN + "Hero of the Village IV");
				}
			} else if (s == AQUATICS) {
				if (i == 50) {
					completedLore.add(ChatColor.AQUA + "Water Breathing");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_AQUA + "Water Breathing");
				}
			} else if (s == CLEANER) {
				if (i % 10 == 0) {
					completedLore.add(ChatColor.BLUE + "+10% True Unbreaking");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_AQUA + "+10% True Unbreaking");
				}
			} else if (s == ENCHANTER) {
				if (i % 20 == 0) {
					completedLore.add(ChatColor.LIGHT_PURPLE + "+1 Enchant Offer Level");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_PURPLE + "+1 Enchant Offer Level");
				}

				if (i % 5 == 0) {
					completedLore.add(ChatColor.AQUA + "-5% Enchant Offer Cost");
					if (!(incompleteLore.contains(" "))) incompleteLore.add(" ");
					incompleteLore.add(ChatColor.BLUE + "-5% Enchant Offer Cost");
				}
			} else if (s == ADVANCER) {
				if (i % 5 == 0) {
					completedLore.add(ChatColor.YELLOW + "+10% Loot Luck");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.GOLD + "+10% Loot Luck");
				}
			} else if (s == SMITHING) {
				if (i == 30) {
					completedLore.add(ChatColor.LIGHT_PURPLE + "Insta-Break Wood");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_PURPLE + "Insta-Break Wood");
				}

				if (i % 4 == 0) {
					completedLore.add(ChatColor.GREEN + "+5% Super Resistance");
					if (!(incompleteLore.contains(" "))) incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_GREEN + "+5% Super Resistance");
				}
			} else if (s == ARCHERY) {
				if (i % 5 == 0) {
					completedLore.add(ChatColor.YELLOW + "+5% Projectile Velocity");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.GOLD + "+5% Projectile Velocity");
				}

				completedLore.add(ChatColor.RED + "+5 Projectile Damage");
				if (!(incompleteLore.contains(" "))) incompleteLore.add(" ");
				incompleteLore.add(ChatColor.RED + "+5 Projectile Damage");
			} else if (s == TRAVELER) {
				if (i % 10 == 0) {
					completedLore.add(ChatColor.WHITE + "+10% Saturation");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.GRAY + "+10% Saturation");
				}
			} else if (s == BUILDER) {
				if (i % 5 == 0) {
					completedLore.add(ChatColor.RED + "+10% Attack Knockback");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_RED + "+10% Attack Knockback");
				}
			} else if (s == COLLECTOR) {
				if (i % 2 == 0) {
					completedLore.add(ChatColor.GREEN + "+10% Statistic Increase");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_GREEN + "10% Statistic Increase");
				}
			} else if (s == SOCIAL) {
				if (i % 6 == 0) {
					completedLore.add(ChatColor.RED + "+5% Mob Ignore Chance");
					incompleteLore.add(" ");
					incompleteLore.add(ChatColor.DARK_RED + "+5% Mob Ignore Chance");
				}
			}
			ItemStack stack = new ItemStack(m);
			ItemMeta stackMeta = stack.getItemMeta();
			
			stackMeta.setDisplayName(name);
			stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
			
			if (p.getSkill(s).getProgress() >= Skill.toMinimumProgress(s.isBasic(), i)) stackMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
			
			
			stackMeta.setLore((p.getSkill(s).getProgress() >= Skill.toMinimumProgress(s.isBasic(), i) ? completedLore : incompleteLore));
			
			stack.setItemMeta(stackMeta);
			
			icons.put((short) (i - 1), stack);
		}
		
		return icons;
	}
	
	protected static final Map<Attribute, Double> getModifier(String name) {
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
		return SilverSkillz.getMessagesFile().getConfigurationSection("SkillNames").getString(getCapitalizedDefaultName());
	}
	
	/**
	 * Gets the name that the player sees based on the setting "Use Messages Names"
	 * @param sp The player to use off of
	 * @return Skill name that the player sees
	 */
	public final String getName(SilverPlayer sp) {
		if (sp.hasMessagesOn()) {
			return getName();
		} else {
			return getDefaultName();
		}
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
		return (getName().substring(0, 1).toUpperCase() + getName().substring(1));
	}
	
	// Public Static Methods
	
	/**
	 * Match the minimum combat experience for this entity type
	 * @param t Entity Type to use
	 * @return A double containing the minimum amount of combat experience a player will receive when killed
	 */
	public static final double matchMinCombatExperience(EntityType t) {
		LivingEntity entity = (LivingEntity) Bukkit.getWorld("world").spawnEntity(new Location(Bukkit.getWorld("world"), 0, 0, 0), t);
		double defaultHP = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		entity.remove();
		
		return (defaultHP / 5);
	}
	
	/**
	 * Match the minimum combat experience for this living  entity
	 * @param e Entity to match off of
	 * @return A double containing the minimum amount of combat experience a player will receive when killed
	 */
	public static final double matchMinCombatExperience(LivingEntity e) {
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
	public static final double toMinimumProgress(boolean basic, short level) {
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
	 * @return short containing its level
	 */
	public static final short toLevel(boolean basic, double progress) {
		if (!(basic)) {
			if (progress < 791.7) return 0;
			return ((short) Math.floor(Math.pow((progress / 150), (1 / 2.4))));
		} else {
			return (short) Math.floor(progress / 2);
		}
	}

}