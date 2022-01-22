package us.teaminceptus.silverskillz.premium.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import us.teaminceptus.silverskillz.SilverPlayer;
import us.teaminceptus.silverskillz.SilverSkillz;
import us.teaminceptus.silverskillz.premium.skills.Ability;
import us.teaminceptus.silverskillz.premium.skills.AbilityType;

public class AbilityEvents implements Listener {

	protected SilverSkillz plugin;

	public AbilityEvents(SilverSkillz plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private static final List<Block> getTypeRelatives(Block b) {
		Material type = b.getType();
		List<Block> blocks = new ArrayList<>();
		for (BlockFace face : BlockFace.values()) {
			Block rel = b.getRelative(face);
			if (rel.getType() == type) {
				blocks.add(rel);
				blocks.addAll(getTypeRelatives(rel));
			}
		}

		return blocks;
	}

	Material[] ores = {
		Material.COAL_ORE,
		Material.DEEPSLATE_COAL_ORE,
		Material.COPPER_ORE,
		Material.DEEPSLATE_COPPER_ORE,
		Material.DIAMOND_ORE,
		Material.DEEPSLATE_DIAMOND_ORE,
		Material.EMERALD_ORE,
		Material.DEEPSLATE_EMERALD_ORE,
		Material.GOLD_ORE,
		Material.DEEPSLATE_GOLD_ORE,
		Material.LAPIS_ORE,
		Material.DEEPSLATE_LAPIS_ORE,
		Material.NETHER_GOLD_ORE
	};
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE) return;
		SilverPlayer sp = new SilverPlayer(p);
		Block b = e.getBlock();

		boolean timber = Ability.TIMBER.isUnlocked(sp) && Tag.LOGS.isTagged(b.getType());
		boolean veinMiner = Ability.VEIN_MINER.isUnlocked(sp) && Arrays.asList(ores).contains(b.getType());
		
		if (Ability.TELEKENESIS.isUnlocked(sp) && e.isDropItems()) {
			e.setDropItems(false);

			List<ItemStack> drops = new ArrayList<>();
			drops.addAll(b.getDrops());

			if (timber || veinMiner) {
				for (Block rel : getTypeRelatives(b)) {
					drops.addAll(rel.getDrops());
					rel.setType(Material.AIR);
				}
			}

			Map<Integer, ItemStack> leftovers = p.getInventory().addItem(drops.toArray(new ItemStack[] {}));

			for (ItemStack i : leftovers.values()) p.getWorld().dropItemNaturally(b.getLocation(), i);
		} else {
			if (timber || veinMiner) {
				for (Block rel : getTypeRelatives(b)) rel.breakNaturally();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player p)) return;
		if (!(e.getEntity() instanceof LivingEntity en)) return;
		
		SilverPlayer sp = new SilverPlayer(p);	

		if (Ability.POISONOUS.isUnlocked(sp)) {
			en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 1, true));
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		SilverPlayer sp = new SilverPlayer(p);
		
		for (Ability a : Ability.values()) {
			if (a.getType() != AbilityType.ACTION && a.getType() != AbilityType.ACTION_POTION_COOLDOWN) continue;
			if (!(a.isUnlocked(sp))) return;
			if (a.getAction() != e.getAction()) continue;
			
			switch (a) {
				case WEAPON_THROW: {
					
					break;
				}
				default: {
					continue;
				}
			}			
		}
	}
	
}