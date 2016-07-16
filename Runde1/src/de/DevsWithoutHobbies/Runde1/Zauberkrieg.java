package de.DevsWithoutHobbies.Runde1;

import org.bukkit.plugin.java.JavaPlugin;

public class Zauberkrieg extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {

    }
}
