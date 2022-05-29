package us.teaminceptus.silverskillz;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.skills.Skill;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static us.teaminceptus.silverskillz.api.skills.SkillUtils.getInventoryPlaceholder;

public class InternalUtil implements Listener {

    SilverSkillz plugin;
    final String inventoryName;

    InternalUtil(SilverSkillz plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.inventoryName = SilverConfig.getConstant("plugin.prefix");
    }


    @EventHandler
    public void damageCalculation(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;

        SilverPlayer sp = new SilverPlayer(p);
        int level = sp.getSkill(Skill.COMBAT).getLevel();
        double knockbackMultiply = 1 + (Math.floor(sp.getSkill(Skill.BUILDER).getLevelDouble() / 5) * 0.1);
        e.getEntity().setVelocity(p.getLocation().getDirection().setY(0).normalize().multiply(knockbackMultiply));
        if (e.getEntity() instanceof Player target) {
            double percentage = Math.floor(sp.getSkill(Skill.SMITHING).getLevelDouble() / 4);
            double defense = Math.pow(percentage, 1.85) + percentage * 7.4;
            double points = target.getAttribute(Attribute.GENERIC_ARMOR).getValue() + defense;
            double toughness = target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
            PotionEffect effect = target.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            int resistance = effect == null ? 0 : effect.getAmplifier();
            int epf = getEPF(target.getInventory());

            e.setDamage(e.getFinalDamage() + calculateDamageApplied(((Math.pow(level, 1.9)) + level * 3.7), points, toughness, resistance, epf));
        } else {
            e.setDamage(e.getFinalDamage() + (Math.pow(level, 1.9)) + level * 3.7);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        InventoryView view = e.getView();
        Player p = (Player) e.getWhoClicked();
        SilverPlayer sp = new SilverPlayer(p);
        if (e.getCurrentItem() == null) return;
        ItemStack item = e.getCurrentItem();
        if (!(item.hasItemMeta())) return;
        ItemMeta meta = item.getItemMeta();

        if (!(view.getTitle().contains(inventoryName))) return;
        e.setCancelled(true);

        Inventory inv = view.getTopInventory();

        switch (item.getType()) {
            case ARROW -> {
                Skill currentSkill = Skill.matchSkill(inv.getItem(9).getType());
                int currentPage = (int) Math.floor((double) Integer.parseInt(inv.getItem(10).getItemMeta().getLocalizedName()) / 20D);
                Map<Integer, Inventory> invs = currentSkill.generateInventories(sp);

                switch (meta.getLocalizedName().toLowerCase()) {
                    case "forward" -> p.openInventory(invs.get(currentPage + 1));
                    case "back" -> p.openInventory(invs.get(currentPage - 1));
                }
                return;
            }
            case BEACON -> {
                p.openInventory(Skill.getMenu());
                return;
            }
        }

        for (Skill s : Skill.values()) {
            if (e.getCurrentItem().getType() == s.getIcon()) {
                p.openInventory(s.generateInventories(sp).get(0));
                return;
            }
        }
    }

    @EventHandler
    public void onClick(InventoryMoveItemEvent e) {
        if (!(e.getSource().contains(getInventoryPlaceholder()))) return;
        e.setCancelled(true);
    }

    protected static String withSuffix(double count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c", count / Math.pow(1000, exp), "KMBTQISPOND".charAt(exp-1));
    }

    protected static void damagePlayer(Player p, double damage) {
        double points = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        PotionEffect effect = p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int resistance = effect == null ? 0 : effect.getAmplifier();
        int epf = getEPF(p.getInventory());

        p.damage(calculateDamageApplied(damage, points, toughness, resistance, epf));
    }

    protected static double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
        return withResistance * (1 - (Math.min(20.0, epf) / 25));
    }

    protected static int getEPF(PlayerInventory inv) {
        ItemStack helm = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack legs = inv.getLeggings();
        ItemStack boot = inv.getBoots();

        return (helm != null ? helm.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) : 0) +
                (chest != null ? chest.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) : 0) +
                (legs != null ? legs.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) : 0) +
                (boot != null ? boot.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) : 0);
    }

    static class APIPlayer {

        public final String name;
        public final String id;

        public APIPlayer(String name, String id) {
            this.name = name;
            this.id = id;
        }

    }

    final static HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

    static UUID nameToUUID(String name) {
        if (Bukkit.getOnlineMode()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name))
                        .setHeader("User-Agent", "Java 17 HttpClient Bot")
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    Gson g = new Gson();

                    return untrimUUID(g.fromJson(response.body(), APIPlayer.class).id);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    static int statusCode(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .setHeader("User-Agent", "Java 17 HttpClient Bot")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 404;
    }

    static UUID untrimUUID(String oldUUID) {
        String p1 = oldUUID.substring(0, 8);
        String p2 = oldUUID.substring(8, 12);
        String p3 = oldUUID.substring(12, 16);
        String p4 = oldUUID.substring(16, 20);
        String p5 = oldUUID.substring(20, 32);

        String newUUID = p1 + "-" + p2 + "-" + p3 + "-" + p4 + "-" + p5;

        return UUID.fromString(newUUID);
    }

}
