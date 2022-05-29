package us.teaminceptus.silverskillz.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.skills.Skill;

public final class Skills {
	
	protected SilverSkillz plugin;
	
	public Skills(SilverSkillz plugin) {
		this.plugin = plugin;
		plugin.getHandler().register(this);
	}
	
	@Command({"skills", "skill"})
	@Usage("/skill")
	@Description("Opens the Skill Menu.")
	@CommandPermission("silverskillz.command.skill")
	public void open(Player p) {
		p.openInventory(Skill.getMenu());
	}

}
