package de.DevsWithoutHobbies.Runde1;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
    private Zauberkrieg plugin;

    public EventListener(Zauberkrieg instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        plugin.getLogger().info("Welcome to the server");
        Player p = e.getPlayer();
        p.sendMessage(ChatColor.RED + "Welcome to the server");
        ItemStack item = new ItemStack(Material.INK_SACK, 1, (short) 5);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName("Awesome effect");
        item.setItemMeta(im);
        p.getInventory().addItem(item);

        new BukkitRunnable() {

            @Override
            public void run() {
                //event.getPlayer().setResourcePack("tex.zip");
            }
        }.runTaskLater(plugin, 20);
    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getType() == Material.INK_SACK) {
            executeMagic(player, (int)itemInHand.getData().getData());
        }
    }

    private void executeMagic(Player player, int id) {
        if (id == 0) {
            for (int i = 5; i < 20; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                player.getWorld().createExplosion(loc, 1F);
            }
        } else if (id == 1) {
            for (int i = 10; i < 30; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                //player.getWorld().createExplosion(loc, 1F);
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    Location playerLocation = p.getLocation();
                    if (playerLocation.subtract(loc).length() < 2) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 4, 1));
                    }
                }
            }
        } else if (id == 2) {
            for (int i = 0; i < 5; i+=1) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));

                player.sendMessage(player.getWorld().getBlockAt(loc).getType().toString());
                if (player.getWorld().getBlockAt(loc).getType() == Material.IRON_DOOR_BLOCK) {
                    player.sendMessage(((int)player.getWorld().getBlockAt(loc).getData()) + "");
                }
            }
        }
    }
}
