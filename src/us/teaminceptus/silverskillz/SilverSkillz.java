package us.teaminceptus.silverskillz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import us.teaminceptus.silverskillz.commands.AddProgressCommand;
import us.teaminceptus.silverskillz.commands.RemoveProgressCommand;
import us.teaminceptus.silverskillz.commands.ResetProgressCommand;
import us.teaminceptus.silverskillz.commands.SettingsCommand;
import us.teaminceptus.silverskillz.commands.SkillsCommand;
import us.teaminceptus.silverskillz.skills.Skill;
import us.teaminceptus.silverskillz.skills.SkillAdvancer;
import us.teaminceptus.silverskillz.skills.SkillUtils;

/**
 * Main Plugin class for SilverSkillz
 * @author GamerCoder215
 *
 */
public class SilverSkillz extends JavaPlugin {
	
	private final SilverSkillz sk = this;

	private static final File messagesFile = new File(getPlugin(SilverSkillz.class).getDataFolder(), "messages.yml");
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
			if (!(messagesFile.exists())) {
				messagesFile.createNewFile();
			}
      reloadMessagesFile();
			return YamlConfiguration.loadConfiguration(messagesFile);
		} catch (IllegalArgumentException) {
			getLogger().info("Error fetching messages file");
			e.printStackTrace();
		}
	}

  public static void reloadMessagesFile() {
    
  }
	
	public void onEnable() {	
		this.saveDefaultConfig();
		this.saveConfig();
		
		new SkillAdvancer(this);
		new SkillUtils(this);
		
		new SkillsCommand(this);
		new AddProgressCommand(this);
		new ResetProgressCommand(this);
		new RemoveProgressCommand(this);
		new SettingsCommand(this);

		// Config Check
		if (!(this.getConfig().isBoolean("DisplayMessages"))) {
			this.getConfig().set("DisplayMessages", true);
		}

    if (!(this.getConfig().isList("DisabledCommands"))) {
      this.getConfig().set("DisabledCommands", new ArrayList<String>());
    }

    // Disabled Commands
    try {
      for (PluginCommand c : this.getDescription().getCommands()) {
        for (String s : this.getConfig().getStringList("DisabledCommands")) {
          if (c.getName().equalsIgnoreCase(s) || c.getAliases().contains(s)) {
            c.setExecutor(null);
          }
        }
      }
    } catch (Exception e) {
      getLogger().info("Malformed Config");
      e.printStackTrace();
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
	}

}