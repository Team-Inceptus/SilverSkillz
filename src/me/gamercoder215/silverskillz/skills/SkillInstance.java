package me.gamercoder215.silverskillz.skills;

import me.gamercoder215.silverskillz.SilverPlayer;

public class SkillInstance {

	private final Skill skill;
	private final SilverPlayer player;

	private double progress;
	private byte level;

	public SkillInstance(Skill s, SilverPlayer p) {
		this.player = p;
		this.skill = s;

		this.progress = p.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(s.getName()).getInt("progress");
		this.level = (byte) p.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(s.getName()).getInt("level");
	}

	public final Skill getSkill() {
		return this.skill;
	}

	public final SilverPlayer getPlayer() {
		return this.player;
	}

	private void reload() {
		player.getPlayerConfig().getConfigurationSection("skills").getConfigurationSection(skill.getName()).set("progress", this.progress);
		player.getPlayerConfig().getConfigurationSection("level").getConfigurationSection(skill.getName()).set("level", this.level);
		player.getPlayerConfig().getConfigurationSection("name").getConfigurationSection(skill.getName()).set("name", skill.getName());
		player.reloadValues();
	}

	public final byte getLevel() {
		return this.level;
	}

	public boolean addProgress() {
		return addProgress(1);
	}

	public void removeProgress() {
		removeProgress(1);
	}
	public void setLevel(byte level) {
		if (level < 0 || level > 100) throw new IllegalArgumentException("Level cannot be greater than 100.");

		this.level = level; 
		this.progress = Skill.toMinimumProgress(level);
		reload();
	}

	public void setProgress(double progress) {
		if (progress < 0) throw new IllegalArgumentException("Progress cannot be less than 0.");

		this.progress = progress;
		this.level = Skill.toLevel(progress);
		reload();
	}

	public final double getProgress() {
		return this.progress;
	}

	public boolean addProgress(double add) {
		if (getProgress() + add > Skill.MAX_PROGRESS_VALUE) throw new IllegalArgumentException("Progress cannot be greater than 1 billion.");

		this.progress += add;
		
		boolean hasLeveled = false;
		
		if (Skill.toLevel(this.progress) != this.level) hasLeveled = true;
		
		this.level = Skill.toLevel(this.progress);
		reload();
		
		return hasLeveled;
	}

	public void removeProgress(double remove) {
		if (getProgress() - remove < 0) throw new IllegalArgumentException("Progress cannot be less than 0.");

		this.progress -= remove;
		this.level = Skill.toLevel(progress);
		reload();
	}
}