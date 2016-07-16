package de.DevsWithoutHobbies.Runde1;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


/**
 * Created by noah on 7/16/16.
 *
 */
public class Zauber {

    private int id;

    public Zauber(int id) {
        this.id = id;
    }

    public void execute(Player player) {
        if (this.id == 0) {
            Vector direction = player.getEyeLocation().getDirection().normalize();
            for (int i = 1; i < 10; i++) {
                Location loc = player.getEyeLocation().add(direction.multiply(i));
            }
        }
    }
}