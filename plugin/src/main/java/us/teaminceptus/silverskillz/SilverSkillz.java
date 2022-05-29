package us.teaminceptus.silverskillz;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.exception.CommandErrorException;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.language.Language;
import us.teaminceptus.silverskillz.api.skills.Skill;
import us.teaminceptus.silverskillz.commands.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
 * Main Plugin class for SilverSkillz
 * @author GamerCoder215
 *
 */
public final class SilverSkillz extends JavaPlugin implements SilverConfig, Listener {

	private BukkitCommandHandler handler;

	private void setupLamp() {
		this.handler = BukkitCommandHandler.create(this);

		handler.registerValueResolver(Skill.class, ctx -> Skill.valueOf(ctx.popForParameter().toUpperCase()));
		handler.getAutoCompleter().registerParameterSuggestions(Skill.class, SuggestionProvider.of(toStringList(s -> s.name().toLowerCase(), Skill.values())));

		handler.registerValueResolver(SilverPlayer.class, ctx -> {
			UUID uid = InternalUtil.nameToUUID(ctx.popForParameter());
			if (uid == null) throw new CommandErrorException("Player does not exist");
			if (Bukkit.getOfflinePlayer(uid) == null) throw new CommandErrorException("Player does not exist");
			return new SilverPlayer(Bukkit.getOfflinePlayer(uid));
		});
		handler.getAutoCompleter().registerParameterSuggestions(SilverPlayer.class, SuggestionProvider.of(toStringList(OfflinePlayer::getName, Bukkit.getOfflinePlayers())));

		new Settings(this);
		new Progress(this);
		new Skills(this);
		new ReloadConfig(this);

		handler.registerBrigadier();
	}

	public static <T> List<String> toStringList(Function<T, String> func, List<T> elements) {
		List<String> list = new ArrayList<>();
		for (T element : elements) list.add(func.apply(element));

		return list;
	}

	public static @SafeVarargs <T> List<String> toStringList(Function<T, String> func, T... elements) {
		return toStringList(func, Arrays.asList(elements));
	}

	public BukkitCommandHandler getHandler() {
		return this.handler;
	}

	public void onEnable() {
		getLogger().info("Loading Config...");
		saveDefaultConfig();
		saveConfig();

		getLogger().info("Loading Languages...");
		loadLanguages();

		getLogger().info("Loading Commands...");
		setupLamp();

		getLogger().info("Loading options...");
		loadEffects();

		getLogger().info("Complete!");
	}

	private void loadLanguages() {
		for (Language l : Language.values()) {
			String id = "silverskillz" + (l.getIdentifier().length() == 0 ? "" : "_" + l.getIdentifier()) + ".properties";
			if (!(new File(getDataFolder(), id).exists())) saveResource(id, false);
		}
	}

	private void loadEffects() {
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					SilverPlayer sp = new SilverPlayer(p);
					if (!(sp.hasPotionEffects())) return;

					int hLevel = sp.getSkill(Skill.HUSBANDRY).getLevel();
					int aLevel = sp.getSkill(Skill.AQUATICS).getLevel();

					if (hLevel >= 25 && hLevel < 50) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 0, true, false, false));
					} else if (hLevel >= 50 && hLevel < 75) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 1, true, false, false));
					} else if (hLevel >= 75 && hLevel < 100) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 2, true, false, false));
					} else if (hLevel == 100) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 2000000, 3, true, false, false));
					}

					if (aLevel > 50) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 200000, 1, true, false, false));
					}
				}
			}
		}.runTaskTimer(this, 0, 4);
	}

	// Config Impl

	@Override
	public String getCurrentLanguage() {
		return getConfig().getString("Language", "en");
	}

	@Override
	public boolean hasNotifications() { return getConfig().getBoolean("Notifications", true); }

}