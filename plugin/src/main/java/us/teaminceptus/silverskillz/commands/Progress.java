package us.teaminceptus.silverskillz.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.Skill;

@Command({"progress", "prog"})
public final class Progress {

    protected SilverSkillz plugin;

    public Progress(SilverSkillz plugin) {
        this.plugin = plugin;
        plugin.getHandler().register(this, new ProgressAliases(this));
    }

    @Subcommand({"set", "setpoints"})
    @Usage("/setprogress <target> [amount] [skill]")
    @Description("Sets progress for another Player.")
    @CommandPermission("silverskillz.command.setprogress")
    public void set(CommandSender sender, SilverPlayer target, @Default("100") double amount, @Optional Skill skill) {
        if (skill != null) {
            target.getSkill(skill).setProgress(amount);
            sender.sendMessage(SilverConfig.getMessage("response.set"));
        } else {
            for (Skill s : Skill.values()) target.getSkill(s).setProgress(amount);
            sender.sendMessage(SilverConfig.getMessage("response.set_all"));
        }
    }

    @Subcommand({"add", "addpoints"})
    @Usage("/addprogress <target> [amount] [skill]")
    @Description("Adds progress to another Player.")
    @CommandPermission("silverskillz.command.addprogress")
    public void add(CommandSender sender, SilverPlayer target, @Default("100") double amount, @Optional Skill skill) {
        if (skill != null) {
            target.getSkill(skill).addProgress(amount);
            sender.sendMessage(SilverConfig.getMessage("response.increase"));
        } else {
            for (Skill s : Skill.values()) target.getSkill(s).addProgress(amount);
            sender.sendMessage(SilverConfig.getMessage("response.increase_all"));
        }
    }

    @Subcommand({"remove", "rm", "removepoints"})
    @Usage("/removeprogress <target> [amount] [skill]")
    public void remove(CommandSender sender, SilverPlayer target, @Default("100") double amount, @Optional Skill skill) {
        if (skill != null) {
            target.getSkill(skill).removeProgress(amount);
            sender.sendMessage(SilverConfig.getMessage("response.decrease"));
        } else {
            for (Skill s : Skill.values()) target.getSkill(s).removeProgress(amount);
            sender.sendMessage(SilverConfig.getMessage("response.decrease_all"));
        }
    }

    @Subcommand("reset")
    @Usage("/resetprogress <target> [skill]")
    @Description("Resets a Player's Progress.")
    @CommandPermission("silverskillz.command.resetprogress")
    public void reset(CommandSender sender, SilverPlayer target, @Optional Skill skill) {
        if (skill != null) {
            target.getSkill(skill).setProgress(0);
            sender.sendMessage(SilverConfig.getMessage("response.reset"));
        } else {
            for (Skill s : Skill.values()) target.getSkill(s).setProgress(0);
            sender.sendMessage(SilverConfig.getMessage("response.reset_all"));
        }
    }

}
