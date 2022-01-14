package us.teaminceptus.silverskillz.premium;

public class PremiumCommands {

	public static void register() {
		try {
			SilverSkillz plugin = JavaPlugin.getPlugin(SilverSkillz.class);
			Field bukkitmap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			bukkitmap.setAccessible(true);

			CommandMap map = (CommandMap) bukkitmap.get(Bukkit.getServer());

			PluginCommand abilities = new PluginCommand("abilities", plugin);
			abilities.setExecutor(new Abilities(plugin));
			abilities.setAliases(Arrays.asList("abilitieslist", "abilitylist", "alist", "al"));
			abilities.setDescription("List your abilities gained from skills.");
			
			map.register(abilities);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
}