package segfault.layout;

import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * @author: Joshua Potter
 * @version: 0.1
*/ 
public class SplitLayout implements LayoutManager2 {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    private SplitNode root;

    public SplitLayout() {
        root = null;
    }

    @Override
    public void addLayoutComponent(String name, Component c) {
        // Add to left or right
    }

    @Override
    public void addLayoutComponent(Component c, Object constraints) {
        if(constraints instanceof SplitConstraints) {

        } else throw new IllegalArgumentException();
    }

    @Override
    public void removeLayoutComponent(Component c) {
        // Clear internal state regarding component
    }

    @Override
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    @Override
    public void layoutContainer(Container parent) {
        // Position and size components in container
        // invokes setSize, setLocation, setBounds
        // must check getInsets and componentorientation
    }

    @Override
    public void invalidateLayout(Container parent) {
        root = null;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        //Size of left and right nodes of model
        return null;
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return null;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return null;
    }

}
