package de.DevsWithoutHobbies.Runde1;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Character {
    ELEMENTARIST(0, true, "Elementarist", asList(Spell.WATER, Spell.LAVA, Spell.FIREBALL, Spell.SAND_TOWER), asList()),
    EXPLOSIONIST(1, true, "Explosionist", asList(Spell.FIREBALL, Spell.EXPLOSION, Spell.LAVA), asList()),
    EFFECTIST(2, true, "Effectist", asList(Spell.LEVITATION, Spell.POISON, Spell.SLOWNESS, Spell.BLINDNESS, Spell.INVISIBILITY), asList()),
    SHOOTER(3, true, "Shooter", asList(Spell.ARROW_SHOOTER, Spell.SNOW_BALL_SHOOTER, Spell.FIREBALL, Spell.TELEPORTATION), asList()),
    BUTCHER(5, false, "Butcher", asList(), asList(new ItemStack(Material.WOOD_SWORD, 1), new ItemStack(Material.IRON_AXE, 1))),
    ARCHER(6, false, "Archer", asList(), asList(new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 32))),
    MINER(5, false, "Miner", asList(), asList(new ItemStack(Material.DIAMOND_PICKAXE, 1), new ItemStack(Material.IRON_CHESTPLATE, 1)));


    final int id;
    final boolean is_magician;
    final String name;
    final List<ItemStack> items;
    final List<Spell> spells;

    Character(int id, boolean is_magician, String name, List<Spell> spells, List<ItemStack> items) {
        this.id = id;
        this.is_magician = is_magician;
        this.name = name;
        this.spells = spells;
        this.items = items;
    }

    int getID() {
        return this.id;
    }

    boolean isMagician() {
        return is_magician;
    }

    boolean isHuman() {
        return !is_magician;
    }

    List<Spell> getSpells() {
        return this.spells;
    }

    List<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Character getByID(int id) {
        for(Character character : values()) {
            if(character.getID() == id) {
                return character;
            }
        }
        return null;
    }

    public static boolean isObjectMagician(Object object) {
        return object != null && ((Character) object).isMagician();
    }

    public static boolean isObjectHuman(Object object) {
        return object != null && ((Character) object).isHuman();
    }
}