package us.teaminceptus.silverskillz.api.artifact;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.skills.Skill;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

/**
 * Represents an Artifact
 */
public final class Artifact {

    private static final Random r = new Random();

    private static ShapedRecipe createRecipe(String value, String shape, Map<Character, ItemStack> items, ItemStack build) {
        ShapedRecipe r = new ShapedRecipe(new NamespacedKey(SilverConfig.getPlugin(), "artifact-" + value), build);
        r.shape(shape.split("_"));
        for (char c : items.keySet()) r.setIngredient(c, new RecipeChoice.ExactChoice(items.get(c)));

        return r;
    }

    private static String ring(char c) {
        return "III_I" + c + "I_III";
    }

    private static String star(char c) {
        return " I _I" + c + "I_ I ";
    }

    private static Map<Character, ItemStack> map(ItemStack base, char c, ItemStack item) { return Map.of('I', base, c, item); }

    private static Map<Character, ItemStack> map(Material m, char c, ItemStack item) { return map(new ItemStack(m), c, item); }
    private static Map<Character, ItemStack> map(Material m, char c, Material item) { return map(m, c, new ItemStack(item)); }

    public static final Artifact FREEZE_SWORD = new Artifact(0, Skill.COMBAT, 10,
            createRecipe("freeze_sword", ring('S'), map(Material.PACKED_ICE, 'S', Material.DIAMOND_SWORD), builder(Material.DIAMOND_SWORD).setName("&bFreeze Sword").add(Enchantment.DAMAGE_ALL, 2).build()),
            EntityDamageByEntityEvent.class, e -> {
                if (!(e.getEntity() instanceof LivingEntity en)) return;
                en.setFreezeTicks(en.getFreezeTicks() + 20 * (r.nextInt(2) + 3));
            });
    public static final Artifact ZEUS_TRIDENT = new Artifact(1, Skill.COMBAT, 40,
            createRecipe("zeus_trident", star('T'), map(Material.ELYTRA, 'T', Material.TRIDENT), builder(Material.TRIDENT).setName("&eZeus's Trident").addAndHide(Enchantment.IMPALING, 20).add(Enchantment.DAMAGE_ALL, 20).build()),
            EntityDamageByEntityEvent.class, e -> {
                if (!(e.getEntity() instanceof LivingEntity en)) return;
                if (r.nextInt(100) < 40) {
                    en.getWorld().strikeLightning(en.getLocation());
                    if (r.nextBoolean()) en.getWorld().strikeLightning(en.getLocation());
                }
            });
    public static final Artifact POSEIDON_TRIDENT = new Artifact(2, Skill.AQUATICS, 35,
            createRecipe("poseidon_trident", star('T'), map(Material.HEART_OF_THE_SEA, 'T', Material.TRIDENT), builder(Material.TRIDENT).setName("&bPoseidon's Trident").addAndHide(Enchantment.IMPALING, 30).add(Enchantment.DAMAGE_ALL, 10).build()),
            EntityDamageByEntityEvent.class, e -> {
                if (!(e.getEntity() instanceof LivingEntity en)) return;
                if (en.getCategory() == EntityCategory.WATER)
                    if (r.nextInt(100) < 70) en.getWorld().strikeLightning(en.getLocation());
                else
                    if (r.nextInt(100) < 10) en.getWorld().strikeLightning(en.getLocation());

             });
    public static final Artifact HADES_TRIDENT = new Artifact(3, Skill.SMITHING, 30,
            createRecipe("hades_trident", ring('T'), map(Material.ELYTRA, 'T', Material.WITHER_SKELETON_SKULL), builder(Material.TRIDENT).setName("&3Hades's Trident").addAndHide(Enchantment.IMPALING, 10).add(Enchantment.DAMAGE_UNDEAD, 20).add(Enchantment.DAMAGE_ALL, 10).build()),
            EntityDamageByEntityEvent.class, e -> {
                if (!(e.getEntity() instanceof LivingEntity en)) return;
                if (r.nextInt(100) < 20) en.getWorld().strikeLightning(en.getLocation());
                if (en.getCategory() == EntityCategory.UNDEAD || en.getCategory() == EntityCategory.ARTHROPOD)
                    if (r.nextInt(100) < 60) en.getWorld().strikeLightning(en.getLocation());
                else
                    if (r.nextInt(100) < 20) en.getWorld().strikeLightning(en.getLocation());

            });
    public static final Artifact KNOCKBACK_SWORD = new Artifact(4, Skill.SMITHING, 7,
            createRecipe("knockback_sword", ring('S'), map(Material.SHIELD, 'S', Material.GOLDEN_SWORD), builder(Material.GOLDEN_SWORD).setName("&eKnockback Sword").add(Enchantment.KNOCKBACK, 9).build()),
            EntityDamageByEntityEvent.class, e -> {
                if (!(e.getEntity() instanceof LivingEntity en)) return;
                en.setVelocity(e.getDamager().getLocation().getDirection().multiply(r.nextInt(3) * 1.5));
            });

    private static int ender_sword_uses = 0;

