package us.teaminceptus.silverskillz.commands;

import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.language.Language;

import java.io.File;

public final class ReloadConfig {
    protected final SilverSkillz plugin;

    public ReloadConfig(SilverSkillz plugin) {
        this.plugin = plugin;
        plugin.getHandler().register(this);
    }

    @Command({"skillsreload", "silverreload", "sreload", "skreload"})
    @Usage("/sreload")
    @Description("Reloads SilverSkillz Configuration & Languages")
    @CommandPermission("silverskillz.command.skreload")
    public void reload(CommandSender sender) {
	    sender.sendMessage(SilverConfig.getMessage("response.reloading"));
	    plugin.reloadConfig();

        for (Language l : Language.values()) {
            String prefix = "silverskillz" + ("en".equals(l.getIdentifier()) ? "" : "_" + l.getIdentifier()) + ".properties";
            if (!(new File(plugin.getDataFolder(), prefix).exists())) plugin.saveResource(prefix, true);
        }

	    plugin.saveConfig();
	    sender.sendMessage(SilverConfig.getMessage("response.reload_success"));
    }
}