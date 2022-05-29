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
import us.teaminceptus.silverskillz.skills.SkillAdvancer;

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

	private String prefix;

	private void setupLamp() {
		this.handler = BukkitCommandHandler.create(this);

		handler.registerValueResolver(Skill.class, ctx -> {
			try {
				return Skill.valueOf(ctx.popForParameter().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new CommandErrorException(prefix + "Skill not Found");
			}
		});
		handler.getAutoCompleter().registerParameterSuggestions(Skill.class, SuggestionProvider.of(toStringList(s -> s.name().toLowerCase(), Skill.values())));

		handler.registerValueResolver(SilverPlayer.class, ctx -> {
			UUID uid = InternalUtil.nameToUUID(ctx.popForParameter());
			if (uid == null) throw new CommandErrorException("Player does not exist");
			if (Bukkit.getOfflinePlayer(uid) == null) throw new CommandErrorException(prefix + "Player does not exist");
			return new SilverPlayer(Bukkit.getOfflinePlayer(uid));
		});
		handler.getAutoCompleter().registerParameterSuggestions(SilverPlayer.class, SuggestionProvider.of(toStringList(OfflinePlayer::getName, Bukkit.getOfflinePlayers())));

		new Settings(this);
		Settings.onEnable();
		new Progress(this);
		new Skills(this);
		new ReloadConfig(this);
		new Level(this);

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
		prefix = SilverConfig.getConstant("plugin.prefix");

		getLogger().info("Loading Classes...");
		setupLamp();
		new InternalUtil(this);
		new SkillAdvancer(this);

		getLogger().info("Loading Options & Features...");
		loadEffects();

		getLogger().info("Complete!");
	}

	private void loadLanguages() {
		for (Language l : Language.values()) {
			String id = "silverskillz" + (l.getIdentifier().length() == 0 ? "" : "_" + l.getIdentifier()) + ".properties";
			if (!(new File(getDataFolder(), id).exists())) saveResource(id, false);
		}
	}

	private static final int PD = 5;

	private void loadEffects() {
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					SilverPlayer sp = new SilverPlayer(p);
					if (!(sp.hasPotionEffects())) return;

					int h = sp.getSkill(Skill.HUSBANDRY).getLevel();
					int am = (int) Math.floor((double) h / 25);
					if (h >= 25){
						p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, PD, am - 1, true, false));
						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, PD, am - 1, true, false));
					}

					int a = sp.getSkill(Skill.AQUATICS).getLevel();

					if (a > 50) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, PD, 1, true, false, false));
					}
				}
			}
		}.runTaskTimer(this, 0, 3);
	}

	// Config Impl

	@Override
	public String getCurrentLanguage() {
		return getConfig().getString("Language", "en");
	}

	@Override
	public boolean hasNotifications() { return getConfig().getBoolean("Notifications", true); }

}