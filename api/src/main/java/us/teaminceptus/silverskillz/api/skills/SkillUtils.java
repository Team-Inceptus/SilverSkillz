package us.teaminceptus.silverskillz.api.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.teaminceptus.silverskillz.api.SilverConfig;

/**
 * Represents events and Utilities for SilverSkillz
 * @author Team Inceptus
 *
 */
public final class SkillUtils {

	/**
	 * Adds a suffix to a health count.
	 * <p>
	 * Ex: {@code withSuffix(1000)} = 1K
	 * @param count Amount to test
	 * @return Amount with Suffix
	 */
	public static String withSuffix(double count) {
		if (count < 1000) return "" + count;
		int exp = (int) (Math.log(count) / Math.log(1000));
		return String.format("%.1f%c", count / Math.pow(1000, exp), "KMBTQISPOND".charAt(exp-1));
	}

	/**
	 * Fetches the Inventory Placeholder.
	 * @return Gray Pne Placeholder
	 */
	public static ItemStack getInventoryPlaceholder() {
		ItemStack guiBG = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		ItemMeta guiBGMeta = guiBG.getItemMeta();
		guiBGMeta.setDisplayName(" ");
		guiBG.setItemMeta(guiBGMeta);
		
		return guiBG;
	}

	/**
	 * Generates an Inventory with no h older.
	 * @param size Size of Inventory (by 9)
	 * @param label Name of Inventory
	 * @return Inventory generated
	 */
	public static Inventory generateGUI(int size, String label) {
	   	return generateGUI(size, label, null);
	}

	/**
	 * Generates an Inventory.
	 * @param size Size of Inventory (by 9)
	 * @param label Name of Inventory
	 * @param holder Holder of Inventory
	 * @return Inventory generated
	 */
	public static Inventory generateGUI(int size, String label, InventoryHolder holder) {
		Inventory inv = Bukkit.createInventory(holder, size, SilverConfig.getConstant("plugin.prefix") + "- " + label);
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
}
