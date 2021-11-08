package me.gamercoder215.silverskillz.skills;

import java.io.IOException;

import me.gamercoder215.silverskillz.SilverPlayer;

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

	public final Skill getSkill() {
		return this.skill;
	}

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

	public final short getLevel() {
		return this.level;
	}

	public boolean addProgress() {
		return addProgress(1);
	}
	
	public boolean addProgress(boolean basic) {
		return addProgress(basic, 1);
	}

	public void removeProgress() {
		removeProgress(1);
	}
	public void setLevel(short level) {
		setLevel(false, level);
	}

	public void setProgress(double progress) {
		setProgress(false, progress);
	}
	
	public void setLevel(boolean basic, short level) {
		if (level < 0 || level > 100) throw new IllegalStateException("Level cannot be greater than 100.");

		this.level = level; 
		this.progress = Skill.toMinimumProgress(basic, level);
		reload();
	}
	
	public void setProgress(boolean basic, double progress) {
		if (progress < 0) throw new IllegalStateException("Progress cannot be less than 0.");

		this.progress = progress;
		this.level = Skill.toLevel(basic, progress);
		reload();
	}

	public final double getProgress() {
		return this.progress;
	}

	public boolean addProgress(double add) {
		return addProgress(false, add);
	}
	
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

	public void removeProgress(double remove) {
		removeProgress(false, remove);
	}
	
	public void removeProgress(boolean basic, double remove) {
		if (getProgress() - remove < 0) throw new IllegalStateException("Progress cannot be less than 0.");

		this.progress -= remove;
		this.level = Skill.toLevel(basic, progress);
		reload();
	}
}