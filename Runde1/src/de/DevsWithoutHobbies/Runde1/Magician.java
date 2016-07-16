package de.DevsWithoutHobbies.Runde1;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Magician {
    GANDALF(0, "Gandalf", asList(Spell.EXPLOSION, Spell.LEVITATION)),
    HARRY_POTTER(1, "Harry Potter", asList(Spell.LEVITATION));


    String name;
    int id;
    List<Spell> speels;

    Magician(int id, String name, List<Spell> spells) {
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

    public static Magician getByID(int id) {
        for(Magician magician: values()) {
            if(magician.getID() == id) {
                return magician;
            }
        }
        return null;
    }
}