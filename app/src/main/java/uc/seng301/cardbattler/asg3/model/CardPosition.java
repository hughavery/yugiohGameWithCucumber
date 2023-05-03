package uc.seng301.cardbattler.asg3.model;

/**
 * Represents the cards position (either Attacking or Defending)
 */
public enum CardPosition {
    ATTACK("attacking"),
    DEFEND("defending");

    public final String label;

    CardPosition(String label) {
        this.label = label;
    }

    /**
     * Get the CardPosition from its label
     * 
     * @param label card position label to look-up
     * @return The matching CardPosition or null if there is no match
     */
    public static CardPosition valueOfLabel(String label) {
        for (CardPosition e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
