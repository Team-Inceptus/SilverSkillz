package us.teaminceptus.silverskillz.api.artifact;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.skills.Skill;

import java.util.function.Consumer;

/**
 * Represents an Artifact
 */
public enum Artifact {

    /**
     * Represents a
     */
    FREEZE_SWORD(0, Skill.COMBAT, 10,
            createRecipe("freeze_sword", builder(Material.DIAMOND_SWORD).setName("&bFreeze Sword").add(Enchantment.DAMAGE_ALL, 2).build()), ChatColor.WHITE,
            EntityDamageByEntityEvent.class, e -> {
                if (!(e.getDamager() instanceof Player p)) return;
                // TODO Finish
            })
    ;

    private final int id;
    private final Skill skill;
    private final int levelUnlocked;
    private final Consumer<Event> function;

    private final Class<? extends Event> clazz;

    private final ChatColor infoCC;

    private final Recipe recipe;

    <T extends Event> Artifact(int id, Skill s, int levelUnlocked, Recipe r, ChatColor rarity, Class<T> clazz, Consumer<T> function) {
        this.id = id;
        this.skill = s;
        this.levelUnlocked = levelUnlocked;
        this.function = (e) -> {
            if (clazz.isInstance(e)) function.accept(clazz.cast(e));
        };

        this.recipe = r;
        this.infoCC = rarity;
        this.clazz = clazz;
    }

    private static ShapedRecipe createRecipe(String value, ItemStack build) {
        return new ShapedRecipe(new NamespacedKey(SilverConfig.getPlugin(), "artifact-" + value), build);
    }

    /**
     * Fetch the color of the Artifact's Information Page.
     * @return Color display
     */
    public ChatColor getInfoColor() {
        return infoCC;
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

        ItemStack item;
        ItemMeta meta;

        ItemBuilder(Material m) {
            this.item = new ItemStack(m);
            this.meta = item.getItemMeta();
        }

        ItemBuilder add(Enchantment ench, int level) {
            meta.addEnchant(ench, level, true);
            return this;
        }

        ItemBuilder add(ItemFlag... flags) {
            meta.addItemFlags(flags);
            return this;
        }

        ItemBuilder add(Attribute a, AttributeModifier m) {
            meta.addAttributeModifier(a, m);
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
