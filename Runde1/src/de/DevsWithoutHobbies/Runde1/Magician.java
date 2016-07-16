package de.DevsWithoutHobbies.Runde1;

/**
 * Created by noah on 7/16/16.
 *
 */
enum Magician {
    GANDALF, HARRY_POTTER;

    int getID() {
        if (this == Magician.GANDALF) {
            return 0;
        } else if (this == Magician.HARRY_POTTER) {
            return 1;
        } else {
            return 0;
        }
    }
}