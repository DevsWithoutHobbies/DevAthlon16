package de.DevsWithoutHobbies.Runde1;

import org.bukkit.event.EventHandler;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Zauberkrieg extends JavaPlugin {

    HashMap mana = new HashMap();
    HashMap magician = new HashMap();
    private List<Location> spawns = new ArrayList<Location>();
    private Location lobbySpawn;

    GameStatus in_game_status = GameStatus.WAITING;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);

        createConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                updateXPBar();
            }
        }.runTaskTimer(this, 0, 40);

        World default_world = getServer().getWorld("World");

        for (int i = 1; i < 9; i++) {
            String spawn = this.getConfig().getString("spawn-" + String.valueOf(i));
            String[] spawnCoordinates = spawn.split(",");
            spawns.add(new Location(default_world, Integer.valueOf(spawnCoordinates[0]), Integer.valueOf(spawnCoordinates[1]), Integer.valueOf(spawnCoordinates[2])));
        }

        String lSpawnString = this.getConfig().getString("spawn-lobby");
        String[] lSpawnArray = lSpawnString.split(",");
        lobbySpawn = new Location(default_world, Integer.valueOf(lSpawnArray[0]), Integer.valueOf(lSpawnArray[1]), Integer.valueOf(lSpawnArray[2]));
    }

    @Override
    public void onDisable() {}

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
        sender.sendMessage(cmd.getName());
        if (cmd.getName().equalsIgnoreCase("start_game") && args.length == 0) {
            this.startGame();
        } else if (cmd.getName().equalsIgnoreCase("stop_game") && args.length == 0) {
            this.stopGame();
        } else {
            return false;
        }
        return true;
    }

    Magician getMagicianByID(int id) {
        if (id == 0) {
            return Magician.GANDALF;
        } else if (id == 1) {
            return Magician.HARRY_POTTER;
        } else {
            return Magician.GANDALF;
        }
    }

    void fillInventoryForLobby(Inventory inventory) {
        inventory.clear();
        for (int i = 0; i < 9; i++) {
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) i);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(getMagicianByID(i).toString());
            item.setItemMeta(im);
            inventory.addItem(item);
        }
    }

    private void initInventoryForGame(Inventory inventory, Magician magician) {
        inventory.clear();
        int start_id = magician.getID() * 5;

        for (int i = 0; i < 5; i++) {
            ItemStack item = new ItemStack(Material.INK_SACK, 1, (short) (i + start_id));
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(getTrickName(i + start_id));
            item.setItemMeta(im);
            inventory.setItem(i, item);
        }
    }

    private String getTrickName(int id) {
        if (id == 0) {
            return "Explosion";
        } else if (id == 1) {
            return "Levitation";
        } else if (id == 2) {
            return "Door Opener";
        } else if (id == 3) {
            return "Arrow Shooter";
        } else if (id == 4) {
            return "Water";
        } else if (id == 5) {
            return "Lava";
        } else if (id == 6) {
            return "Snow Ball Shooter";
        } else if (id == 7) {
            return "Fireball";
        } else if (id == 8) {
            return "Slowness";
        } else if (id == 9) {
            return "Blindness";
        } else if (id == 10) {
            return "Poison";
        } else if (id == 11) {
            return "Teleportation";
        } else {
            return "Unknown";
        }
    }

    private void startGame() {
        int counter = 0;
        this.in_game_status = GameStatus.IN_GAME;
        for (Player player: getServer().getOnlinePlayers()) {
            initInventoryForGame(player.getInventory(), (Magician) magician.get(player.getName()));
            player.teleport(spawns.get(counter));
            counter++;
        }
    }

    private void stopGame() {
        this.in_game_status = GameStatus.WAITING;
        for (Player player: getServer().getOnlinePlayers()) {
            fillInventoryForLobby(player.getInventory());
            player.teleport(lobbySpawn);
        }
    }

    private void updateXPBar() {
        for (Player player : getServer().getOnlinePlayers()) {
            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + mana.get(player.getName()) + " Mana\"}");
            PacketPlayOutChat ppoc = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
        }
    }
}
