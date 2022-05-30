package us.teaminceptus.silverskillz;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.teaminceptus.silverskillz.api.SilverConfig;
import us.teaminceptus.silverskillz.api.SilverPlayer;
import us.teaminceptus.silverskillz.api.artifact.Artifact;
import us.teaminceptus.silverskillz.api.skills.Skill;
import us.teaminceptus.silverskillz.api.skills.SkillUtils;

import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.ChatColor.*;
import static us.teaminceptus.silverskillz.api.skills.SkillUtils.getInventoryPlaceholder;

public class InternalUtil implements Listener {

    final SilverSkillz plugin;
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


    private static Map<Integer, ItemStack> getInventoryIcons(Skill s, SilverPlayer p) {
        df.setRoundingMode(RoundingMode.FLOOR);
        Map<Integer, ItemStack> icons = new HashMap<>();

        for (int i = 1; i <= 100; i++) {
            int nextLevel = i + 1;
            boolean comp = p.getSkill(s).getProgress() >= Skill.toMinimumProgress(s.isBasic(), i);
            Material m = comp ? Material.LIME_STAINED_GLASS_PANE : Material.YELLOW_STAINED_GLASS_PANE;
            ChatColor nameCC = comp ? GREEN : YELLOW;
            String name = String.format(SilverConfig.getConstant("skills.gui.panel_name"), nameCC, i, nameCC, comp ? SilverConfig.getConstant("constants.complete") : SilverConfig.getConstant("constants.incomplete"));

            List<String> completedLore = new ArrayList<>();
            List<String> incompleteLore = new ArrayList<>();

            if (i != 100) incompleteLore.add(YELLOW + SkillUtils.withSuffix(Double.parseDouble(df.format(p.getSkill(s).getProgress()))) + " / " + SkillUtils.withSuffix(Double.parseDouble(df.format(Skill.toMinimumProgress(s.isBasic(), nextLevel)))));
            else incompleteLore.add(YELLOW + SkillUtils.withSuffix(Double.parseDouble(df.format(p.getSkill(s).getProgress()))) + " / " + SkillUtils.withSuffix(Double.parseDouble(df.format(Skill.toMinimumProgress(s.isBasic(), 100)))));

            switch (s) {
                case COMBAT -> {
                    completedLore.add(String.format(SilverConfig.getConstant("skills.gui.combat.buff"), "" + BLUE, i, df.format((Math.pow(i, 1.9)) + i * 3.7)));
                    incompleteLore.add(SP);
                    incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.combat.buff"), "" + AQUA, i, df.format((Math.pow(i, 1.9)) + i * 3.7)));
                }
                case FARMING -> {
                    if (i % 20 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.farming.drops"), GOLD));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.farming.drops"), YELLOW));
                    }

                    if (i % 3 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.farming.damage"), RED));
                        if (!(incompleteLore.contains(SP))) incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.farming.damage"), DARK_RED));
                    }
                }
                case MINING -> {
                    if (i % 5 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.mining.fortune"), AQUA));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.mining.fortune"), DARK_AQUA));
                    }
                }
                case BREWER -> {
                    completedLore.add(String.format(SilverConfig.getConstant("skills.gui.brewer.time"), LIGHT_PURPLE));
                    incompleteLore.add(SP);
                    incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.brewer.time"), DARK_PURPLE));
                }
                case HUSBANDRY -> {
                    String id = switch (i) {
                        case 25 -> "I";
                        case 50 -> "II";
                        case 75 -> "III";
                        default -> "IV";
                    };


                    switch (i) {
                        case 25, 50, 75, 100 -> {
                            completedLore.add(String.format(SilverConfig.getConstant("skills.gui.husbandry.hero"), GREEN, id));
                            incompleteLore.add(SP);
                            incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.husbandry.hero"), DARK_GREEN, id));

                            completedLore.add(String.format(SilverConfig.getConstant("skills.gui.husbandry.jump"), GREEN, id));
                            incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.husbandry.jump"), DARK_GREEN, id));
                        }
                    }

                }
                case AQUATICS -> {
                    if (i == 50) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.aquatics.breathing"), AQUA));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.aquatics.breathing"), DARK_AQUA));
                    }

                    String id = switch (i) {
                        case 30 -> "I";
                        case 60 -> "II";
                        default -> "III";
                    };

                    if (i % 30 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.aquatics.dolphin"), BLUE, id));
                        if (!(incompleteLore.contains(SP))) incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.aquatics.dolphin"), DARK_BLUE, id));
                    }
                }
                case CLEANER -> {
                    if (i % 7 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.cleaner.undead"), DARK_RED));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.cleaner.unbreaking"), DARK_AQUA));
                    }

                    if (i % 10 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.cleaner.unbreaking"), BLUE));
                        if (!(incompleteLore.contains(SP)))incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.cleaner.unbreaking"), DARK_AQUA));
                    }
                }
                case ENCHANTER -> {
                    if (i % 20 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.level"), LIGHT_PURPLE));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.level"), DARK_PURPLE));
                    }

                    if (i % 5 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.offer"), AQUA));
                        if (!(incompleteLore.contains(SP))) incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.enchanter.offer"), BLUE));
                    }
                }
                case ADVANCER -> {
                    if (i % 5 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.advancer.loot"), YELLOW));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.advancer.loot"), GOLD));
                    }
                }
                case SMITHING -> {
                    if (i == 30) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.insta"), LIGHT_PURPLE));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.insta"), DARK_PURPLE));
                    }

                    if (i % 4 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.resistance"), GREEN));
                        if (!(incompleteLore.contains(SP))) incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.smithing.resistance"), DARK_GREEN));
                    }
                }
                case ARCHERY -> {
                    if (i % 5 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.archery.velocity"), YELLOW));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.archery.velocity"), GOLD));
                    }

                    completedLore.add(SilverConfig.getConstant("skills.gui.archery.damage"));
                    if (!(incompleteLore.contains(SP))) incompleteLore.add(SP);
                    incompleteLore.add(SilverConfig.getConstant("skills.gui.archery.damage"));
                }
                case TRAVELER -> {
                    if (i % 10 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.traveler.saturation"), WHITE));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.traveler.saturation"), GRAY));
                    }

                    if (i % 15 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.traveler.speed"), AQUA));
                        if (!(incompleteLore.contains(SP))) incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.traveler.speed"), YELLOW));
                    }
                }
                case BUILDER -> {
                    if (i % 5 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.builder.knockback"), RED));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.builder.knockback"), DARK_RED));
                    }
                }
                case COLLECTOR -> {
                    if (i % 2 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.collector.statistic"), GREEN));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.collector.statistic"), DARK_GREEN));
                    }
                }
                case SOCIAL -> {
                    if (i % 6 == 0) {
                        completedLore.add(String.format(SilverConfig.getConstant("skills.gui.social.ignore"), RED));
                        incompleteLore.add(SP);
                        incompleteLore.add(String.format(SilverConfig.getConstant("skills.gui.social.ignore"), DARK_RED));
                    }
                }
            }

            for (Artifact a : Artifact.values())
                if (a.getSkill() == s && i == a.getLevelUnlocked()) {
                    String msg = String.format(SilverConfig.getConstant("skills.gui.artifact"), a.getRecipe().getResult().getItemMeta().getDisplayName());
                    completedLore.add(msg);
                    incompleteLore.add(SP);
                    incompleteLore.add(msg);
                }

            ItemStack stack = new ItemStack(m);
            ItemMeta stackMeta = stack.getItemMeta();
            stackMeta.setDisplayName(name);
            stackMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
            if (comp) stackMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            stackMeta.setLore(comp ? completedLore : incompleteLore);
            stackMeta.setLocalizedName(i + "");
            stack.setItemMeta(stackMeta);

            icons.put(i - 1, stack);
        }

        return icons;
    }

    private static final DecimalFormat df = new DecimalFormat("###.#");
    private static final String SP = " ";

    /**
     * Generate inventory pages for the skill
     * @param p The player to use.
     * @return A Map listing the inventories by page (starting at 0)
     */
    private final Map<Integer, Inventory> generateInventories(SilverPlayer p, Skill skill) {
        Map<Integer, Inventory> pages = new HashMap<>();

        Map<Integer, ItemStack> panels = getInventoryIcons(skill, p);

        ItemStack headInfo = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) headInfo.getItemMeta();
        headMeta.setOwningPlayer(p.getPlayer());
        headMeta.setDisplayName(String.format(SilverConfig.getConstant("constants.gui.stats"), p.getName()));

        List<String> info = new ArrayList<>();
        info.add(String.format(SilverConfig.getConstant("constants.skill.progress"), SkillUtils.withSuffix(p.getSkill(skill).getProgress())));
        info.add(String.format(SilverConfig.getConstant("constants.skill.level"), p.getSkill(skill).getLevel()));
        headMeta.setLore(info);
        headInfo.setItemMeta(headMeta);

        ItemStack arrowForward = new ItemStack(Material.ARROW);
        ItemMeta forwardMeta = arrowForward.getItemMeta();
        forwardMeta.setDisplayName(SilverConfig.getConstant("constants.gui.next_page"));
        forwardMeta.setLocalizedName("forward");
        arrowForward.setItemMeta(forwardMeta);

        ItemStack arrowBack = new ItemStack(Material.ARROW);
        ItemMeta backMeta = arrowForward.getItemMeta();
        backMeta.setDisplayName(SilverConfig.getConstant("constants.gui.previous_page"));
        backMeta.setLocalizedName("back");
        arrowBack.setItemMeta(backMeta);

        ItemStack menuBack = new ItemStack(Material.BEACON);
        ItemMeta menuMeta = menuBack.getItemMeta();
        menuMeta.setDisplayName(SilverConfig.getConstant("constants.gui.back"));
        menuBack.setItemMeta(menuMeta);
        // First Page
        Inventory firstPage = SkillUtils.generateGUI(54, AQUA + skill.getCapitalizedName() + "Skill");

        firstPage.setItem(9, skill.getIconAsStack());
        firstPage.setItem(4, headInfo);
        firstPage.setItem(49, menuBack);
        firstPage.setItem(50, arrowForward);

        firstPage.setItem(10, panels.get(0));
        firstPage.setItem(19, panels.get(1));
        firstPage.setItem(28, panels.get(2));
        firstPage.setItem(37, panels.get(3));
        firstPage.setItem(38, panels.get(4));
        firstPage.setItem(39, panels.get(5));
        firstPage.setItem(30, panels.get(6));
        firstPage.setItem(21, panels.get(7));
        firstPage.setItem(12, panels.get(8));
        firstPage.setItem(13, panels.get(9));
        firstPage.setItem(14, panels.get(10));
        firstPage.setItem(23, panels.get(11));
        firstPage.setItem(32, panels.get(12));
        firstPage.setItem(41, panels.get(13));
        firstPage.setItem(42, panels.get(14));
        firstPage.setItem(43, panels.get(15));
        firstPage.setItem(34, panels.get(16));
        firstPage.setItem(25, panels.get(17));
        firstPage.setItem(16, panels.get(18));
        firstPage.setItem(17, panels.get(19));

        pages.put(0, firstPage);
        // Second Page
        Inventory secondPage = SkillUtils.generateGUI(54, AQUA + skill.getCapitalizedName() + "Skill - " + DARK_AQUA + SilverConfig.getConstant("constants.page") + " 2");

        secondPage.setItem(9, skill.getIconAsStack());
        secondPage.setItem(4, headInfo);
        secondPage.setItem(50, arrowForward);
        secondPage.setItem(48, arrowBack);

        secondPage.setItem(10, panels.get(20));
        secondPage.setItem(19, panels.get(21));
        secondPage.setItem(28, panels.get(22));
        secondPage.setItem(37, panels.get(23));
        secondPage.setItem(38, panels.get(24));
        secondPage.setItem(39, panels.get(25));
        secondPage.setItem(30, panels.get(26));
        secondPage.setItem(21, panels.get(27));
        secondPage.setItem(12, panels.get(28));
        secondPage.setItem(13, panels.get(29));
        secondPage.setItem(14, panels.get(30));
        secondPage.setItem(23, panels.get(31));
        secondPage.setItem(32, panels.get(32));
        secondPage.setItem(41, panels.get(33));
        secondPage.setItem(42, panels.get(34));
        secondPage.setItem(43, panels.get(35));
        secondPage.setItem(34, panels.get(36));
        secondPage.setItem(25, panels.get(37));
        secondPage.setItem(16, panels.get(38));
        secondPage.setItem(17, panels.get(39));

        pages.put(1, secondPage);
        // Third Page
        Inventory thirdPage = SkillUtils.generateGUI(54, AQUA + skill.getCapitalizedName() + "Skill - " + DARK_AQUA + SilverConfig.getConstant("constants.page") + " 3");

        thirdPage.setItem(9, skill.getIconAsStack());
        thirdPage.setItem(4, headInfo);
        thirdPage.setItem(50, arrowForward);
        thirdPage.setItem(48, arrowBack);

        thirdPage.setItem(10, panels.get(40));
        thirdPage.setItem(19, panels.get(41));
        thirdPage.setItem(28, panels.get(42));
        thirdPage.setItem(37, panels.get(43));
        thirdPage.setItem(38, panels.get(44));
        thirdPage.setItem(39, panels.get(45));
        thirdPage.setItem(30, panels.get(46));
        thirdPage.setItem(21, panels.get(47));
        thirdPage.setItem(12, panels.get(48));
        thirdPage.setItem(13, panels.get(49));
        thirdPage.setItem(14, panels.get(50));
        thirdPage.setItem(23, panels.get(51));
        thirdPage.setItem(32, panels.get(52));
        thirdPage.setItem(41, panels.get(53));
        thirdPage.setItem(42, panels.get(54));
        thirdPage.setItem(43, panels.get(55));
        thirdPage.setItem(34, panels.get(56));
        thirdPage.setItem(25, panels.get(57));
        thirdPage.setItem(16, panels.get(58));
        thirdPage.setItem(17, panels.get(59));

        pages.put(2, thirdPage);

        // Fourth Page
        Inventory fourthPage = SkillUtils.generateGUI(54, AQUA + skill.getCapitalizedName() + "Skill - " + DARK_AQUA + SilverConfig.getConstant("constants.page") + " 4");

        fourthPage.setItem(9, skill.getIconAsStack());
        fourthPage.setItem(4, headInfo);
        fourthPage.setItem(50, arrowForward);
        fourthPage.setItem(48, arrowBack);

        fourthPage.setItem(10, panels.get(60));
        fourthPage.setItem(19, panels.get(61));
        fourthPage.setItem(28, panels.get(62));
        fourthPage.setItem(37, panels.get(63));
        fourthPage.setItem(38, panels.get(64));
        fourthPage.setItem(39, panels.get(65));
        fourthPage.setItem(30, panels.get(66));
        fourthPage.setItem(21, panels.get(67));
        fourthPage.setItem(12, panels.get(68));
        fourthPage.setItem(13, panels.get(69));
        fourthPage.setItem(14, panels.get(70));
        fourthPage.setItem(23, panels.get(71));
        fourthPage.setItem(32, panels.get(72));
        fourthPage.setItem(41, panels.get(73));
        fourthPage.setItem(42, panels.get(74));
        fourthPage.setItem(43, panels.get(75));
        fourthPage.setItem(34, panels.get(76));
        fourthPage.setItem(25, panels.get(77));
        fourthPage.setItem(16, panels.get(78));
        fourthPage.setItem(17, panels.get(79));

        pages.put(3, fourthPage);
        // Fifth Page
        Inventory fifthPage = SkillUtils.generateGUI(54, AQUA + skill.getCapitalizedName() + "Skill - " + DARK_AQUA + SilverConfig.getConstant("constants.page") + " 5");

        fifthPage.setItem(9, skill.getIconAsStack());
        fifthPage.setItem(4, headInfo);
        fifthPage.setItem(48, arrowBack);

        fifthPage.setItem(10, panels.get(80));
        fifthPage.setItem(19, panels.get(81));
        fifthPage.setItem(28, panels.get(82));
        fifthPage.setItem(37, panels.get(83));
        fifthPage.setItem(38, panels.get(84));
        fifthPage.setItem(39, panels.get(85));
        fifthPage.setItem(30, panels.get(86));
        fifthPage.setItem(21, panels.get(87));
        fifthPage.setItem(12, panels.get(88));
        fifthPage.setItem(13, panels.get(89));
        fifthPage.setItem(14, panels.get(90));
        fifthPage.setItem(23, panels.get(91));
        fifthPage.setItem(32, panels.get(92));
        fifthPage.setItem(41, panels.get(93));
        fifthPage.setItem(42, panels.get(94));
        fifthPage.setItem(43, panels.get(95));
        fifthPage.setItem(34, panels.get(96));
        fifthPage.setItem(25, panels.get(97));
        fifthPage.setItem(16, panels.get(98));
        fifthPage.setItem(17, panels.get(99));

        pages.put(4, fifthPage);

        return pages;
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
                Map<Integer, Inventory> invs = generateInventories(sp, currentSkill);

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
                p.openInventory(generateInventories(sp, s).get(0));
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
