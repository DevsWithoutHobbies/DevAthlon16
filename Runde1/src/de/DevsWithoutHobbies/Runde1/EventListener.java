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
    }

    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getType() == Material.INK_SACK) {
            executeZauber(player, 0);
        }
    }

    public void executeZauber(Player player, int id) {
        if (id == 0) {
            for (int i = 5; i < 20; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                player.getWorld().createExplosion(loc, 1F);
            }
        }
    }
}
