package segfault.layout;

import java.awt.Component;


/**
 * The following allows the user to specify which
 * component in the layout should be split via the
 * already present methods in java swing container
 * methods (namely adding with constraint).
 *
 * @author: Joshua Potter
 * @version: 0.1
*/
public class SplitConstraints {
    private Component c;
    private int orientation;

    // Used if orientation is unimportant (i.e. single element)
    public SplitConstraints(Component c) {
        this.c = c;
        orientation = -1;
    }

    // Used most of time
    public SplitConstraints(Component c, int orientation) {
        if(orientation != SplitLayout.VERTICAL 
        || orientation != SplitLayout.HORIZONTAL)
            throw new IllegalArgumentException("Invalid orientation specified");

        this.c = c;
        this.orientation = orientation;
    }

    public Component getComponent() {
        return c;
    }

    public int getOrientation() {
        return orientation;
    }
}
