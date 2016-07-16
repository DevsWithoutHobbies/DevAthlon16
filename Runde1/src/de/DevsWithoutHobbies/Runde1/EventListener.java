package de.DevsWithoutHobbies.Runde1;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;

import java.util.Set;

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

        plugin.onlinePlayers++;
        if (plugin.onlinePlayers >= plugin.minPlayers) {
            plugin.startCountdown();
        }


        new BukkitRunnable() {

            @Override
            public void run() {
                //event.getPlayer().setResourcePack("tex.zip");
            }
        }.runTaskLater(plugin, 20);

        plugin.mana.put(p.getName(), 0);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.mana.remove(event.getPlayer().getName());
        plugin.onlinePlayers--;
        if (plugin.onlinePlayers < plugin.minPlayers) {
            plugin.stopCountdown();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (plugin.in_game_status == GameStatus.WAITING) {
            event.setCancelled(true);
            plugin.fillInventoryForLobby(event.getInventory());
        } else if (plugin.in_game_status == GameStatus.IN_GAME) {
            if (event.getSlot() >= 36) {
                event.setCancelled(true);
                plugin.resetInventoryForGame(event.getInventory(), Magician.GANDALF); // TODO use correct value
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand.getType() == Material.INK_SACK) {
            executeMagic(player, (int)itemInHand.getData().getData());
        }
    }

    private void executeMagic(final Player player, int id) {
        if (id == 0) { // Explosion
            for (int i = 5; i < 20; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                player.getWorld().createExplosion(loc, 1F);
            }
        } else if (id == 1) { // Levitation
            for (int i = 10; i < 30; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    Location playerLocation = p.getLocation();
                    if (playerLocation.subtract(loc).length() < 2) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 80, 1));
                    }
                }
            }
        } else if (id == 2) { // Door Opener
            Block target_block = player.getTargetBlock((Set<Material>) null, 3);
            player.sendMessage("found block");
            player.sendMessage(target_block.getType().toString());
            if (target_block.getType() == Material.IRON_DOOR_BLOCK) {
                player.sendMessage("found door");
                MaterialData md = target_block.getState().getData();
                ((Door)md).setOpen(!((Door)md).isOpen());
                target_block.getState().setData(md);
                target_block.getState().update();
            }
        } else if (id==3) { // Arrow Shooter
            final BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    player.launchProjectile(Arrow.class);
                }
            }.runTaskTimer(plugin, 0, 5);

            new BukkitRunnable() {
                @Override
                public void run() {
                    task.cancel();
                }
            }.runTaskLater(plugin, 40);
        } else if (id==4) { // Water
            Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 1000);
            Location l = targetBlock.getLocation().add(0, 1, 0);
            Block target = targetBlock.getWorld().getBlockAt(l); // TODO improve get block
            if (target.getType() == Material.AIR) {
                target.setType(Material.WATER);
            }
        } else if (id==5) { // Lava
            Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 1000);
            Location l = targetBlock.getLocation().add(0, 1, 0);
            Block target = targetBlock.getWorld().getBlockAt(l); // TODO improve get block
            if (target.getType() == Material.AIR) {
                target.setType(Material.LAVA);
            }
        } else if (id==6) { // Snow Ball Shooter
            final BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    player.launchProjectile(Snowball.class);
                }
            }.runTaskTimer(plugin, 0, 3);

            new BukkitRunnable() {
                @Override
                public void run() {
                    task.cancel();
                }
            }.runTaskLater(plugin, 40);
        } else if (id==7) { //Fireball
            player.launchProjectile(Fireball.class);
        } else if (id == 8) { //Slowness
            for (int i = 10; i < 30; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    Location playerLocation = p.getLocation();
                    if (playerLocation.subtract(loc).length() < 2) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
                    }
                }
            }
        } else if (id == 9) { //Blindness
            for (int i = 10; i < 30; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    Location playerLocation = p.getLocation();
                    if (playerLocation.subtract(loc).length() < 2) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    }
                }
            }
        } else if (id == 10) { //Poison
            for (int i = 10; i < 30; i+=3) {
                Vector direction = player.getEyeLocation().getDirection();
                Location loc = player.getEyeLocation().add(direction.multiply(i));
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    Location playerLocation = p.getLocation();
                    if (playerLocation.subtract(loc).length() < 2) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
                    }
                }
            }
        } else if (id == 11) { //Teleportation
            player.launchProjectile(EnderPearl.class);
        }
    }


}
