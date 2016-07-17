package de.DevsWithoutHobbies.Runde1;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Spell {
    EXPLOSION(0, "Explosion", 30),
    LEVITATION(1, "Levitation", 10),
    DOOR_OPENER(2, "Door Opener", 5),
    ARROW_SHOOTER(3, "Arrow Shooter", 30),
    WATER(4, "Water", 10),
    LAVA(5, "Lava", 20),
    SNOW_BALL_SHOOTER(6, "Snow Ball Shooter", 30),
    FIREBALL(7, "Fireball", 50),
    SLOWNESS(8, "Slowness", 40),
    BLINDNESS (9, "Blindness", 40),
    POISON(10, "Poison", 40),
    TELEPORTATION(11, "Teleportation", 60),
    INVISIBILITY(12, "Invisibility", 60),
    SAND_TOWER(13, "Sand Tower", 5);

    final String name;
    final int id;
    final int mana;

    Spell(int id, String name, int mana) {
        this.id = id;
        this.name = name;
        this.mana = mana;
    }

    int getID() {
        return this.id;
    }

    int getCost() {
        return this.mana;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Spell getByID(int id) {
        for(Spell spell: values()) {
            if(spell.getID() == id) {
                return spell;
            }
        }
        return null;
    }
}
