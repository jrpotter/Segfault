package segfault.core;

import javax.swing.JSeparator;

import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * @author: Joshua Potter
 * @version: 0.1
*/ 
public class SplitLayout implements LayoutManager2 {

    /**
    */
    public class Divider extends JSeparator implements MouseMotionListener {

        public Divider() {

        }

        public Divider(int orientation) {
            super(orientation);
        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            System.out.println(e.getX() + " " + e.getY());
        }
    }

    /**
    */
    private class SplitNode {
        Divider separator;
        Component left, right;

        public SplitNode() {
            separator = null;
            left = right = null;
        }
    }

    /**
    */
    public SplitLayout() {

    }

    @Override
    public void addLayoutComponent(String name, Component c) {

    }

    @Override
    public void addLayoutComponent(Component c, Object constraints) {

    }

    @Override
    public void removeLayoutComponent(Component c) {

    }

    @Override
    public float getLayoutAlignmentX(Container parent) {
        return 0.f;
    }

    @Override
    public float getLayoutAlignmentY(Container parent) {
        return 0.f;
    }

    @Override
    public void layoutContainer(Container parent) {

    }

    @Override
    public void invalidateLayout(Container parent) {

    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
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
