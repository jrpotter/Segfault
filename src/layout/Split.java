package segfault.layout;

import java.awt.Component;

/**
 * Split Class.
 *
 * Constraints class for use in layout management
 * (specifically the SplitLayout class). Specifies
 * whether the contained Component should be split
 * vertically or horizontally.
 *
 * @author: Joshua Potter
 * @version: 0.1
*/
public class Split {

    private Component key;
    private int orientation;

    // Constructors
    public Split() {
        this(null);
    }

    public Split(Component key) {
        this(key, SplitLayout.VERTICAL);
    }

    public Split(Component key, int orientation) {
        if(orientation != SplitLayout.VERTICAL
        && orientation != SplitLayout.HORIZONTAL)
            throw new IllegalArgumentException("Invalid orientation");

        this.key = key;
        this.orientation = orientation;
    }

    // Getters
    public Component getComponent() {
        return key;
    }

    public int getOrientation() {
        return orientation;
    }
}
