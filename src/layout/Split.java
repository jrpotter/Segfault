package segfault.layout;

import java.awt.Component;

/**
 * Split Class.
 * Constraints class for use in layout management
 * (specifically the SplitLayout class). 
 *
 * Specifies whether the contained Component should 
 * be split vertically or horizontally as well as the order
 * in which a nodes subtree should be traversed.
 *
 * @author: Joshua Potter
 * @version: 0.1
*/
public class Split {

    private Component key;

    // Traverse left or right first
    private int priority;

    // Splitting vertically or horizontall
    private int orientation;

    // Constructors
    public Split(int priority) {
        this(null, SplitLayout.VERTICAL, priority);
    }

    public Split(Component key, int orientation) {
        this(key, orientation, SplitLayout.VERTICAL);
    }

    public Split(Component key, int orientation, int priority) {

        if(orientation != SplitLayout.VERTICAL
        && orientation != SplitLayout.HORIZONTAL)
            throw new IllegalArgumentException("Invalid orientation");

        this.key = key;
        this.priority = priority;
        this.orientation = orientation;
    }

    // Getters
    public Component getComponent() {
        return key;
    }

    public int getPriority() {
        return priority;
    }

    public int getOrientation() {
        return orientation;
    }
}
