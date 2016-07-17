package de.DevsWithoutHobbies.Runde1;

import org.bukkit.Material;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Character {
    GANDALF(0, true, "Gandalf", asList(Spell.EXPLOSION, Spell.LEVITATION), asList()),
    HARRY_POTTER(1, true, "Harry Potter", asList(Spell.LEVITATION), asList()),
    OP(2, true, "OP", asList(Spell.EXPLOSION, Spell.LEVITATION, Spell.DOOR_OPENER, Spell.ARROW_SHOOTER, Spell.WATER, Spell.LAVA, Spell.SNOW_BALL_SHOOTER, Spell.FIREBALL, Spell.SLOWNESS, Spell.BLINDNESS, Spell.POISON, Spell.TELEPORTATION), asList()),
    BUTCHER(5, false, "Butcher", asList(), asList(Material.WOOD_SWORD, Material.IRON_AXE));


    int id;
    boolean is_magician;
    String name;
    List<Spell> speels;
    List<Material> items;

    Character(int id, boolean is_magician, String name, List<Spell> spells, List<Material> items) {
        this.id = id;
        this.is_magician = is_magician;
        this.name = name;
        this.speels = spells;
        this.items = items;
    }

    int getID() {
        return this.id;
    }

    boolean isMagician() {
        return id < 4;
    }

    boolean isHuman() {
        return !this.isMagician();
    }

    List<Spell> getSpells() {
        return this.speels;
    }

    List<Material> getMaterials() {
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