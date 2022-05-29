package us.teaminceptus.silverskillz.api.skills;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;

import java.util.*;

/**
 * Class used to represent abilities
 */
public enum Ability {

    /**
     * Ability to throw your weapon
     */
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
    POISONOUS(3, Skill.BREWER, 20, ChatColor.DARK_GREEN + "Poisonous", Material.VINE,
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
    TOUGH_SKIN(7, Skill.CLEANER, 60, ChatColor.DARK_PURPLE + "Tough Skin", Material.IRON_CHESTPLATE,
            new String[] {
                    "Gain a very large",
                    "amount of resistance",
                    "from your hard work at cleaning!"
            },
            new PotionEffect[] {
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 30, 199, true),
                    new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 30, 2, true)
            }, 20 * 240),
    TELEKENESIS(8, Skill.COLLECTOR, 40, ChatColor.DARK_AQUA + "Telekenesis", Material.ENDER_EYE,
            new String[] {
                    ChatColor.GOLD + "Automatically" + ChatColor.GRAY + " pick up items",
                    "mined from you and your abilities!",
            }),
    TIMBER(9, Skill.SMITHING, 35, ChatColor.BLUE + "Timber", Material.DIAMOND_AXE,
            new String[] {
                    "Mine an entire tree",
                    "down easily with this",
                    "ability!"
            }),
    CLOVER(10, Skill.ADVANCER, 25, ChatColor.GREEN + "Clover", Material.OXEYE_DAISY,
            new String[] {
                    "Apply some very useful",
                    "luck and increase your",
                    "chances of finding better",
                    "loot!"
            },
            new PotionEffect[] {
                    new PotionEffect(PotionEffectType.LUCK, 20 * 60, 9, true)
            }, 20 * 180),
    CROSS_WORLD(11, Skill.TRAVELER, 25, ChatColor.DARK_BLUE + "Cross World", Material.IRON_BOOTS,
            new String[] {
                    "Gain some speed and",
                    "travel very long",
                    "distances!"
            },
            new PotionEffect[] {
                    new PotionEffect(PotionEffectType.SPEED, 20 * 30, 7, true)
            }, 20 * 90),
    SUPER_SPONGE(12, Skill.AQUATICS, 45, ChatColor.YELLOW + "Super Sponge", Material.SPONGE,
            new String[] {
                    "Water and Lava that come",
                    "near you will cease to exist!"
            }, 20 * 30, 20 * 60),


    ;

    private final int id;

    private final Action action;
    private final String name;
    private final PotionEffect[] effects;
    private final long cooldownTicks;
    private final long durationTicks;
    private final Skill skill;
    private final int unlockLevel;

    private final AbilityType type;

    private final List<String> description;
    private final Material icon;

    private static final Map<Ability, List<UUID>> cooldown = new HashMap<>();

    Ability(int id, Skill skill, int skillLevel, String name, Action action, Material icon, String[] description, PotionEffect[] effects, long cooldownTicks) {
        this.id = id;
        this.name = name;
        this.effects = effects;
        this.cooldownTicks = cooldownTicks;
        this.skill = skill;
        this.action = action;
        this.unlockLevel = skillLevel;

        List<String> desc = new ArrayList<>();
        for (String s : description) desc.add(ChatColor.GRAY + s);
        this.description = desc;

        this.icon = icon;
        this.durationTicks = -1;
        this.type = AbilityType.ACTION_POTION_COOLDOWN;
    }

    Ability(int id, Skill skill, int skillLevel, String name, Material icon, String[] description, long durationTicks, long cooldownTicks) {
        this.id = id;
        this.name = name;
        this.effects = null;
        this.cooldownTicks = cooldownTicks;
        this.skill = skill;
        this.action = null;
        this.unlockLevel = skillLevel;

        List<String> desc = new ArrayList<>();
        for (String s : description) desc.add(ChatColor.GRAY + s);
        this.description = desc;

        this.icon = icon;
        this.durationTicks = durationTicks;
        this.type = AbilityType.DURATION_COOLDOWN;
    }

    Ability(int id, Skill skill, int skillLevel, String name, Action action, Material icon, String[] description) {
        this.id = id;
        this.name = name;
        this.effects = null;
        this.cooldownTicks = -1;
        this.skill = skill;
        this.action = action;
        this.unlockLevel = skillLevel;

        List<String> desc = new ArrayList<>();
        for (String s : description) desc.add(ChatColor.GRAY + s);
        this.description = desc;

        this.icon = icon;
        this.durationTicks = -1;
        this.type = AbilityType.ACTION;
    }

    Ability(int id, Skill skill, int skillLevel, String name, Material icon, String[] description) {
        this.id = id;
        this.name = name;
        this.effects = null;
        this.cooldownTicks = -1;
        this.skill = skill;
        this.action = null;
        this.unlockLevel = skillLevel;

        List<String> desc = new ArrayList<>();
        for (String s : description) desc.add(ChatColor.GRAY + s);
        this.description = desc;

        this.icon = icon;
        this.durationTicks = -1;
        this.type = AbilityType.EVENT;
    }

    Ability(int id, Skill skill, int skillLevel, String name, Material icon, String[] description, PotionEffect[] effects, long cooldownTicks) {
        this.id = id;
        this.name = name;
        this.effects = effects;
        this.cooldownTicks = cooldownTicks;
        this.skill = skill;
        this.action = null;
        this.unlockLevel = skillLevel;

        List<String> desc = new ArrayList<>();
        for (String s : description) desc.add(ChatColor.GRAY + s);
        this.description = desc;

        this.icon = icon;
        this.durationTicks = -1;
        this.type = AbilityType.POTION_COOLDOWN;
    }

    public final ItemStack generateLockedItem() {
        ItemStack locked = new ItemStack(Material.BARRIER);
        ItemMeta lMeta = locked.getItemMeta();
        lMeta.setDisplayName(ChatColor.RED + "This Ability is Locked!");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "To Unlock " + this.name + ChatColor.AQUA + ", you need:");
        lore.add(ChatColor.LIGHT_PURPLE + "- " + ChatColor.GOLD + this.skill.getCapitalizedName() + ChatColor.AQUA + " Level " + this.getLevelUnlocked());
        locked.setItemMeta(lMeta);

        return locked;
    }


    /**
     * Executes this Ability with the option of showing the cooldown message.
     * This will silently fail if the ability is not unlocked, cooldown is active or the player is not online.
     * This will also execute a cooldown if one is present.
     * @param sp Player involved in this ability
     * @param message true if send cooldown message, else false
     * @throws IllegalArgumentException if player is null
     */
    public void execute(@NotNull SilverPlayer sp, boolean message) throws IllegalArgumentException {
        Validate.notNull(sp, "Player cannot be null");
        if (!(this.isUnlocked(sp))) return;
        if (!(sp.getPlayer().isOnline())) return;
        Player p = sp.getOnlinePlayer();

        if (cooldown.get(this).contains(p.getUniqueId())) {
            if (message) p.sendMessage(ChatColor.RED + "This ability is currently on a cooldown!");
            return;
        }

        if (this.type == AbilityType.ACTION_POTION_COOLDOWN || this.type == AbilityType.POTION_COOLDOWN) {
            this.addPotionEffects(p);
        }

        Plugin plugin = SilverConfig.getPlugin();

        switch (this) {
            case SUPER_SPONGE -> {
                World w = p.getWorld();
                Ability a = this;

                new BukkitRunnable() {
                    int i = 0;

                    public void run() {
                        if (i >= a.durationTicks) {
                            cancel();
                            p.sendMessage(ChatColor.YELLOW + "Your ability has worn off!");
                        }

                        for (int x = -3; x < 3; x++) {
                            for (int y = -3; y < 3; y++) {
                                for (int z = -3; z < 3; z++) {
                                    if (w.getBlockAt(x, y, z).getType() == Material.WATER || w.getBlockAt(x, y, z).getType() == Material.LAVA) {
                                        w.getBlockAt(x, y, z).setType(Material.AIR);
                                    }
                                }
                            }
                        }
                        i++;
                    }
                }.runTaskTimer(plugin, 0, 1);
            }
            default -> {}
        }


        if (this.cooldownTicks != -1) { // -1 for no cooldown
            cooldown.get(this).add(p.getUniqueId());
            Ability a = this;
            new BukkitRunnable() {
                public void run() {
                    cooldown.get(a).remove(p.getUniqueId());
                }
            }.runTaskLater(plugin, this.cooldownTicks);
        }
    }

    /**
     * Execute the Ability with no message.
     * This will silently fail if the ability is not unlocked, or a cooldown is active.
     * This will also execute a cooldown if one is present.
     * @param sp Player involved in this ability
     * @throws IllegalArgumentException if player is null
     */
    public final void execute(@NotNull SilverPlayer sp) throws IllegalArgumentException {
        execute(sp, false);
    }

    /**
     * Get the Action needed to trigger this ability
     * @return Action to trigger, may be null
     */
    public final Action getAction() {
        return this.action;
    }

    /**
     * Get the Description of this Ability
     * @return Description of Ability, with ChatColors
     */
    public final List<String> getDescription() {
        return this.description;
    }

    /**
     * Get the PotionEffects involved in this ability
     * @return Array of PotionEffects, may be null
     */
    public final PotionEffect[] getPotionEffects() {
        return this.effects;
    }

    public final String getName() {
        return ChatColor.stripColor(this.name);
    }

    /**
     * Get the Type of Ability this one is used for
     * @return Type of Ability
     */
    public final AbilityType getType() {
        return this.type;
    }

    /**
     * Get the amount of time this ability will last
     * @return Amount of ticks ability will last, -1 if not used
     */
    public final long getDuration() {
        return this.durationTicks;
    }

    /**
     * Fetch the ability's icon
     * @return Ability Icon
     */
    public final Material getIcon() {
        return this.icon;
    }

    /**
     * Convienence Method
     * @return Ability Icon as ItemStack
     */
    public final ItemStack getIconAsStack() {
        return new ItemStack(this.icon);
    }

    /**
     * Duplicate of getName
     */
    public String toString() {
        return getName();
    }

    /**
     * Fetch PotionEffects attacked to this ability. Can Be Null.
     * @return PotionEffects attatched to this ability
     */
    public final PotionEffect[] getEffects() {
        return this.effects;
    }

    /**
     * Get if the player has unlocked this ability.
     * @param sp Player to Compare
     * @return true if unlocked, else false
     */
    public final boolean isUnlocked(SilverPlayer sp) {
        return sp.getSkill(this.skill).getLevel() >= this.unlockLevel;
    }

    /**
     * Get the Skill associated with this ability.
     * @return Skill with this ability.
     */
    public final Skill getSkill() {
        return this.skill;
    }

    /**
     * Fetch Ability by its Id
     * @param id ID to compare
     * @return Ability, if found
     */
    public static Ability getById(int id) {
        for (Ability a : values()) {
            if (a.getId() == id) return a;
        }

        return null;
    }

    /**
     * Get the Ability's Unique Identifier
     * @return Integer ID of this Ability
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Generate an ItemStack used in the list
     * @return ItemStack used in menu
     */
    public final ItemStack generateItemStack() {
        ItemStack item = getIconAsStack();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.name);

        List<String> itemDesc = new ArrayList<>(this.description);

        itemDesc.add(" ");
        itemDesc.add(ChatColor.DARK_GRAY + "" + ChatColor.UNDERLINE + "Information");
        if (this.cooldownTicks != -1) itemDesc.add(ChatColor.GRAY + "Cooldown: " + ChatColor.RED + (double) this.cooldownTicks / 20 + "s");
        itemDesc.add(ChatColor.GRAY + "Unlocked At: " + ChatColor.AQUA + this.skill.getCapitalizedName() + ChatColor.GOLD + " Level " + this.unlockLevel);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Get the level minimum to use this ability.
     * @return int of level required
     */
    public final int getLevelUnlocked() {
        return this.unlockLevel;
    }

    /**
     * Convienence Method to add PotionEffects
     * @param p Player to add effects to
     */
    public final void addPotionEffects(Player p) {
        if (this.effects == null) return;

        p.addPotionEffects(Arrays.asList(this.effects));
    }

    public enum AbilityType {

        ACTION_POTION_COOLDOWN(0),
        DURATION_COOLDOWN(1),
        ACTION(2),
        EVENT(3),
        POTION_COOLDOWN(4)
        ;

        private final int id;

        AbilityType(int id) {
            this.id = id;
        }

        public final int getId() {
            return this.id;
        }

        public static AbilityType getById(int id) {
            for (AbilityType t : values()) {
                if (t.id == id) return t;
            }

            return null;
        }
    }

}
