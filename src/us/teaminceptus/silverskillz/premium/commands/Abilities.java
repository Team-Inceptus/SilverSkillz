package us.teaminceptus.silverskillz.premium.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import us.teaminceptus.silverskillz.SilverPlayer;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.premium.skills.Ability;
import us.teaminceptus.silverskillz.skills.SkillUtils;

class Abilities implements CommandExecutor {

	protected SilverSkillz plugin;
	
	protected Abilities(SilverSkillz plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player p)) return false;

		SilverPlayer sp = new SilverPlayer(p);
		
		Inventory gui = SkillUtils.generateGUI(45, ChatColor.DARK_AQUA + "Player Abilities");
		
		for (Ability a : Ability.values()) gui.addItem(a.isUnlocked(sp) ? a.generateItemStack() : a.generateLockedItem());
		
		p.openInventory(gui);
		return true;
	}
	
}