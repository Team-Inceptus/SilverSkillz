package us.teaminceptus.silverskillz.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.Skill;

public final class ProgressAliases {

    private final Progress p;

    public ProgressAliases(Progress p) {
        this.p = p;
    }

    @Command({"setprogress", "setprog"})
    @Usage("/setprogress <target> [amount] [skill]")
    @Description("Sets progress for another Player.")
    @CommandPermission("silverskillz.command.addprogress")
    public void set(CommandSender sender, SilverPlayer target, @Default("100") double amount, @Optional Skill skill) {
        p.set(sender, target, amount, skill);
    }

    @Command({"addprogress", "addprog"})
    @Usage("/addprogress <target> [amount] [skill]")
    @Description("Adds progress to another Player.")
    @CommandPermission("silverskillz.command.addprogress")
    public void add(CommandSender sender, SilverPlayer target, @Default("100") double amount, @Optional Skill skill) {
        p.add(sender, target, amount, skill);
    }

    @Command({"removeprogress", "removeprog"})
    @Usage("/removeprogress <target> [amount] [skill]")
    public void remove(CommandSender sender, SilverPlayer target, @Default("100") double amount, @Optional Skill skill) {
        p.remove(sender, target, amount, skill);
    }

    @Command({"resetprogress", "resetprog"})
    @Usage("/resetprogress <target> [skill]")
    @Description("Resets a Player's Progress.")
    @CommandPermission("silverskillz.command.resetprogress")
    public void reset(CommandSender sender, SilverPlayer target, @Optional Skill skill) {
        p.reset(sender, target, skill);
    }

}
