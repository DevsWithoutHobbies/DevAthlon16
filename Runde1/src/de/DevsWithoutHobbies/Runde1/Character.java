package de.DevsWithoutHobbies.Runde1;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Character {
    GANDALF(0, "Gandalf", asList(Spell.EXPLOSION, Spell.LEVITATION)),
    HARRY_POTTER(1, "Harry Potter", asList(Spell.LEVITATION)),
    OP(2, "OP", asList(Spell.EXPLOSION, Spell.LEVITATION, Spell.DOOR_OPENER, Spell.ARROW_SHOOTER, Spell.WATER, Spell.LAVA, Spell.SNOW_BALL_SHOOTER, Spell.FIREBALL, Spell.SLOWNESS, Spell.BLINDNESS, Spell.POISION, Spell.TELEPORTATION));


    String name;
    int id;
    List<Spell> speels;

    Character(int id, String name, List<Spell> spells) {
        this.name = name;
        this.speels = spells;
        this.id = id;
    }

    int getID() {
        return this.id;
    }

    List<Spell> getSpells() {
        return this.speels;
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
        return GANDALF;
    }
}