    public static final Artifact ENDER_SWORD = new Artifact(5, Skill.CLEANER, 25,
            createRecipe("ender_sword", ring('S'), map(Material.ENDER_EYE, 'S', Material.NETHERITE_SWORD), builder(Material.NETHERITE_SWORD).setName("&dEnder Sword").addAndHide(Enchantment.DAMAGE_ARTHROPODS, 40).add(Enchantment.DAMAGE_ALL, 10).build()),
            PlayerInteractEvent.class, e -> {
                if (!(e.getAction() == Action.RIGHT_CLICK_AIR) && !(e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
                Player p = e.getPlayer();
                if (ender_sword_uses > 10) {
                    p.sendMessage(SilverConfig.getMessage("response.artifact.cooldown"));
                    return;
                }

                Location loc = p.getEyeLocation();
                Location target = loc.add(loc.getDirection());
                if (!target.getBlock().isPassable() || !(target.clone().add(0, -1, 0).getBlock().isPassable())) {
                    p.sendMessage(SilverConfig.getMessage("response.artifact.ender.obstructed"));
                    return;
                }
                target.add(loc.getDirection());
                if (!target.getBlock().isPassable() || !(target.clone().add(0, -1, 0).getBlock().isPassable())) {
                    p.sendMessage(SilverConfig.getMessage("response.artifact.ender.obstructed"));
                    return;
                }
                for (int i = 0; i < 6; i++) {
                    target.add(loc.getDirection());
                    if (!target.getBlock().isPassable() || !(target.clone().add(0, -1, 0).getBlock().isPassable())) {
                        p.teleport(target);
                        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 3F, 1F);
                        break;
                    }
                }
                p.teleport(target);
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 3F, 1F);
                ender_sword_uses++;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ender_sword_uses--;
                    }
                }.runTaskLater(SilverConfig.getPlugin(), 60);
            });

    /**
     * Fetches a list of Artifacts.
     * @return List of Artifacts
     */
    public static Artifact[] values() {
        List<Artifact> values = new ArrayList<>();
        try {
            for (Field f : Artifact.class.getFields()) {
                if (!(Modifier.isStatic(f.getModifiers()))) continue;
                if (!(Modifier.isFinal(f.getModifiers()))) continue;
                if (!(Artifact.class.isInstance(f.get(null)))) continue;

                values.add((Artifact) f.get(null));
            }
            return values.toArray(new Artifact[0]);
        } catch (Exception e) {
            return values.toArray(new Artifact[0]);
        }
    }


    private final int id;
    private final Skill skill;
    private final int levelUnlocked;
    private final Consumer<Event> function;

    private final Class<? extends Event> clazz;

    private final Recipe recipe;

    private <T extends Event> Artifact(int id, Skill s, int levelUnlocked, Recipe r, Class<T> clazz, Consumer<T> function) {
        this.id = id;
        this.skill = s;
        this.levelUnlocked = levelUnlocked;
        this.function = (e) -> {
            if (clazz.isInstance(e)) function.accept(clazz.cast(e));
        };

        this.recipe = r;
        this.clazz = clazz;
    }

    /**
     * Fetches the event class type.
     * @return Event Class Type
     */
    public Class<? extends Event> getEventClass() {
        return clazz;
    }


    /**
     * Fetch the skill associated with this Artifact.
     * @return Skill associated
     */
    @NotNull
    public Skill getSkill() {
        return skill;
    }

    /**
     * Fetch the Recipe used to create this Artifact.
     * @return Recipe used
     */
    @NotNull
    public Recipe getRecipe() {
        return recipe;
    }

    /**
     * Fetch the Level that this Artifact is unlocked on.
     * @return Artifact Unlock
     */
    public int getLevelUnlocked() {
        return levelUnlocked;
    }

    /**
     * Fetch the Function called when this Artifact is interacted.
     * @return Function on interact
     */
    @NotNull
    public Consumer<Event> getFunction() {
        return function;
    }

    /**
     * Fetch the Artifact's Numerical ID.
     * @return Num ID
     */
    public int getId() {
        return id;
    }

    private static ItemBuilder builder(Material m) {
        return new ItemBuilder(m);
    }

    private static class ItemBuilder {

        final ItemStack item;
        final ItemMeta meta;

        ItemBuilder(Material m) {
            this.item = new ItemStack(m);
            this.meta = item.getItemMeta();
        }

        ItemBuilder add(Enchantment ench, int level) {
            meta.addEnchant(ench, level, true);
            return this;
        }

        ItemBuilder addAndHide(Enchantment ench, int level) {
            add(ench, level);
            add(ItemFlag.HIDE_ENCHANTS);
            return this;
        }

        ItemBuilder add(ItemFlag... flags) {
            meta.addItemFlags(flags);
            return this;
        }

        ItemBuilder add(Attribute a, AttributeModifier.Operation op, double amount) {
            UUID uuid = UUID.randomUUID();
            meta.addAttributeModifier(a, new AttributeModifier(uuid, uuid.toString(), amount, op));
            return this;
        }

        ItemBuilder add(Attribute a, AttributeModifier.Operation op, double amount, EquipmentSlot slot) {
            UUID uuid = UUID.randomUUID();
            meta.addAttributeModifier(a, new AttributeModifier(uuid, uuid.toString(), amount, op, slot));
            return this;
        }

        ItemBuilder setName(String display, String localized) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',display));
            meta.setLocalizedName(localized);
            return this;
        }

        ItemBuilder setName(String display) {
            return setName(display, ChatColor.stripColor(display).toLowerCase());
        }

        ItemStack build() {
            item.setItemMeta(meta);
            return this.item;
        }

    }

}
