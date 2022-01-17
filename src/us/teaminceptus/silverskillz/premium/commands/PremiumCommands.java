package us.teaminceptus.silverskillz.premium.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import us.teaminceptus.silverskillz.SilverSkillz;

public class PremiumCommands {
	
	private static PluginCommand createCommand(String name) {
		try {
			Constructor<PluginCommand> p = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			p.setAccessible(true);
			
			return p.newInstance(name, JavaPlugin.getPlugin(SilverSkillz.class));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void register() {
		try {
			SilverSkillz plugin = JavaPlugin.getPlugin(SilverSkillz.class);
			Field bukkitmap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitmap.setAccessible(true);

			CommandMap map = (CommandMap) bukkitmap.get(Bukkit.getServer());

			PluginCommand abilities = createCommand("abilities");
			abilities.setExecutor(new Abilities(plugin));
			abilities.setAliases(Arrays.asList("abilitieslist", "abilitylist", "alist", "al"));
			abilities.setDescription("List your abilities gained from skills.");
			
			map.register("abilities", abilities);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}