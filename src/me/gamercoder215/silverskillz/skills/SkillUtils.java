package me.gamercoder215.silverskillz.skills;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.gamercoder215.silverskillz.SilverPlayer;
import me.gamercoder215.silverskillz.SilverSkillz;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SkillUtils implements Listener {
	
	protected SilverSkillz plugin;
	
	public SkillUtils(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public final static void sendActionBar(Player p, final String message) {
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		InventoryView view = e.getView();
		Player p = (Player) e.getWhoClicked();
		SilverPlayer sp = SilverPlayer.fromPlayer(p);
		if (e.getCurrentItem() == null) return;
		if (!(view.getTitle().contains("SilverSkillz - "))) return;
		e.setCancelled(true);
		
		if (e.getCurrentItem().getType() == Material.ARROW) {
			Skill currentSkill = Skill.matchSkill(ChatColor.stripColor(view.getTitle()).split("\\s")[2].toLowerCase());
			int currentPage = (view.getTitle().split("\\s").length > 6 ? Integer.parseInt(view.getTitle().split("\\s")[6]) - 1 : 0);
			Map<Integer, Inventory> invs = currentSkill.generateInventories(sp);
			if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Next Page")) {
				p.openInventory(invs.get(currentPage + 1));
			} else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Previous Page")) {
				p.openInventory(invs.get(currentPage - 1));
			}
			return;
		}
		
		if (e.getCurrentItem().getType() == Material.BEACON) {
			p.openInventory(Skill.getMenu());
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
	
	private static ItemStack getInventoryPlaceholder() {
		ItemStack guiBG = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta guiBGMeta = guiBG.getItemMeta();
		guiBGMeta.setDisplayName(" ");
		guiBG.setItemMeta(guiBGMeta);
		
		return guiBG;
	}
	
	public static Inventory generateGUI(int size, String label) {
	   	Inventory inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "SilverSkillz - " + label);
	   	ItemStack guiBG = getInventoryPlaceholder();
		
		if (size < 27) return inv;
		
		for (int i = 0; i < 9; i++) {
			inv.setItem(i, guiBG);
		}
		
		for (int i = size - 9; i < size; i++) {
			inv.setItem(i, guiBG);
		}
		
		if (size >= 27) {
			inv.setItem(9, guiBG);
			inv.setItem(17, guiBG);
		}
		if (size >= 36) {
			inv.setItem(18, guiBG);
			inv.setItem(26, guiBG);
		}
		if (size >= 45) {
			inv.setItem(27, guiBG);
			inv.setItem(35, guiBG);
		}
		if (size == 54) {
			inv.setItem(36, guiBG);
			inv.setItem(44, guiBG);
		}
		return inv;
	}
	
	@EventHandler
	public void damageCalculation(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player p)) return;
		
		SilverPlayer sp = SilverPlayer.fromPlayer(p);
		short level = sp.getSkill(Skill.COMBAT).getLevel();
		double knockbackMultiply = 1 + (Math.floor(sp.getSkill(Skill.BUILDER).getLevel() / 5) * 0.1);
		e.getEntity().setVelocity(p.getLocation().getDirection().setY(0).normalize().multiply(knockbackMultiply));
		if (e.getEntity() instanceof Player target) {
			double points = target.getAttribute(Attribute.GENERIC_ARMOR).getValue() + defense;
			double toughness = target.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
			PotionEffect effect = target.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			int resistance = effect == null ? 0 : effect.getAmplifier();
			int epf = getEPF(target.getInventory());
			double percentage = Math.floor(sp.getSkill(Skill.SMITHING).getLevel() / 4);
			double defense = Math.pow(percentage, 1.85) + percentage * 7.4;

			e.setDamage(e.getFinalDamage() + calculateDamageApplied(((Math.pow(level, 1.9)) + level * 3.7), points, toughness, resistance, epf));
			return;
		} else {
			e.setDamage(e.getFinalDamage() + (Math.pow(level, 1.9)) + level * 3.7);
			return;
		}
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
  int epf = Util.getEPF(p.getInventory());

  p.damage(calculateDamageApplied(damage, points, toughness, resistance, epf));
}

protected static double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
  double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
  double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
  double withEnchants = withResistance * (1 - (Math.min(20.0, epf) / 25));
  return withEnchants;
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
}
