package us.teaminceptus.silverskillz.artifact;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Recipe;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.artifact.Artifact;

import static us.teaminceptus.silverskillz.api.artifact.Artifact.values;

public final class ArtifactUtils implements Listener {

    protected final SilverSkillz plugin;

    public ArtifactUtils(SilverSkillz plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }



    private static boolean checkItem(Player p, Artifact a) {
        return checkItem(p, a, EquipmentSlot.HAND);
    }

    private static boolean checkItem(Player p, Artifact a, EquipmentSlot s) {
        return p.getEquipment().getItem(s).hasItemMeta() &&
                p.getEquipment().getItem(s).getItemMeta().hasLocalizedName() &&
                p.getEquipment().getItem(s).getItemMeta().getLocalizedName().equals(a.getRecipe().getResult().getItemMeta().getLocalizedName());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        for (Artifact a : values()) if (a.getEventClass().isInstance(e) && e.getDamager() instanceof Player p && checkItem(p, a)) a.getFunction().accept(e);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        for (Artifact a : values()) if (a.getEventClass().isInstance(e) && checkItem(e.getPlayer(), a, e.getHand())) a.getFunction().accept(e);
    }

    private static boolean compare(Recipe r, Recipe r2) {
        if (!(r instanceof Keyed k)) return false;
        if (!(r2 instanceof Keyed k2)) return false;

        return k.getKey().getNamespace().equals(k2.getKey().getNamespace()) && k.getKey().getKey().equals(k2.getKey().getKey());
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        if (!(e.getViewers().get(0) instanceof Player p)) return;

        Recipe r = e.getRecipe();
        SilverPlayer pl = new SilverPlayer(p);
        for (Artifact a : Artifact.values()) {
            if (pl.getSkill(a.getSkill()).getLevel() < a.getLevelUnlocked() && compare(r, e.getRecipe())) e.getInventory().setResult(null);
        }
    }

}
