package us.teaminceptus.silverskillz.skills;

import java.io.IOException;

import us.teaminceptus.silverskillz.SilverPlayer;

/**
 * Represents a Skill Instance from SilverPlayer#getSkill(Skill)
 * @author GamerCoder215
 *
 */
public class SkillInstance {

	private final Skill skill;
	private final SilverPlayer player;

	private double progress;
	private short level;

	public SkillInstance(Skill s, SilverPlayer p) {
		this.player = p;
		this.skill = s;
		
		if (this.progress == 0 && this.level == 0) {
			this.progress = player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getDouble("progress");
			this.level = (short) player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getInt("level");
		}
	}
	
	/**
	 * The skill involved in this instance
	 * @return The skill involved
	 */
	public final Skill getSkill() {
		return this.skill;
	}
	
	/**
	 * The player involved in this instance
	 * @return The player involved
	 */
	public final SilverPlayer getPlayer() {
		return this.player;
	}

	private final void reload() {
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("level", this.level);
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("progress", this.progress);
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("name", this.skill.getName());
		
		try {
			player.getPlayerConfig().save(player.getPlayerFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.progress = player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getDouble("progress");
		this.level = (short) player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).getInt("level");
	}
	
	/**
	 * The level of this skill
	 * @return short of level
	 */
	public final short getLevel() {
		return this.level;
	}
	
	/**
	 * Adds progress
	 * @return true if leveled up, else false
	 */
	public boolean addProgress() {
		return addProgress(1);
	}
	
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
	public void removeProgress() {
		removeProgress(1);
	}
	
	/**
	 * Sets the level
	 * <p>The progress will be set to the lowest required for this level</p>
	 * @param level New level to set
	 */
	public void setLevel(short level) {
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
	public void setLevel(boolean basic, short level) {
		if (level < 0 || level > 100) throw new IllegalStateException("Level cannot be greater than 100.");

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
		if (progress < 0) throw new IllegalStateException("Progress cannot be less than 0.");

		this.progress = progress;
		this.level = Skill.toLevel(basic, progress);
		reload();
	}
	
	/**
	 * Current progress
	 * @return double containing current progress
	 */
	public final double getProgress() {
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
	 */
	public boolean addProgress(boolean basic, double add) {
		if (getProgress() + add > Skill.MAX_PROGRESS_VALUE) throw new IllegalStateException("Progress cannot be greater than 1 billion.");
		this.progress += add;
		boolean hasLeveled = false;
		
		if (Skill.toLevel(basic, this.progress) != this.level) hasLeveled = true;
		short newLevel = Skill.toLevel(basic, this.progress);
		this.level = newLevel;
		Skill.awardLevelUp(player, skill, hasLeveled, add);
		reload();
		
		return hasLeveled;
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
		if (getProgress() - remove < 0) throw new IllegalStateException("Progress cannot be less than 0.");

		this.progress -= remove;
		this.level = Skill.toLevel(basic, progress);
		reload();
	}
}