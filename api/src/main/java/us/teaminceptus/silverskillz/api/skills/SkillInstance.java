package us.teaminceptus.silverskillz.api.skills;

import java.io.IOException;
import java.text.DecimalFormat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;

/**
 * Represents a Skill Instance from SilverPlayer#getSkill(Skill)
 *
 */
public final class SkillInstance {

	private final Skill skill;
	private final SilverPlayer player;

	private double progress;
	private int level;

	/**
	 * Constructs a SkillInstance.
	 * @param s Skill to use
	 * @param p SilverPlayer to use
	 */
	public SkillInstance(Skill s, SilverPlayer p) {
		this.player = p;
		this.skill = s;
		
		if (this.progress == 0 && this.level == 0) {
			this.progress = player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getDouble("progress");
			this.level = player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getInt("level");
		}
	}
	
	/**
	 * The skill involved in this instance
	 * @return The skill involved
	 */
	public Skill getSkill() {
		return this.skill;
	}
	
	/**
	 * The player involved in this instance
	 * @return The player involved
	 */
	public SilverPlayer getPlayer() {
		return this.player;
	}

	private void reload() {
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("level", this.level);
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("progress", this.progress);
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("name", this.skill.getName());
		
		try {
			player.getPlayerConfig().save(player.getPlayerFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.progress = player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getDouble("progress");
		this.level = player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getInt("level");
	}
	
	/**
	 * Fetches the level of this skill, as an integer
	 * @return integer of level
	 */
	public int getLevel() {
		return this.level;
	}

	/**
	 * Fetches the level of this skill, as a double
	 * @return double of level
	 */
	public double getLevelDouble() {
		return getLevel();
	}
	
	/**
	 * Adds progress
	 * @return true if leveled up, else false
	 */
	public boolean addProgress() { return addProgress(1); }
	
	/**
	 * Increments progress by one, option for basic
	 * @param basic Whether or not to use basic for converting to level
	 * @return true if leveled up, else false
	 */
	public boolean addProgress(boolean basic) {
		return addProgress(basic, 1);
	}
	
	/**
	 * Removes progress
	 */
	public void removeProgress() { removeProgress(1); }
	
	/**
	 * Sets the level
	 * <p>The progress will be set to the lowest required for this level</p>
	 * @param level New level to set
	 */
	public void setLevel(int level) {
		setLevel(false, level);
	}
	
	/**
	 * Sets the progress
	 * @param progress The new progress to set
	 */
	public void setProgress(double progress) {
		setProgress(false, progress);
	}
	
	/**
	 * Sets the level
	 * <p>The progress will be the minimum for this level.</p>
	 * @param basic Whether or not to use basic to convert to progress
	 * @param level New level to set
	 */
	public void setLevel(boolean basic, int level) {
		if (level < 0 || level > 100) throw new IllegalArgumentException("Level cannot be greater than 100.");

		this.level = level; 
		this.progress = Skill.toMinimumProgress(basic, level);
		reload();
	}
	
	/**
	 * Sets the new progress
	 * @param basic Whether or not to use basic to convert to level
	 * @param progress Progress to set
	 */
	public void setProgress(boolean basic, double progress) {
		if (progress < 0) throw new IllegalArgumentException("Progress cannot be less than 0.");

		this.progress = progress;
		this.level = Skill.toLevel(basic, progress);
		reload();
	}
	
	/**
	 * Current progress
	 * @return double containing current progress
	 */
	public double getProgress() {
		return this.progress;
	}
	
	/**
	 * Adds progress
	 * @param add The amount to add
	 * @return true if leveled, else false
	 */
	public boolean addProgress(double add) {
		return addProgress(false, add);
	}
	
	/**
	 * Adds progress
	 * @param basic Whether or not to use basic for converting to level
	 * @param add The amount of progress to add
	 * @return true if leveled, else false
	 * @throws IllegalArgumentException if getProgress() + add is greater than {@link Skill#MAX_PROGRESS_VALUE}
	 */
	public boolean addProgress(boolean basic, double add) throws IllegalArgumentException {
		if (getProgress() + add > Skill.MAX_PROGRESS_VALUE) throw new IllegalArgumentException("Progress cannot be greater than 1 billion.");
		this.progress += add;
		boolean hasLeveled = Skill.toLevel(basic, this.progress) != this.level;

		this.level = Skill.toLevel(basic, this.progress);
		awardLevelUp(player, skill, hasLeveled, add);
		reload();
		
		return hasLeveled;
	}

	private static final DecimalFormat df = new DecimalFormat("###.#");

	private static void awardLevelUp(SilverPlayer p, Skill s, boolean hasLeveled, double increaseBy) throws IllegalArgumentException {
		Validate.notNull(p, "Player is null");
		Validate.notNull(s, "Skill is null");

		if (p.getOnlinePlayer() == null) return;
		if (s == Skill.TRAVELER) return;
		if (!(SilverConfig.getConfig().hasNotifications())) return;
		if (!(p.hasNotifications())) return;

		sendActionBar(p.getOnlinePlayer(), String.format(SilverConfig.getConstant("response.actionbar.experience"), df.format(increaseBy), s.getCapitalizedName()));

		if (hasLeveled) {
			Player pl = p.getOnlinePlayer();
			pl.sendTitle(String.format(SilverConfig.getMessage("response.level_up"), s.getCapitalizedName()), "", 5, 100, 10);
		}
	}

	private static void sendActionBar(Player p, final String message) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}
	
	/**
	 * Removes progress
	 * @param remove The amount to remove
	 */
	public void removeProgress(double remove) {
		removeProgress(false, remove);
	}
	
	/**
	 * Removes progress
	 * @param basic Whether or not to use basic to convert to level
	 * @param remove The amount to remove
	 */
	public void removeProgress(boolean basic, double remove) {
		if (getProgress() - remove < 0) throw new IllegalArgumentException("Progress cannot be less than 0.");

		this.progress -= remove;
		this.level = Skill.toLevel(basic, progress);
		reload();
	}
}