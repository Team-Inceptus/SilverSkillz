package us.teaminceptus.silverskillz.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.Skill;

@Command({"level", "lvl"})
public final class Level {

    protected SilverSkillz plugin;

    public Level(SilverSkillz plugin) {
        this.plugin = plugin;
        plugin.getHandler().register(this, new LevelAliases(this));
    }

    @Subcommand({"set", "setpoints"})
    @Usage("/setlevel <target> [amount] [skill]")
    @Description("Sets progress for another Player.")
    @CommandPermission("silverskillz.command.setprogress")
    public void set(CommandSender sender, SilverPlayer target, @Range(min = 1, max = 100) int level, @Optional Skill skill) {
        if (skill != null) {
            target.getSkill(skill).setLevel(level);
            sender.sendMessage(SilverConfig.getMessage("response.set"));
        } else {
            for (Skill s : Skill.values()) target.getSkill(s).setLevel(level);
            sender.sendMessage(SilverConfig.getMessage("response.set_all"));
        }
    }


}
