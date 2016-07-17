package de.DevsWithoutHobbies.Runde1;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Character {
    ELEMENTARIST(0, true, 300, "Elementarist", asList(Spell.WATER, Spell.LAVA, Spell.FIREBALL, Spell.SAND_TOWER), Collections.emptyList()),
    EXPLOSIONIST(1, true, 300, "Explosionist", asList(Spell.FIREBALL, Spell.EXPLOSION, Spell.LAVA), Collections.emptyList()),
    EFFECTIST(2, true, 300, "Effectist", asList(Spell.LEVITATION, Spell.POISON, Spell.SLOWNESS, Spell.BLINDNESS, Spell.INVISIBILITY), Collections.emptyList()),
    SHOOTER(3, true, 300, "Shooter", asList(Spell.ARROW_SHOOTER, Spell.SNOW_BALL_SHOOTER, Spell.FIREBALL, Spell.TELEPORTATION), Collections.emptyList()),
    BUTCHER(5, false, 300, "Butcher", Collections.emptyList(), asList(new ItemStack(Material.WOOD_SWORD, 1), new ItemStack(Material.IRON_AXE, 1), new ItemStack(Material.LEATHER_CHESTPLATE, 1), new ItemStack(Material.LEATHER_LEGGINGS, 1), new ItemStack(Material.SHIELD, 1))),
    ARCHER(6, false, 300, "Archer", Collections.emptyList(), asList(new ItemStack(Material.WOOD_SWORD, 1), new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 32), new ItemStack(Material.LEATHER_CHESTPLATE, 1), new ItemStack(Material.SHIELD, 1))),
    MINER(7, false, 300, "Miner", Collections.emptyList(), asList(new ItemStack(Material.DIAMOND_PICKAXE, 1), new ItemStack(Material.IRON_CHESTPLATE, 1), new ItemStack(Material.IRON_LEGGINGS, 1), new ItemStack(Material.SHIELD, 1))),
    FARMER(8, false, 300, "Farmer", Collections.emptyList(), asList(new ItemStack(Material.IRON_AXE, 1), new ItemStack(Material.EGG, 16), new ItemStack(Material.EGG, 16), new ItemStack(Material.LEATHER_CHESTPLATE, 1), new ItemStack(Material.LEATHER_LEGGINGS, 1), new ItemStack(Material.SHIELD, 1)));


    final int id;
    final boolean is_magician;
    final String name;
    final List<ItemStack> items;
    final List<Spell> spells;
    final int max_mana;

    Character(int id, boolean is_magician, int max_mana, String name, List<Spell> spells, List<ItemStack> items) {
        this.id = id;
        this.is_magician = is_magician;
        this.name = name;
        this.spells = spells;
        this.items = items;
        this.max_mana = max_mana;
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

    public static int getMaxManaFromObject(Object object) {
        if (object != null) {
            return ((Character) object).max_mana;
        } else {
            return 0;
        }
    }
}