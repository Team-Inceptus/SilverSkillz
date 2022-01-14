package us.teaminceptus.silverskillz.premium.skills;

/**
* Class used to represent abilities
*/
public enum Ability {

	WEAPON_THROW(0, Skill.COMBAT, 55, ChatColor.DARK_RED + "Weapon Throw", Action.RIGHT_CLICK_AIR, Material.DIAMOND_AXE,
							new String[] {
								"Throw a sword or an axe",
								ChatColor.GREEN + "20" + ChatColor.GRAY + " blocks in front of you",
								"and damage your enemies!"
							}),
	BERSERK(1, Skill.COMBAT, 25, ChatColor.RED + "Berserk", Material.IRON_AXE,
				 			new String[] {
								"Gain strength and",
								"speed to destroy your",
								"enemies!"
							},
				 			new PotionEffect[] {
								new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 4, true),
								new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60, 3, true),
								new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 30, 1, true),
								new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 2, true),
							}, 20 * 120),
	VEIN_MINER(2, Skill.MINING, 35, ChatColor.AQUA + "Vein Miner", Material.GOLDEN_PICKAXE,
						new String[] {
							"Mine an " + ChatColor.GREEN + "entire vein",
							"in an instant!"
						}),
	POISONOUS(3, Skill.BREWER, 20, ChatColor.DARK_GREEM + "Poisonous", Material.VINE,
					 new String[] {
						 "Poison your enemies on",
						 "hit, and when you're damaged!"
					 }),
	WITHERING(4, Skill.BREWER, 50, ChatColor.DARK_GRAY + "Withering", Material.WITHER_SKELETON_SKULL,
					 new String[] {
						 "Double the debuff! Add",
						 "withering to your enemies on",
						 "offense and defense!"
					 }),
	HOMING(5, Skill.ARCHERY, 70, ChatColor.GOLD + "Homing", Material.BOW,
					new String[] {
						"Never miss your shots!",
						"Any projectile you shoot will",
						ChatColor.GOLD + "automatically" + ChatColor.GRAY + " aim towards",
						"the nearest target!"
					}),
	SEA_AURA(6, Skill.AQUATICS, 65, ChatColor.DARK_AQUA + "Sea Aura", Material.CONDUIT,
					new String[] {
						ChatColor.DARK_AQUA + "Drowned and Dolphins",
						"will not attack you."
					}),
	
	
	;

	private final int id;
	
	private Action action;
	private final String name;
	private final PotionEffect[] effects;
	private final long cooldownTicks;
	private final Skill skill;
	private final short unlockLevel;
	
	private final List<String> description;
	private final Material icon;
	
	public Ability(int id, Skill skill, short skillLevel, String name, Action action, Material icon, String[] description, PotionEffect[] effects, long cooldownTicks) {
		this.id = id;
		this.name = name;
		this.effects = effects;
		this.cooldownTicks = cooldownTicks;
		this.skill = skill;
		if (action != null) this.action = action;
		this.unlockLevel = skillLevel;

		List<String> desc = new ArrayList<>();
		for (String s : description) desc.add(ChatColor.GRAY + s);
		this.description = desc;
		
		this.icon = icon;
	}

	public Ability(int id, Skill skill, short skillLevel, String name, Action action, Material icon, String[] description) {
		this(id, skill, skillLevel, name, action, icon, description, null, -1);
	}

	public Ability(int id, Skill skill, short skillLevel, String name, Material icon, String[] description) {
		this(id, skill, skillLevel, name, null, icon, description);
	}

	public Ability(int id, Skill skill, short skillLevel, String name, Material icon, String[] description, PotionEffect[] effects, long cooldownTicks) {
		this(id, skill, skillLevel, name, null, icon, description, effects, cooldownTicks);
	}

	public final Action getAction() {
		return this.action;
	}

	public final List<String> getDescription() {
		return this.description;
	}
	
	public final String getName() {
		return ChatColor.stripColor(this.name);
	}

	public final Material getIcon() {
		return this.icon;
	}

	public final ItemSack getIconAsStack() {
		return new ItemStack(this.icon);
	}

	public String toString() {
		return getName();
	}

	public final boolean equals(Object obj) {
		if (!(obj instanceof Ability a)) return false;
		if (a.getId() == this.id) return true;

		return false;
	}

	public final PotionEffect[] getEffects() {
		return this.effects;
	}

	public final boolean isUnlocked(SilverPlayer sp) {
		return sp.getSkill(this.skill).getLevel() >= this.unlockLevel;
	}

	public final Skill getSkill() {
		return this.skill;
	}

	public static final Ability getById(int id) {
		for (Ability a : values()) {
			if (a.getId() == id) return a;
			else continue;
		}

		return null;
	}

	public final int getId() {
		return this.id;
	}

	public final ItemStack generateItemStack() {
		ItemStack item = new ItemStack(this.icon);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.name);
		
		List<String> itemDesc = new ArrayList<>();
		for (String s : this.description) itemDesc.add(s);

		itemDesc.add(" ");
		itemDesc.add(ChatColor.DARK_GRAY + "" + ChatColor.UNDERLINE + "Information");
		if (this.durationTicks != -1) itemDesc.add(ChatColor.GRAY + "Duration: " + ChatColor.AQUA + Double.toString(this.durationTicks / 20) + "s");
		if (this.cooldownTicks != -1) itemDesc.add(ChatColor.GRAY + "Cooldown: " + ChatColor.RED + Double.toString(this.cooldownTicks / 20) + "s");
		itemDesc.add(ChatColor.GRAY + "Unlocked At: " + ChatColor.AQUA + this.skill.getCapitalizedName() + ChatColor.GOLD + " Level " + Short.toString(this.unlockLevel));
		item.setItemMeta(meta);
		return item;
	}

	public final short getLevelUnlocked() {
		return this.unlockLevel;
	}

	public final void addPotionEffects(Player p) {
		if (this.effects == null) return;
		p.addPotionEffects(Arrays.asList(this.effects));
	}

	

}