package de.DevsWithoutHobbies.Runde1;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Spell {
    EXPLOSION(0, "Explosion"),
    LEVITATION(1, "Levitation"),
    DOOR_OPENER(2, "Door Opener"),
    ARROW_SHOOTER(3, "Arrow Shooter"),
    WATER(4, "Water"),
    LAVE(5, "Lave"),
    SNOW_BALL_SHOOTER(6, "Snow Ball Shooter"),
    FIREBALL(7, "Fireball"),
    SLOWNESS(8, "Slowness"),
    BLINDNESS (9, "Blindness"),
    POISION(10, "Poison"),
    TELEPORTATION(11, "Teleportation");

    String name;
    int id;

    Spell(int id, String name) {
        this.id = id;
        this.name = name;
    }

    int getID() {
        return this.id;
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
