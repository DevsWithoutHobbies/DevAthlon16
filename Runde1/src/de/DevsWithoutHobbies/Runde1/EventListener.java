package de.DevsWithoutHobbies.Runde1;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;

@SuppressWarnings("unchecked")
class EventListener implements Listener {
    private final Zauberkrieg plugin;

    EventListener(Zauberkrieg instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        plugin.getLogger().info("Welcome to the server");
        Player player = event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);

        if (plugin.getServer().getOnlinePlayers().size() >= plugin.minPlayers) {
            plugin.startCountdown();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                //event.getPlayer().setResourcePack("https://drive.google.com/uc?export=download&id=0B3nrXpuc7an1ZTZfMm9vS1dlbW8");
                //event.getPlayer().setResourcePack("http://addons-origin.cursecdn.com/files/2293/954/Mint%20Flavor.zip");
            }
        }.runTaskLater(plugin, 20);

        plugin.mana.put(player.getName(), 0);

        plugin.characters.put(player.getName(), null);

        plugin.fillInventoryForLobby(player.getInventory());

        player.teleport(plugin.lobbySpawn);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        plugin.mana.remove(event.getPlayer().getName());
        plugin.characters.remove(event.getPlayer().getName());
        if (plugin.getServer().getOnlinePlayers().size() < plugin.minPlayers) {
            plugin.stopCountdown();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.burningPeople.contains(player.getName())) {
            Location loc = event.getPlayer().getLocation();
            loc.setX(event.getFrom().getX());
            loc.setY(event.getFrom().getY());
            loc.setZ(event.getFrom().getZ());
            player.teleport(loc);
            event.setCancelled(true);
        }
    }


        @EventHandler
    public void onInventoryClickItem(InventoryClickEvent event) {
        if (plugin.in_game_status == GameStatus.WAITING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().setGameMode(GameMode.SPECTATOR);
        event.getDrops().clear();

        int aliveMagicians = plugin.getNumberOfAliveMagicians();
        int aliveHumans = plugin.getNumberOfAliveHumans();
        if (aliveMagicians == 0 || aliveHumans == 0) {
            Bukkit.broadcastMessage(ChatColor.RED + "The server will restart in 10 seconds");

            if (aliveHumans != 0) {
                Bukkit.broadcastMessage(ChatColor.RED + "The Humans won the game");
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "The Magicians won the game");
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.stopGame();
                }
            }.runTaskLater(plugin, 200);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {

        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (((Character) plugin.characters.get(player.getName())).isHuman()) {
                    player.teleport(plugin.respawn);
                } else if (plugin.burningPeople.contains(player.getName())) {
                    player.teleport(plugin.respawn);
                    plugin.burningPeople.remove(player.getName());
                } else {
                    plugin.burningPeople.add(player.getName());
                    Location player_loc = plugin.burningPlaces.get(plugin.burned).clone().add(0.5, 1.0, 0.5);
                    player.teleport(player_loc);
                    player.getPlayer().setGameMode(GameMode.ADVENTURE);
                    plugin.enableBurningPlace(plugin.burned);
                    plugin.burned++;
                }
            }
        }.runTaskLater(plugin, 20);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        if (event.getHand().equals(EquipmentSlot.HAND)) {
            final Player player = event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (plugin.in_game_status == GameStatus.IN_GAME) {
                if (itemInHand.getType() == Material.INK_SACK) {
                    executeSpell(player, (int) itemInHand.getData().getData());
                }
            } else if (plugin.in_game_status == GameStatus.WAITING || plugin.in_game_status == GameStatus.COUNTDOWN) {
                if (itemInHand.getType() == Material.STAINED_GLASS_PANE) {
                    Character new_character = Character.getByID(itemInHand.getData().getData());
                    Character old_character = (Character) plugin.characters.get(player.getName());

                    int magician_count = plugin.getNumberOfMagicians();
                    int human_count = plugin.getNumberOfHumans();

                    if (new_character != null) {
                        if (new_character.isMagician()) {
                            magician_count++;
                        } else {
                            human_count++;
                        }
                    }
                    if (old_character != null) {
                        if (old_character.isMagician()) {
                            magician_count--;
                        } else {
                            human_count--;
                        }
                    }
                    if (abs(magician_count - human_count) < 2) {
                        plugin.characters.put(player.getName(), new_character);
                    } else {
                        player.sendMessage("Your old team is too small");
                    }
                } else if (itemInHand.getType() == Material.BARRIER) {
                    plugin.characters.put(player.getName(), null);
                }
            }
        }
    }

    private void executeSpell(final Player player, int id) {
        Spell spell = Spell.getByID(id);
        if (spell == Spell.EXPLOSION) { // Explosion
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.EXPLOSION.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.EXPLOSION.getCost());
                for (int i = 5; i < 20; i += 3) {
                    Vector direction = player.getEyeLocation().getDirection();
                    Location loc = player.getEyeLocation().add(direction.multiply(i));
                    player.getWorld().playEffect(loc, Effect.EXPLOSION, 1);
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        double distance = loc.distance(p.getLocation());
                        if (distance < 4) {
                            if (1/distance < 8) {
                                p.damage(1 / distance);
                            } else {
                                p.damage(8.0);
                            }
                        }
                    }
                }
            }
        } else if (spell == Spell.LEVITATION) { // Levitation
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.LEVITATION.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.LEVITATION.getCost());
                for (int i = 10; i < 30; i += 3) {
                    Vector direction = player.getEyeLocation().getDirection();
                    Location loc = player.getEyeLocation().add(direction.multiply(i));
                    player.getWorld().playEffect(loc, Effect.CLOUD, 2);
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        Location playerLocation = p.getLocation();
                        if (playerLocation.subtract(loc).length() < 2) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 80, 1));
                        }
                    }
                }
            }
        } else if (spell == Spell.DOOR_OPENER) { // Door Opener
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.DOOR_OPENER.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.DOOR_OPENER.getCost());
                Block target_block = player.getTargetBlock((Set<Material>) null, 3);
                player.sendMessage("found block");
                player.sendMessage(target_block.getType().toString());
                if (target_block.getType() == Material.IRON_DOOR_BLOCK) {
                    player.sendMessage("found door");
                    MaterialData md = target_block.getState().getData();
                    ((Door) md).setOpen(!((Door) md).isOpen());
                    target_block.getState().setData(md);
                    target_block.getState().update();
                }
            }
        } else if (spell == Spell.ARROW_SHOOTER) { // Arrow Shooter
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.ARROW_SHOOTER.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.ARROW_SHOOTER.getCost());
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
            }
        } else if (spell == Spell.WATER) { // Water
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.WATER.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.WATER.getCost());
                Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 1000);
                Location l = targetBlock.getLocation().add(0, 1, 0);
                final Block target = targetBlock.getWorld().getBlockAt(l); // TODO improve get block
                if (targetBlock.getType() != Material.AIR) {
                    if (target.getType() == Material.AIR) {
                        target.setType(Material.WATER);
                    }
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.setType(Material.AIR);
                    }
                }.runTaskLater(plugin, 200);
            }
        } else if (spell == Spell.LAVA) { // Lava
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.LAVA.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.LAVA.getCost());
                Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 1000);
                Location l = targetBlock.getLocation().add(0, 1, 0);
                Block target = targetBlock.getWorld().getBlockAt(l); // TODO improve get block
                if (targetBlock.getType() != Material.AIR) {
                    if (target.getType() == Material.AIR) {
                        target.setType(Material.LAVA);
                    }
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.setType(Material.AIR);
                    }
                }.runTaskLater(plugin, 200);
            }
        } else if (spell == Spell.SNOW_BALL_SHOOTER) { // Snow Ball Shooter
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.SNOW_BALL_SHOOTER.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.SNOW_BALL_SHOOTER.getCost());
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
            }
        } else if (spell == Spell.FIREBALL) { //Fireball
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.FIREBALL.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.FIREBALL.getCost());
                player.launchProjectile(Fireball.class);
            }
        } else if (spell == Spell.SLOWNESS) { //Slowness
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.SLOWNESS.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.SLOWNESS.getCost());
                for (int i = 10; i < 30; i += 3) {
                    Vector direction = player.getEyeLocation().getDirection();
                    Location loc = player.getEyeLocation().add(direction.multiply(i));
                    player.getWorld().playEffect(loc, Effect.SMOKE, 2);
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        Location playerLocation = p.getLocation();
                        if (playerLocation.subtract(loc).length() < 2) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
                        }
                    }
                }
            }
        } else if (spell == Spell.BLINDNESS) { //Blindness
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.BLINDNESS.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.BLINDNESS.getCost());
                for (int i = 10; i < 30; i += 3) {
                    Vector direction = player.getEyeLocation().getDirection();
                    Location loc = player.getEyeLocation().add(direction.multiply(i));
                    player.getWorld().playEffect(loc, Effect.LARGE_SMOKE, 2);
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        Location playerLocation = p.getLocation();
                        if (playerLocation.subtract(loc).length() < 2) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 1));
                        }
                    }
                }
            }
        } else if (spell == Spell.POISON) { //Poison
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.POISON.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.POISON.getCost());
                for (int i = 10; i < 30; i += 3) {
                    Vector direction = player.getEyeLocation().getDirection();
                    Location loc = player.getEyeLocation().add(direction.multiply(i));
                    player.getWorld().playEffect(loc, Effect.VILLAGER_PLANT_GROW, 2);
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        Location playerLocation = p.getLocation();
                        if (playerLocation.subtract(loc).length() < 2) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 1));
                        }
                    }
                }
            }
        } else if (spell == Spell.TELEPORTATION) { //Teleportation
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.TELEPORTATION.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.TELEPORTATION.getCost());
                player.launchProjectile(EnderPearl.class);
            }
        } else if (spell == Spell.INVISIBILITY) { //Invisibility
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.INVISIBILITY.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.INVISIBILITY.getCost());
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getLocation().distance(player.getLocation()) < 3) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 400, 1));
                    }
                }
            }
        } else if (spell == Spell.SAND_TOWER) { // Sand Tower
            if ((Integer) plugin.mana.get(player.getName()) >= Spell.SAND_TOWER.getCost()) {
                plugin.mana.put(player.getName(), (Integer) plugin.mana.get(player.getName()) - Spell.SAND_TOWER.getCost());
                Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 1000);
                for (int i = 0; i< 3; i++) {
                    Location l = targetBlock.getLocation().add(0, 10 + i, 0);
                    final Block target = targetBlock.getWorld().getBlockAt(l); // TODO improve get block
                    if (targetBlock.getType() != Material.AIR) {
                        if (target.getType() == Material.AIR) {
                            target.setType(Material.SAND);
                        }
                    }
                }
            }
        }
    }


}
