package us.teaminceptus.silverskillz.commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import us.teaminceptus.silverskillz.SilverPlayer;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.skills.SkillUtils;

public class SettingsCommand implements CommandExecutor, Listener {

	protected SilverSkillz plugin;

	public SettingsCommand(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getCommand("settings").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

  private static String settingsInventoryName = SilverSkillz.getMessagesFile().getConfigurationSection("InventoryTitles").getString("Settings");

	public final static Inventory settingsInventory(SilverPlayer p) {
		Inventory inv = SkillUtils.generateGUI(45, ChatColor.DARK_AQUA + settingsInventoryName);

		ItemStack messages = new ItemStack((p.canSeeSkillMessages() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
		ItemMeta mMeta = messages.getItemMeta();
		mMeta.setDisplayName(ChatColor.YELLOW + "Skill Messages: " + (p.canSeeSkillMessages() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
		messages.setItemMeta(mMeta);
		
		ItemStack potionEffects = new ItemStack((p.hasPotionEffects() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
		ItemMeta pMeta = messages.getItemMeta();
		pMeta.setDisplayName(ChatColor.YELLOW + "Potion Effects: " + (p.hasPotionEffects() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
		potionEffects.setItemMeta(pMeta);
		
		ItemStack customMessages = new ItemStack((p.hasPotionEffects() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
		ItemMeta cMeta = customMessages.getItemMeta();
		cMeta.setDisplayName(ChatColor.YELLOW + "Use Messages Names: " + (p.hasMessagesOn() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
		customMessages.setItemMeta(cMeta);
		
		inv.setItem(10, messages);
		inv.setItem(11, potionEffects);
		inv.setItem(12, customMessages);

		// Premium Settings
		if (SilverSkillz.isPremium()) {
			ItemStack abilities = new ItemStack((p.hasAbilities() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
			ItemMeta aMeta = customMessages.getItemMeta();
			aMeta.setDisplayName(ChatColor.YELLOW + "Abilities: " + (p.hasAbilities() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
			abilities.setItemMeta(aMeta);

			inv.setItem(13, abilities);
		}

		return inv;
	}
	private static String matchSetting(ItemStack i) {
		String name = ChatColor.stripColor(i.getItemMeta().getDisplayName()).toLowerCase().replaceAll("on", "").replaceAll("off", "").replaceAll(" ", "").replaceAll(":", "");
		if (name.equalsIgnoreCase("skillmessages")) return "messages";
		else if (name.equalsIgnoreCase("potieffects")) return "potion-effects";
		else if (name.equalsIgnoreCase("usemessagesnames")) return "custom-messages";
		else if (name.equalsIgnoreCase("abilities")) return "abilities";
		else return null;
	}

	private final void toggleSetting(SilverPlayer p, Inventory inv, ItemStack item, int slot) {
		if (!(item.getItemMeta().getDisplayName().contains("On")) && !(item.getItemMeta().getDisplayName().contains("Off"))) return;
		String setting = matchSetting(item);
		boolean newValue = !(p.getPlayerConfig().getConfigurationSection("settings").getBoolean(setting));
		p.getPlayerConfig().getConfigurationSection("settings").set(setting, newValue);
		try {
			p.getPlayerConfig().save(p.getPlayerFile());		
		} catch (IOException e) {
			plugin.getLogger().info("Error toggling setting");
			e.printStackTrace();
		}

		String newName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).replaceAll("On", "").replaceAll("Off", "");
		ItemStack newItem = item.clone();
		newItem.setType(item.getType() == Material.LIME_CONCRETE ? Material.RED_CONCRETE : Material.LIME_CONCRETE);
		ItemMeta newMeta = newItem.getItemMeta();
		newMeta.setDisplayName(ChatColor.YELLOW + newName + (item.getType() == Material.LIME_CONCRETE ? ChatColor.RED + "Off" : ChatColor.GREEN + "On"));
		newItem.setItemMeta(newMeta);

		inv.setItem(slot, newItem);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof OfflinePlayer p)) return;
		InventoryView view = e.getView();
		SilverPlayer sp = new SilverPlayer(p);	
		if (!(ChatColor.stripColor(view.getTitle()).contains(settingsInventoryName))) return;
		if (e.getCurrentItem() == null) return;
		e.setCancelled(true);
		Inventory gui = view.getTopInventory();

		toggleSetting(sp, gui, e.getCurrentItem(), e.getSlot());
		return;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return false;

		Player p = (Player) sender;

		p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 3F, 1F);
		p.openInventory(settingsInventory(new SilverPlayer(p)));

		return true;
	}

}