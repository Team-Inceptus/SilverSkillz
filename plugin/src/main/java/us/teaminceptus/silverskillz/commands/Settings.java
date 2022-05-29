package us.teaminceptus.silverskillz.commands;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.SkillUtils;

import java.io.IOException;

public final class Settings implements Listener {

    private static final Material ON = Material.LIME_CONCRETE;
    private static final Material OFF = Material.RED_CONCRETE;
    protected SilverSkillz plugin;

    private static String off;
    private static String on;

    public Settings(SilverSkillz plugin) {
        this.plugin = plugin;
        plugin.getHandler().register(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void onEnable() {
        off = SilverConfig.getConstant("constants.off");
        on = SilverConfig.getConstant("constants.on");
    }

    private static class SettingsHolder implements InventoryHolder {
        public Inventory getInventory() {return null;}
    }

    public static Inventory settingsInventory(SilverPlayer p) {
        Plugin plugin = SilverConfig.getPlugin();
        Inventory inv = SkillUtils.generateGUI(45, SilverConfig.getConstant("title.settings"), new SettingsHolder());

        ItemStack potionEffects = new ItemStack((p.hasPotionEffects() ? ON : OFF));
        ItemMeta pMeta = potionEffects.getItemMeta();
        pMeta.setDisplayName(SilverConfig.getConstant("constants.item.potions") + (p.hasPotionEffects() ? on : off));
        pMeta.setLocalizedName("potion-effects");
        pMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "og"), PersistentDataType.STRING, SilverConfig.getConstant("constants.item.potions"));
        potionEffects.setItemMeta(pMeta);

        ItemStack abilities = new ItemStack((p.hasAbilities() ? ON : OFF));
        ItemMeta aMeta = abilities.getItemMeta();
        aMeta.setDisplayName(SilverConfig.getConstant("constants.item.abilities") + (p.hasAbilities() ? on : off));
        aMeta.setLocalizedName("abilities");
        aMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "og"), PersistentDataType.STRING, SilverConfig.getConstant("constants.item.abilities"));
        abilities.setItemMeta(aMeta);

        ItemStack notifications = new ItemStack((p.hasAbilities() ? ON : OFF));
        ItemMeta nMeta = notifications.getItemMeta();
        nMeta.setDisplayName(SilverConfig.getConstant("constants.item.notifications") + (p.hasNotifications() ? on : off));
        nMeta.setLocalizedName("messages");
        nMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "og"), PersistentDataType.STRING, SilverConfig.getConstant("constants.item.notifications"));
        notifications.setItemMeta(nMeta);

        inv.setItem(10, potionEffects);
        inv.setItem(11, abilities);
        inv.setItem(12, notifications);

        return inv;
    }

    private void toggleSetting(SilverPlayer p, Inventory inv, ItemStack item, int slot) {
        Plugin plugin = SilverConfig.getPlugin();
        ItemMeta meta = item.getItemMeta();
        Material m = item.getType();
        if (m != ON && m != OFF) return;

        String setting = meta.getLocalizedName();
        boolean newValue = !p.getPlayerConfig().getConfigurationSection("settings").getBoolean(setting);
        FileConfiguration f = p.getPlayerConfig();
        f.getConfigurationSection("settings").set(setting, newValue);

        try {
            f.save(p.getPlayerFile());
        } catch (IOException e) {
            plugin.getLogger().severe("Error toggling setting");
            e.printStackTrace();
        }

        String newName = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "og"), PersistentDataType.STRING);
        ItemStack newItem = item.clone();
        newItem.setType(item.getType() == ON ? OFF : ON);
        ItemMeta newMeta = newItem.getItemMeta();
        newMeta.setDisplayName(ChatColor.YELLOW + newName + (item.getType() == ON ? off : on));
        newItem.setItemMeta(newMeta);

        inv.setItem(slot, newItem);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        InventoryView view = e.getView();
        SilverPlayer sp = new SilverPlayer(p);
        if (!(view.getTopInventory().getHolder() instanceof SettingsHolder)) return;
        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);
        Inventory gui = view.getTopInventory();

        toggleSetting(sp, gui, e.getCurrentItem(), e.getSlot());
    }

    @Command({"silverpsettings", "spsett", "spsettings", "splayersettings"})
    @Description("Opens SilverSkillz Player Settings")
    @Usage("/spsett")
    public void openSettings(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 3F, 1F);
        p.openInventory(settingsInventory(new SilverPlayer(p)));
    }


}
