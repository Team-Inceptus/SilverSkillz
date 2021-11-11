package us.teaminceptus.silverskillz.commands;

import java.io.IOException;

import javax.annotation.Nullable;

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

	public final static Inventory settingsInventory(SilverPlayer p) {
		Inventory inv = SkillUtils.generateGUI(45, ChatColor.DARK_AQUA + "Player Settings");

		ItemStack messages = new ItemStack((p.canSeeSkillMessages() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
		ItemMeta mMeta = messages.getItemMeta();
		mMeta.setDisplayName(ChatColor.YELLOW + "Skill Messages: " + (p.canSeeSkillMessages() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
		messages.setItemMeta(mMeta);

		inv.setItem(10, messages);

		return inv;
	}
	
	@Nullable
	private static String matchSetting(ItemStack i) {
		String name = ChatColor.stripColor(i.getItemMeta().getDisplayName()).toLowerCase().replaceAll("on", "").replaceAll("off", "").replaceAll(" ", "").replaceAll(":", "");

		if (name.equalsIgnoreCase("skillmessages")) return "messages";
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
		if (!(ChatColor.stripColor(view.getTitle()).contains("SilverSkillz - Player Settings"))) return;
		if (e.getCurrentItem() == null) return;
		e.setCancelled(true);
		Inventory gui = view.getTopInventory();

		toggleSetting(sp, gui, e.getCurrentItem(), e.getSlot());
		return;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player p = (Player) sender;

		p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 3F, 1F);
		p.openInventory(settingsInventory(new SilverPlayer(p)));

		return true;
	}

}