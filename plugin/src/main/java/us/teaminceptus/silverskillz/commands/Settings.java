package us.teaminceptus.silverskillz.commands;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Usage;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.SkillUtils;

import java.io.IOException;

public final class Settings implements Listener {

    protected SilverSkillz plugin;

    private final String invName;

    public Settings(SilverSkillz plugin) {
        this.plugin = plugin;
        plugin.getHandler().register(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.invName = SilverConfig.getConstant("title.settings");
    }


    public static Inventory settingsInventory(SilverPlayer p) {
        Inventory inv = SkillUtils.generateGUI(45, SilverConfig.getConstant("title.settings"));

        ItemStack potionEffects = new ItemStack((p.hasPotionEffects() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
        ItemMeta pMeta = potionEffects.getItemMeta();
        pMeta.setDisplayName(SilverConfig.getConstant("constants.item.potions") + (p.hasPotionEffects() ? SilverConfig.getConstant("constants.on") : SilverConfig.getConstant("constants.off")));
        potionEffects.setItemMeta(pMeta);

        ItemStack abilities = new ItemStack((p.hasAbilities() ? Material.LIME_CONCRETE : Material.RED_CONCRETE));
        ItemMeta aMeta = abilities.getItemMeta();
        aMeta.setDisplayName(SilverConfig.getConstant("constants.item.abilities") + (p.hasAbilities() ? SilverConfig.getConstant("constants.on") : SilverConfig.getConstant("constants.off")));
        abilities.setItemMeta(aMeta);

        inv.setItem(10, potionEffects);
        inv.setItem(11, abilities);

        return inv;
    }

    private static String matchSetting(ItemStack i) {
        String name = ChatColor.stripColor(i.getItemMeta().getDisplayName()).toLowerCase().replace(SilverConfig.getConstant("constants.on").toLowerCase(), "")
                .replace(SilverConfig.getConstant("constants.off").toLowerCase(), "")
                .replace(" ", "")
                .replace(":", "");

        return switch(name) {
            case "potieffects" -> "potion-effects";
            default -> name;
        };
    }

    private final void toggleSetting(SilverPlayer p, Inventory inv, ItemStack item, int slot) {
        if (!(item.getItemMeta().getDisplayName().contains("On")) && !(item.getItemMeta().getDisplayName().contains("Off"))) return;
        String setting = matchSetting(item);
        boolean newValue = !(p.getPlayerConfig().getConfigurationSection("settings").getBoolean(setting));
        p.getPlayerConfig().getConfigurationSection("settings").set(setting, newValue);

        try {
            p.getPlayerConfig().save(p.getPlayerFile());
        } catch (IOException e) {
            plugin.getLogger().info("Error toggling setting");
            e.printStackTrace();
        }

        String newName = ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace("On", "").replace("Off", "");
        ItemStack newItem = item.clone();
        newItem.setType(item.getType() == Material.LIME_CONCRETE ? Material.RED_CONCRETE : Material.LIME_CONCRETE);
        ItemMeta newMeta = newItem.getItemMeta();
        newMeta.setDisplayName(ChatColor.YELLOW + newName + (item.getType() == Material.LIME_CONCRETE ? SilverConfig.getConstant("constants.off") : SilverConfig.getConstant("constants.on")));
        newItem.setItemMeta(newMeta);

        inv.setItem(slot, newItem);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof OfflinePlayer p)) return;
        InventoryView view = e.getView();
        SilverPlayer sp = new SilverPlayer(p);
        if (!(ChatColor.stripColor(view.getTitle()).contains(invName))) return;
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
