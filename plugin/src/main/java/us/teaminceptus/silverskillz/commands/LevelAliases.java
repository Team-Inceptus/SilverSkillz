package us.teaminceptus.silverskillz.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.Skill;

public class LevelAliases {

    private final Level l;

    public LevelAliases(Level l) {
        this.l = l;
    }

    @Command({"setlevel", "setl"})
    @Usage("/setlevel <target> [amount] [skill]")
    @Description("Sets progress for another Player.")
    @CommandPermission("silverskillz.command.setprogress")
    public void set(CommandSender sender, SilverPlayer target, @Range(min = 1, max = 100) int level, @Optional Skill skill) {
        l.set(sender, target, level, skill);
    }

}
