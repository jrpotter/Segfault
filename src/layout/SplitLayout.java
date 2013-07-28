package segfault.layout;

import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager2;

// SplitNode Specifics
import javax.swing.JSeparator;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.ArrayDeque;

/**
 * @author: Joshua Potter
 * @version: 0.1
*/ 
public class SplitLayout implements LayoutManager2 {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    /**
     * A left substree denotes a vertical split, right - horizontal.
    */
    public class SplitNode {

        private Component comp;

        private JSeparator v_divider;
        private JSeparator h_divider;
        private SplitNode left, right;

        public SplitNode() {
            this(null);
        }

        public SplitNode(Component c) {
            comp = c;
            left = null;
            right = null;
            v_divider = null;
            h_divider = null;
        }

        public void addNode(Component c, int orientation) {
            SplitNode next = new SplitNode(c);

            if(orientation == HORIZONTAL) {

                if(right == null) {
                    right = next;
                    h_divider = new JSeparator(orientation);
                    h_divider.addMouseMotionListener(new MouseMotionAdapter() {
                        
                        @Override
                        public void mouseDragged(MouseEvent e) {

                        } 

                    });
                } else {
                    SplitNode tmp = right;
                    right = next;
                    next.right = tmp;
                }


            } else if(orientation == VERTICAL) {

                if(right == null) {
                    left = next;
                    v_divider = new JSeparator(orientation);
                    v_divider.addMouseMotionListener(new MouseMotionAdapter() {
                        
                        @Override
                        public void mouseDragged(MouseEvent e) {

                        } 

                    });
                } else {
                    SplitNode tmp = left;
                    left = next;
                    next.left = tmp;
                }
                

            } else throw new IllegalArgumentException("Invalid orientation specified.");
        }

    }

    /**
    */
    private SplitNode root;

    public SplitLayout() {
        root = null;
    }

    // Root must not be null when invoked
    private void split(Component c, int orientation) {

        ArrayDeque<SplitNode> search = new ArrayDeque<SplitNode>();
        search.add(root);

        while(!search.isEmpty()){
            SplitNode s = search.removeFirst();
            if(s.comp == c) {
                s.addNode(c, orientation);
                return;
            } else {
                if(s.left != null) search.add(s.left);
                if(s.right != null) search.add(s.right); 
            }
        }
    }

    /**
    */
    @Override
    public void addLayoutComponent(String name, Component c) {
        // Add to left or right
    }

    @Override
    public void addLayoutComponent(Component c, Object constraints) {
        // TODO: Java dynamic casting
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
