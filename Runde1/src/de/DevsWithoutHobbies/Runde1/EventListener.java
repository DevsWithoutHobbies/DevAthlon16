package de.DevsWithoutHobbies.Runde1;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

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
        ItemStack item = new ItemStack(Material.INK_SACK, 1, (short)2);
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
}
