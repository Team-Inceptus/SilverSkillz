package us.teaminceptus.silverskillz;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import us.teaminceptus.silverskillz.commands.AddProgressCommand;
import us.teaminceptus.silverskillz.commands.ReloadConfigCommand;
import us.teaminceptus.silverskillz.commands.RemoveProgressCommand;
import us.teaminceptus.silverskillz.commands.ResetProgressCommand;
import us.teaminceptus.silverskillz.commands.SettingsCommand;
import us.teaminceptus.silverskillz.commands.SkillsCommand;
import us.teaminceptus.silverskillz.premium.commands.PremiumCommands;
import us.teaminceptus.silverskillz.skills.Skill;
import us.teaminceptus.silverskillz.skills.SkillAdvancer;
import us.teaminceptus.silverskillz.skills.SkillUtils;

/**
 * Main Plugin class for SilverSkillz
 * @author GamerCoder215
 *
 */
public class SilverSkillz extends JavaPlugin {
	
	/**
	 * Send a message from SilverSkillz plugin
	 * @param sender Sender to send it to
	 * @param msg The message to send
	 */
	public static void sendPluginMessage(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.GRAY + "SilverSkillz" + ChatColor.DARK_GREEN + "] " + ChatColor.RED + msg);
	}
	
	public static final FileConfiguration getMessagesFile() {
		try {
			File messages = new File(JavaPlugin.getPlugin(SilverSkillz.class).getDataFolder(), "messages.yml");
			if (!(messages.exists())) messages.createNewFile();
			FileConfiguration messagesFile = YamlConfiguration.loadConfiguration(messages);
		    return messagesFile;
		} catch (Exception e) {
			JavaPlugin.getPlugin(SilverSkillz.class).getLogger().info("Error loading messages.yml");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final void reloadMessagesFile() {
		FileConfiguration messagesFile = getMessagesFile();
		
		reloadMessagesFile(messagesFile);
	}
	
	private static final void reloadMessagesFile(FileConfiguration messagesFile) {
		if (!(messagesFile.isString("PluginName"))) {
	    	messagesFile.set("PluginName", "SilverSkillz");
	    }
	    
	    if (!(messagesFile.isString("InvalidPlayer"))) {
	    	messagesFile.set("InvalidPlayer", "Please provide a valid player.");
	    }
	    
	    if (!(messagesFile.isString("InvalidAmount"))) {
	    	messagesFile.set("InvalidAmount", "Please provide a valid amount.");
	    }
	    
	    if (!(messagesFile.isString("ErrorArguments"))) {
	    	messagesFile.set("ErrorArguments", "There was an error parsing arguments.");
	    }
	    
	    if (!(messagesFile.isString("Error"))) {
	    	messagesFile.set("Error", "There was an error");
	    }
	    
	    // Success Messages
	    
	    if (!(messagesFile.isString("SuccessIncrease"))) {
	    	messagesFile.set("SuccessIncrease", "Increase skill successful.");
	    }
	    
	    if (!(messagesFile.isString("SuccessIncreaseAll"))) {
	    	messagesFile.set("SuccessIncreaseAll", "Increase skills successful.");
	    }
	    
	    if (!(messagesFile.isString("SuccessDecrease"))) {
	    	messagesFile.set("SuccessDecrease", "Decrease skill successful.");
	    }
	    
	    if (!(messagesFile.isString("SuccessDecreaseAll"))) {
	    	messagesFile.set("SuccessDecreaseAll", "Decrease skills successful.");
	    }
	    
	    if (!(messagesFile.isString("SuccessReset"))) {
	    	messagesFile.set("SuccessReset", "Successfully reset skill.");
	    }
	    
	    if (!(messagesFile.isString("SuccessResetAll"))) {
	    	messagesFile.set("SuccessResetAll", "Successfully reset each skill.");
	    }
	    
	    // Other
	    
	    if (!(messagesFile.isString("LevelUp"))) {
	    	messagesFile.set("LevelUp", "%skill% has leveled up!");
	    }
	    
	    if (!(messagesFile.isString("ExperienceGain"))) {
	    	messagesFile.set("ExperienceGain", "+%exp% %skill% Experience");
	    }
	    
	    
	    // Inventory Titles
	    if (!(messagesFile.isConfigurationSection("InventoryTitles"))) {
	    	messagesFile.createSection("InventoryTitles");
	    }
	    
	    ConfigurationSection titles = messagesFile.getConfigurationSection("InventoryTitles");
	    
	    if (!(titles.isString("Settings"))) {
	    	titles.set("Settings", "Player Settings");
	    }
	    
	    if (!(titles.isString("SkillMenu"))) {
	    	titles.set("SkillMenu", "Player Skills");
	    }
	    
	    // Skill Names
	    if (!(messagesFile.isConfigurationSection("SkillNames"))) {
	    	messagesFile.createSection("SkillNames");
	    }
	    
	    ConfigurationSection skillNames = messagesFile.getConfigurationSection("SkillNames");
	    
	   if (!(skillNames.isString("Combat"))) {
		   skillNames.set("Combat", "combat");
	   }
	   
	   if (!(skillNames.isString("Archery"))) {
		   skillNames.set("Archery", "archery");
	   }
	   
	   if (!(skillNames.isString("Aquatics"))) {
		   skillNames.set("Aquatics", "aquatics");
	   }
	   
	   if (!(skillNames.isString("Advancer"))) {
		   skillNames.set("Advancer", "advancer");
	   }
	   
	   if (!(skillNames.isString("Brewer"))) {
		   skillNames.set("Brewer", "brewer");
	   }
	   
	   if (!(skillNames.isString("Cleaner"))) {
		   skillNames.set("Cleaner", "cleaner");
	   }
	   
	   if (!(skillNames.isString("Builder"))) {
		   skillNames.set("Builder", "builder");
	   }
	   
	   if (!(skillNames.isString("Enchanter"))) {
		   skillNames.set("Enchanter", "enchanter");
	   }
	   
	   if (!(skillNames.isString("Social"))) {
		   skillNames.set("Social", "social");
	   }
	   
	   if (!(skillNames.isString("Traveler"))) {
		   skillNames.set("Traveler", "traveler");
	   }
	   
	   if (!(skillNames.isString("Mining"))) {
		   skillNames.set("Mining", "mining");
	   }
	   
	   if (!(skillNames.isString("Smithing"))) {
		   skillNames.set("Smithing", "smithing");
	   }
	   
	   if (!(skillNames.isString("Collector"))) {
		   skillNames.set("Collector", "collector");
	   }
	   
	   if (!(skillNames.isString("Farming"))) {
		   skillNames.set("Farming", "farming");
	   }
	   
	   if (!(skillNames.isString("Husbandry"))) {
		   skillNames.set("Husbandry", "husbandry");
	   }
	    
	    // Inventory Items
	    
	    if (!(messagesFile.isConfigurationSection("InventoryItems"))) {
	    	messagesFile.createSection("InventoryItems");
	    }
	    
	    ConfigurationSection inventoryItems = messagesFile.getConfigurationSection("InventoryItems");
	    
	    if (!(inventoryItems.isConfigurationSection("SkillInventory"))) {
	    	inventoryItems.createSection("SkillInventory");
	    }	    
	    
	    ConfigurationSection skillInv = inventoryItems.getConfigurationSection("SkillInventory");
	    
	    if (!(skillInv.isString("NextPage"))) {
	    	skillInv.set("NextPage", "Next Page");
	    }
	    
	    if (!(skillInv.isString("PreviousPage"))) {
	    	skillInv.set("PreviousPage", "Previous Page");
	    }
	    
	    if (!(skillInv.isString("Back"))) {
	    	skillInv.set("Back", "Back");
	    }
	    
	    if (!(skillInv.isString("PlayerStatistics"))) {
	    	skillInv.set("PlayerStatistics", "%player%'s Statistics");
	    }
	    
	    try {
	    	messagesFile.save(new File(JavaPlugin.getPlugin(SilverSkillz.class).getDataFolder(), "messages.yml"));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	public void onEnable() {
		getLogger().info("Loading Config...");
		this.saveDefaultConfig();
		this.saveConfig();
		getLogger().info("Loading messages.yml...");
		reloadMessagesFile();
		
		getLogger().info("Loading Commands...");
		new SkillAdvancer(this);
		new SkillUtils(this);
		
		new SkillsCommand(this);
		new AddProgressCommand(this);
		new ResetProgressCommand(this);
		new RemoveProgressCommand(this);
		new SettingsCommand(this);
		new ReloadConfigCommand(this);
		
		getLogger().info("Loading options...");
		// Config Check
		if (!(this.getConfig().isBoolean("DisplayMessages"))) {
			this.getConfig().set("DisplayMessages", true);
		}
		
		// Global Effects
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					SilverPlayer sp = new SilverPlayer(p);
					if (!(sp.hasPotionEffects())) return;
					
					int hLevel = sp.getSkill(Skill.HUSBANDRY).getLevel();
					int aLevel = sp.getSkill(Skill.AQUATICS).getLevel();
					
					if (hLevel >= 25 && hLevel < 50) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 0, true, false, false));
					} else if (hLevel >= 50 && hLevel < 75) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 1, true, false, false));
					} else if (hLevel >= 75 && hLevel < 100) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 2, true, false, false));
					} else if (hLevel == 100) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 3, true, false, false));
					}

					if (aLevel > 50) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 200000, 1, true, false, false));
					}
				}
			}
		}.runTaskTimer(this, 0, 4);
		this.saveConfig();
		// Premium
		try {
			Class.forName("us.teaminceptus.silverskillz.premium.commands.PremiumCommands");

			new us.teaminceptus.silverskillz.premium.License(); // Check if License Exists
			if (us.teaminceptus.silverskillz.premium.PremiumUtils.isCracked()) {
				getLogger().info("!! Cracked version detected, disabling; Please contact support if this is an error. !!");
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
			// License Exists

			PremiumCommands.register();
			
		} catch (Exception e) {
			getLogger().info("Free version detected! Please consider purchasing the Premium Version on our spigot page!");
		} catch (NoClassDefFoundError e) {
			getLogger().info("!! Cracked version detected, disabling; Please contact support if this is an error. !!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		getLogger().info("Complete!");
	}

	public static final boolean isPremium() {
		try {
			Class.forName("us.teaminceptus.silverskillz.premium.commands.PremiumCommands");

			new us.teaminceptus.silverskillz.premium.License();
			if (us.teaminceptus.silverskillz.premium.PremiumUtils.isCracked()) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

}