package me.gamercoder215.silverskillz.commands;

public class SettingsCommand implements CommandExecutor, Listener {

	protected SilverSkillz plugin;

	public SettingCommand(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getCommand("settings").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public final static Inventory settingsInventory(SilverPlayer p) {
		Inventory inv = SkillUtils.generateGUI(45, "Player Settings");

		ItemStack messages = new ItemStack((p.canSeeSkillMessages() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
		ItemMeta mMeta = messages.getItemMeta();
		mMeta.setDisplayName(ChatColor.YELLOW + "Skill Messages: " + (p.canSeeSkillMessages() ? ChatColor.GREEN + "On" : ChatColor.RED + "Off"));
		messages.setItemMeta(mMeta);

		inv.setItem(10, messages);

		return inv;
	}

	private static String matchSetting(ItemStack i) {
		String name = ChatColor.stripColor(i.getItemMeta().getDisplayName()).toLowerCase().replaceAll("on", "").replaceAll("off", "").replaceAll(" ", "").replaceAll(":", "");

		if (name.equalsIgnoreCase("skillmessages")) return "messages";
	}

	private final void toggleSetting(SilverPlayer p, Inventory inv, ItemStack item, int slot) {
		if (!(item.getItemMeta().getDisplayName().contains("On")) && !(item.getItemMeta().getDisplayName().contains("Off"))) return;
		String setting = matchSetting(item);
		boolean newValue = !(p.getPlayerConfig().getConfigurationSection("settings").getBoolean(setting));
		new BukkitRunnable() {
			public void run() {
				p.getPlayerConfig().getConfigurationSection("settings").set(setting, newValue);
				try {
					p.getPlayerConfig().save(p.getPlayerFile());		
				} catch (IOException e) {
					plugin.getLogger().info("Error toggling setting");
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);

		String newName = ChatColor.stripColor(i.getItemMeta().getDisplayName()).replaceAll("On", "").replaceAll("Off", "");
		ItemStack newItem = item.clone();
		newItem.setType(item.getType() == Material.LIME_CONCRETE ? Material.RED_CONCRETE : Material.LIME_CONCRETE);
		ItemMeta newMeta = newItem.getItemMeta();
		newMeta.setDisplayName(ChatColor.YELLOW + newName + (item.getType() == Material.LIME_CONCRETE ? Material.RED_CONCRETE : Material.LIME_CONCRETE));
		newItem.setItemMeta(newMeta);

		inv.setItem(slot, newItem);
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		InventoryView view = e.getView();
		SilverPlayer sp = SilverPlayer.fromPlayer(e.getPlayer());
		if (!(view.getTopInventory() == settingsInventory(sp))) return;
		if (e.getCurrentItem() == null) return;
		e.setCanceled(true);
		Inventory gui = view.getTopInventory();

		toggleSetting(sp, gui, e.getCurrentItem(), e.getSlot());
		return;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player p = (Player) sender;

		p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 3F, 1F);
		p.openInventory(settingsInventory(SilverPlayer.fromPlayer(sp)));

		return true;
	}

}