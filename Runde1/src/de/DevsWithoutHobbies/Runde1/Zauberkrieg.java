package de.DevsWithoutHobbies.Runde1;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Zauberkrieg extends JavaPlugin {


    GameStatus in_game_status = GameStatus.WAITING;

    final HashMap mana = new HashMap();
    final HashMap characters = new HashMap();

    int onlinePlayers = 0; // TODO remove

    private BukkitTask countdownTask;
    private BukkitTask manaTask;
    private int countdownTimer;

    private final List<Location> spawns = new ArrayList<>();
    private final List<Location> healingStations = new ArrayList<>();
    Location lobbySpawn;

    int minPlayers;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        createConfig();
        onlinePlayers = getServer().getOnlinePlayers().size();

        new BukkitRunnable() {
            @Override
            public void run() {
                updateXPBar();
            }
        }.runTaskTimer(this, 0, 10);

        final World default_world = getServer().getWorld("world");

        for (int i = 1; i <= getConfig().getInt("number-of-spawns"); i++) {
            String spawn = this.getConfig().getString("spawn-" + String.valueOf(i));
            String[] spawnCoordinates = spawn.split(",");
            spawns.add(new Location(default_world, Integer.valueOf(spawnCoordinates[0]), Integer.valueOf(spawnCoordinates[1]), Integer.valueOf(spawnCoordinates[2])));
            getLogger().info("Spawn at " + Integer.valueOf(spawnCoordinates[0]) + Integer.valueOf(spawnCoordinates[1]) + Integer.valueOf(spawnCoordinates[2]));
        }
        for (int i = 1; i <= getConfig().getInt("number-of-healing-stations"); i++) {
            String heal = this.getConfig().getString("healing-" + String.valueOf(i));
            String[] healCoordinates = heal.split(",");
            healingStations.add(new Location(default_world, Integer.valueOf(healCoordinates[0]), Integer.valueOf(healCoordinates[1]), Integer.valueOf(healCoordinates[2])));
            getLogger().info("Healing at " + Integer.valueOf(healCoordinates[0]) + Integer.valueOf(healCoordinates[1] + 1) + Integer.valueOf(healCoordinates[2]));
        }
        for (final Location healingStation : healingStations) {
            final Location loc1 = new Location(default_world, healingStation.getX(), healingStation.getY() + 1, healingStation.getZ());
            //final List<Location> effects = new ArrayList<Location>();
            //effects.add(new Location(default_world, healingStation.getX() + 3, healingStation.getY(), healingStation.getZ()));
            //effects.add(new Location(default_world, healingStation.getX() - 3, healingStation.getY(), healingStation.getZ()));
            //effects.add(new Location(default_world, healingStation.getX(), healingStation.getY(), healingStation.getZ() + 3));
            //effects.add(new Location(default_world, healingStation.getX(), healingStation.getY(), healingStation.getZ() - 3));
            //effects.add(new Location(default_world, healingStation.getX() + 1.8, healingStation.getY(), healingStation.getZ() + 1.8));
            //effects.add(new Location(default_world, healingStation.getX() - 1.8, healingStation.getY(), healingStation.getZ() + 1.8));
            //effects.add(new Location(default_world, healingStation.getX() + 1.8, healingStation.getY(), healingStation.getZ() - 1.8));
            //effects.add(new Location(default_world, healingStation.getX() - 1.8, healingStation.getY(), healingStation.getZ() - 1.8));
            new BukkitRunnable() {
                @Override
                public void run() {
                    default_world.playEffect(loc1, Effect.HEART, 10);
                    //for (Location effect : effects) {
                    //    default_world.playEffect(effect, Effect.FLAME, 100);
                    //}
                    for (Player p : getServer().getOnlinePlayers()) {
                        if (loc1.distance(p.getLocation()) < 5) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                        }
                    }
                }
            }.runTaskTimer(this, 0, 15);
        }

        minPlayers = getConfig().getInt("min-player");

        String lSpawnString = this.getConfig().getString("spawn-lobby");
        String[] lSpawnArray = lSpawnString.split(",");
        lobbySpawn = new Location(default_world, Integer.valueOf(lSpawnArray[0]), Integer.valueOf(lSpawnArray[1]), Integer.valueOf(lSpawnArray[2]));
    }

    @Override
    public void onDisable() {
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                if (!getDataFolder().mkdirs()) {
                    getLogger().warning("Cannot create config folder!");
                }
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @EventHandler
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("start_game") && args.length == 0) {
            this.startGame();
        } else if (cmd.getName().equalsIgnoreCase("stop_game") && args.length == 0) {
            this.stopGame();
        } else {
            return false;
        }
        return true;
    }

    int getNumberOfMagicians() {
        int result = 0;
        for (Object player_name: characters.keySet()) {
            if (Character.isObjectMagician(characters.get(player_name))) {
                result++;
            }
        }
        return result;
    }

    int getNumberOfAliveMagicians() {
        int result = 0;
        for (Object player_name: characters.keySet()) {
            if (Character.isObjectMagician(characters.get(player_name)) && getServer().getPlayer((String) player_name).getGameMode() != GameMode.SPECTATOR) {
                result++;
            }
        }
        return result;
    }

    int getNumberOfHumans() {
        int result = 0;
        for (Object player_name: characters.keySet()) {
            if (Character.isObjectHuman(characters.get(player_name))) {
                result++;
            }
        }
        return result;
    }

    int getNumberOfAliveHumans() {
        int result = 0;
        for (Object player_name: characters.keySet()) {
            if (Character.isObjectHuman(characters.get(player_name)) && getServer().getPlayer((String) player_name).getGameMode() != GameMode.SPECTATOR) {
                result++;
            }
        }
        return result;
    }

    void fillInventoryForLobby(Inventory inventory) {
        inventory.clear();
        for (int i = 0; i < 9; i++) {
            Character character = Character.getByID(i);
            if (character != null) {
                ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) i);
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(character.toString());
                item.setItemMeta(im);
                inventory.setItem(i, item);
            }
        }
        inventory.setItem(4, new ItemStack(Material.BARRIER, 1));
    }

    private void initInventoryForGame(Inventory inventory, Character character) {
        inventory.clear();
        for (Spell spell: character.getSpells()) {
            ItemStack item = new ItemStack(Material.INK_SACK, 1, (short) spell.getID());
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(spell.toString());
            item.setItemMeta(im);
            inventory.addItem(item);
        }
        for (Material item_material: character.getMaterials()) {
            ItemStack item = new ItemStack(item_material, 1);
            inventory.addItem(item);
        }
    }

    void startCountdown() {
        countdownTimer = 31;
        in_game_status = GameStatus.COUNTDOWN;
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                countdownTimer--;
                if (countdownTimer == 30) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 30 seconds");
                } else if (countdownTimer == 20) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 20 seconds");
                } else if (countdownTimer == 10) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 10 seconds");
                } else if (countdownTimer == 5) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 5 seconds");
                } else if (countdownTimer == 4) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 4 seconds");
                } else if (countdownTimer == 3) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 3 seconds");
                } else if (countdownTimer == 2) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 2 seconds");
                } else if (countdownTimer == 1) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting in 1 seconds");
                } else if (countdownTimer == 0) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Game starting now");
                    startGame();
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 10, 20);
    }

    void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            Bukkit.broadcastMessage(ChatColor.RED + "Aborted! Not enough player");
        }
        in_game_status = GameStatus.WAITING;
        countdownTask = null;
    }

    private void startGame() {
        startMana();
        int counter = 0;
        this.in_game_status = GameStatus.IN_GAME;
        for (Player player : getServer().getOnlinePlayers()) {
            if (characters.get(player.getName()) == null) {
                if (getNumberOfMagicians() >= getNumberOfHumans()) {
                    characters.put(player.getName(), Character.BUTCHER);
                } else {
                    characters.put(player.getName(), Character.GANDALF);
                }
            }
            initInventoryForGame(player.getInventory(), (Character) characters.get(player.getName()));
            player.teleport(spawns.get(counter));
            counter++;
        }
    }

    void stopGame() {
        stopMana();
        this.in_game_status = GameStatus.WAITING;
        for (Player player : getServer().getOnlinePlayers()) {
            fillInventoryForLobby(player.getInventory());
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(lobbySpawn);
        }
    }

    private void startMana() {
        manaTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (Character.isObjectMagician(characters.get(player.getName()))) {
                        mana.put(player.getName(), (Integer) mana.get(player.getName()) + 1);
                    }
                }
            }
        }.runTaskTimer(this, 0, 10);
    }

    private void stopMana() {
        if (manaTask != null) {
            manaTask.cancel();
            manaTask = null;
        }
    }

    private void updateXPBar() {
        for (Player player : getServer().getOnlinePlayers()) {
            String text = null;
            if (in_game_status == GameStatus.IN_GAME) {
                if (Character.isObjectMagician(characters.get(player.getName()))) {
                    text = mana.get(player.getName()) + " Mana";
                }
            } else {
                Character character = (Character) characters.get(player.getName());
                if (character != null) {
                    text = "You are a " + characters.get(player.getName());
                } else {
                    text = "You don't have any character.";
                }
            }
            if (text != null) {
                IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
                PacketPlayOutChat ppoc = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
            }
        }
    }
}
