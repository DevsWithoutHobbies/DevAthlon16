package de.DevsWithoutHobbies.Runde1;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

@SuppressWarnings("unchecked")
public class Zauberkrieg extends JavaPlugin {


    GameStatus in_game_status = GameStatus.WAITING;
    final HashMap mana = new HashMap();
    final HashMap characters = new HashMap();
    private final List<Location> spawns = new ArrayList<>();
    final List<Location> burningPlaces = new ArrayList<>();
    private final List<Location> healingStations = new ArrayList<>();
    private int burning_places_count;
    int burned = 0;
    final List<String> burningPeople = new ArrayList<>();
    Location lobbySpawn;
    Location respawn;
    int minPlayers;
    private BukkitTask countdownTask;
    private BukkitTask manaTask;
    private int countdownTimer;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        createConfig();

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
        burning_places_count = getConfig().getInt("number-of-burning-places");
        for (int i = 1; i <= burning_places_count; i++) {
            String heal = this.getConfig().getString("burn-" + String.valueOf(i));
            String[] healCoordinates = heal.split(",");
            burningPlaces.add(new Location(default_world, Integer.valueOf(healCoordinates[0]), Integer.valueOf(healCoordinates[1]), Integer.valueOf(healCoordinates[2])));
            getLogger().info("Healing at " + Integer.valueOf(healCoordinates[0]) + Integer.valueOf(healCoordinates[1] + 1) + Integer.valueOf(healCoordinates[2]));
        }
        for (final Location healingStation : healingStations) {
            final Location loc1 = new Location(default_world, healingStation.getX() + 0.5, healingStation.getY() + 1, healingStation.getZ() + 0.5);
            final List<Location> effects = new ArrayList<>();
            effects.add(new Location(default_world, loc1.getX(), loc1.getY(), loc1.getZ()));
            effects.add(new Location(default_world, loc1.getX(), loc1.getY() + 1, loc1.getZ()));
            effects.add(new Location(default_world, loc1.getX() + 1, loc1.getY(), loc1.getZ() + 1));
            effects.add(new Location(default_world, loc1.getX() - 1, loc1.getY(), loc1.getZ() + 1));
            effects.add(new Location(default_world, loc1.getX() + 1, loc1.getY(), loc1.getZ() - 1));
            effects.add(new Location(default_world, loc1.getX() - 1, loc1.getY(), loc1.getZ() - 1));
            new BukkitRunnable() {
                @Override
                public void run() {
                    //default_world.playEffect(loc1, Effect.HEART, 10);
                    for (Location effect : effects) {
                        default_world.playEffect(effect, Effect.HEART, 10);
                    }
                    getServer().getOnlinePlayers().stream().filter(p -> loc1.distance(p.getLocation()) < 5).forEachOrdered(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1)));
                }
            }.runTaskTimer(this, 0, 15);
        }

        minPlayers = getConfig().getInt("min-player");

        String lSpawnString = this.getConfig().getString("spawn-lobby");
        String[] lSpawnArray = lSpawnString.split(",");
        lobbySpawn = new Location(default_world, Integer.valueOf(lSpawnArray[0]), Integer.valueOf(lSpawnArray[1]), Integer.valueOf(lSpawnArray[2]));
        lSpawnString = this.getConfig().getString("respawn");
        lSpawnArray = lSpawnString.split(",");
        respawn = new Location(default_world, Integer.valueOf(lSpawnArray[0]), Integer.valueOf(lSpawnArray[1]), Integer.valueOf(lSpawnArray[2]));
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

    private void setBurningPlace(int id, Material materials) {
        Block mainBlock = burningPlaces.get(id).getBlock();
        mainBlock.getRelative(BlockFace.UP).setType(materials);
        mainBlock.getRelative(BlockFace.NORTH).setType(materials);
        mainBlock.getRelative(BlockFace.EAST).setType(materials);
        mainBlock.getRelative(BlockFace.WEST).setType(materials);
        mainBlock.getRelative(BlockFace.SOUTH).setType(materials);
    }

    void enableBurningPlace(int id) {
        setBurningPlace(id, Material.FIRE);
    }

    private void disableBurningPlace(int id) {
        setBurningPlace(id, Material.AIR);
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
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName("Leave Team");
        item.setItemMeta(im);
        inventory.setItem(4, item);
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
        character.getItems().forEach(inventory::addItem);
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
        this.burned = 0;
        for (Player player : getServer().getOnlinePlayers()) {
            mana.put(player.getName(), 0);
            player.setFoodLevel(20);
            player.setHealth(20);
            if (characters.get(player.getName()) == null) {
                if (getNumberOfMagicians() >= getNumberOfHumans()) {
                    characters.put(player.getName(), Character.BUTCHER);
                } else {
                    characters.put(player.getName(), Character.ELEMENTARIST);
                }
            }
            initInventoryForGame(player.getInventory(), (Character) characters.get(player.getName()));
            player.teleport(spawns.get(counter));
            counter++;
            if (characters.get(player.getName()) == Character.BUTCHER || characters.get(player.getName()) == Character.ARCHER || characters.get(player.getName()) == Character.MINER || characters.get(player.getName()) == Character.FARMER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 4, false, false));
                player.setHealth(40);
            }
        }

    }

    void stopGame() {
        stopMana();
        this.in_game_status = GameStatus.WAITING;
        this.burningPeople.clear();
        for (Player player : getServer().getOnlinePlayers()) {
            fillInventoryForLobby(player.getInventory());
            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.teleport(lobbySpawn);
            player.setFireTicks(0);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }
        for (int i = 0; i < burning_places_count; i++) {
            disableBurningPlace(i);
        }
    }

    private void startMana() {
        manaTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    Integer last_mana = (Integer) mana.get(player.getName());
                    int new_mana;
                    if (Character.isObjectMagician(characters.get(player.getName()))) {
                        new_mana = last_mana + 2;
                    } else {
                        new_mana = last_mana + 1;
                    }
                    int max_mana = Character.getMaxManaFromObject(characters.get(player.getName()));
                    if (max_mana < new_mana) {
                        new_mana = max_mana;
                    }
                    mana.put(player.getName(), new_mana);
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
            String text;
            if (in_game_status == GameStatus.IN_GAME) {
                text = mana.get(player.getName()) + " Mana";
            } else {
                Character character = (Character) characters.get(player.getName());
                if (character != null) {
                    text = "You are a " + characters.get(player.getName());
                } else {
                    text = "You don't have any character.";
                }
            }
            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
            PacketPlayOutChat ppoc = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
        }
    }
}
