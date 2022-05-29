package us.teaminceptus.silverskillz.artifact;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.artifact.Artifact;

import static us.teaminceptus.silverskillz.api.artifact.Artifact.values;

public final class ArtifactUtils implements Listener {

    protected SilverSkillz plugin;

    public ArtifactUtils(SilverSkillz plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        for (Artifact a : values()) {
            if (a.getEventClass().isInstance(e) && e.getDamager() instanceof Player) {
                a.getFunction().accept(e);
            }
        }
    }

}
