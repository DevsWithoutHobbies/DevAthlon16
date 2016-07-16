package de.DevsWithoutHobbies.Runde1;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;

public class Zauberkrieg extends JavaPlugin {

    HashMap mana = new HashMap();

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

    void fillInventoryForLobby(Inventory inventory) {
        inventory.clear();
        for (int i = 0; i < 9; i++) {
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) i);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName("Awesome effect");
            item.setItemMeta(im);
            inventory.addItem(item);
        }
    }

    void initInventoryForGame(Inventory inventory, Magician magician) {
        inventory.clear();
        resetInventoryForGame(inventory, magician);
    }

    void resetInventoryForGame(Inventory inventory, Magician magician) {
        for (int i = 0; i < 5; i++) {
            ItemStack item = new ItemStack(Material.INK_SACK, 1, (short) i);
            ItemMeta im = item.getItemMeta();
            im.setDisplayName("Awesome effect");
            item.setItemMeta(im);
            inventory.setItem(i, item);
        }
    }

    void startGame() {
        this.in_game_status = GameStatus.IN_GAME;
        for (Player player: getServer().getOnlinePlayers()) {
            initInventoryForGame(player.getInventory(), Magician.GANDALF);
        }
    }

    void stopGame() {
        this.in_game_status = GameStatus.WAITING;
        for (Player player: getServer().getOnlinePlayers()) {
            fillInventoryForLobby(player.getInventory());
        }
    }

    private void updateXPBar() {
        for (Player player : getServer().getOnlinePlayers()) {
            IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + mana.get(player.getName()) + " Mana\"}");
            PacketPlayOutChat ppoc = new PacketPlayOutChat(chatBaseComponent, (byte) 2);;
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(ppoc);
        }
    }
}
