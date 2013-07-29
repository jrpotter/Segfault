package segfault.core;

// Swing
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Container;
import javax.swing.JSeparator;
import java.awt.LayoutManager2;

// Events
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

// Utils
import java.util.ArrayDeque;


/**
 * The SplitLayout provides an interface in which
 * one can work on multiple sliding panes, without
 * dealing with the recursiveness of JSplitPanes.
 *
 * The bulk of the organization resides in the SplitNode,
 * providing a tree model that maps well with the idea
 * of nested JSplitPanes. A further explanation can be
 * found in the comment before the class definition.
 *
 * @author: Joshua Potter
 * @version: 0.1
*/ 
public class SplitLayout implements LayoutManager2 {

    // Mirrors SwingConstants
    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;


    /**
     * The following allows the user to specify which
     * component in the layout should be split and
     * whether or not that split is verical or horizontal.
    */
    public static class SplitConstraints {

        private Component split;
        private int orientation;

        public SplitConstraints(Component split, int orientation) {
            if(orientation != VERTICAL || orientation != HORIZONTAL)
                throw new IllegalArgumentException("Invalid orientation specified");

            this.split = split;
            this.orientation = orientation;
        }

        // Getters
        public Component getSplit() {
            return split;
        }

        public int getOrientation() {
            return orientation;
        }

    }

    
    /**
     * The split node works by abstracting the idea of recursive
     * JSplitPanes to a tree (a naturally recursive model).
     *
     * A left subtree denotes a vertical split and a right subtree
     * denotes a horizontal one. Note that during laying, the tree
     * is traversed depth-first, in a preorder manner moving from
     * from the right subtree (horizontal elements) to the left.
    */
    private class SplitNode extends MouseMotionAdapter {

        private Component key;
        private JSeparator h_div, v_div;
        private SplitNode left, right, parent;

        public SplitNode() {
            this(null);
        }

        public SplitNode(Component key) {
            this(key, null);
        }

        public SplitNode(Component key, SplitNode parent) {
            this.key = key;
            this.parent = parent;

            left = right = null;
            h_div = v_div = null;
        }

        
        // Helper Methods
        private void replace(SplitNode s, SplitNode r) {
            if(left == s) left = r;
            else if(right == s) right = r;
        }


        /** 
         * Insertion methods.
         *
         * An insertion works in the same manner for both
         * the left and right subtree, though mirrored. A 
         * right subtree is built (or extended) if a horizontal 
         * split is requested on a component. The left subtree
         * does the same if a vertical split is requested.
        */
        private void addNode(Component c, int orientation) {
            if(orientation == VERTICAL) {
                SplitNode tmp = left;
                left = new SplitNode(c, this);
                left.left = tmp;

                // First time a veritcal insertion is made on this node
                if(tmp == null) {
                    v_div = new JSeparator(orientation);
                    v_div.addMouseMotionListener(this);
                }    
            } else if(orientation == HORIZONTAL) {
                SplitNode tmp = right;
                right = new SplitNode(c, this);
                right.right = tmp;

                // First time a horizontal insertion is made on this node
                if(tmp == null) {
                    h_div = new JSeparator(orientation);
                    h_div.addMouseMotionListener(this);
                }    
            } else {
                throw new IllegalArgumentException("Invalid orientation specified.");
            }
        }

        /**
         * @c: Component to find
         * @ins: Component to insert into found node
         * @orientation: Vertical or horizontal insertion
        */
        public void insert(Component c, Component ins, int orientation) {

            ArrayDeque<SplitNode> search = new ArrayDeque<SplitNode>();
            search.add(this);

            while(!search.isEmpty()) {
                SplitNode s = search.removeFirst();
                
                if(s.key == c) {
                    s.addNode(ins, orientation);
                    return;
                } else {
                    if(s.left != null) search.add(s.left);
                    if(s.right != null) search.add(s.right);
                }
            }
        }


        /** 
         * Removal Methods.
         * 
         * Searches its subtrees for the node containing
         * the matching passed component, and removing said node
         * from the tree. The removed node's right tree serves as
         * a replacement unless it is null, in which the left tree
         * takes accepts the role. 
         *
         * This allows all horizontal components to remain above
         * all vertical components or the vertical component
         * to move up to fill the empty space.
        */
        public void remove(Component c) {
            
            ArrayDeque<SplitNode> search = new ArrayDeque<SplitNode>();
            search.add(this);

            while(!search.isEmpty()) {
                SplitNode s = search.removeFirst();
                
                if(s.key == c) {
                    if(right != null) {

                        // Attach left tree to leftmost node of right
                        SplitNode tmp = right;
                        while(tmp.left != null) {
                            tmp = tmp.left;
                        }
                        tmp.left = left;

                        s.parent.replace(s, right);
                    } else {
                        s.parent.replace(s, left);
                    }

                    return;
                } else {
                    if(s.left != null) search.add(s.left);
                    if(s.right != null) search.add(s.right);
                }
            }
        }


        /**
         * Size Methods.
         *
         * Return the current nodes corresponding dimensions.
         * Finds the left and right subdimensions, adding them
         * to the current node's component's dimensions.
        */
        private void add(Dimension fst, Dimension snd) {
            fst.width += snd.width;
            fst.height = Math.max(fst.height, snd.height);
        }


        public Dimension getMinimumSize() {
            Dimension base = new Dimension(0, 0);
            
            // Find size of subtrees
            if(left != null) add(base, left.getMinimumSize());
            if(right != null) add(base, right.getMinimumSize());

            // Find size of separators
            if(h_div != null) add(base, h_div.getMinimumSize());
            if(v_div != null) add(base, v_div.getMinimumSize());

            // Find size of current component
            if(key != null) add(base, key.getMinimumSize());

            return base;
        }

        public Dimension getMaximumSize() {
            Dimension base = new Dimension(0, 0);
            
            // Find size of subtrees
            if(left != null) add(base, left.getMaximumSize());
            if(right != null) add(base, right.getMaximumSize());

            // Find size of separators
            if(h_div != null) add(base, h_div.getMaximumSize());
            if(v_div != null) add(base, v_div.getMaximumSize());

            // Find size of current component
            if(key != null) add(base, key.getMaximumSize());

            return base;
        }

        public Dimension getPreferredSize() {
            Dimension base = new Dimension(0, 0);
            
            // Find size of subtrees
            if(left != null) add(base, left.getPreferredSize());
            if(right != null) add(base, right.getPreferredSize());

            // Find size of separators
            if(h_div != null) add(base, h_div.getPreferredSize());
            if(v_div != null) add(base, v_div.getPreferredSize());

            // Find size of current component
            if(key != null) add(base, key.getPreferredSize());

            return base;
        }


        /**
         * Event Handling.
         *
         * If the horizontal bar is dragged, the widths
         * of the current component and all components in the
         * right subtree must be recalculated. 
         *
         * If the vertical bar is dragged, the heights of
         * the current component and all components in the 
         * left subtree must be recalculated.
        */
        @Override
        public void mouseDragged(MouseEvent e) {
            if(e.getSource() == h_div) {

            } else if(e.getSource() == v_div) {

            }
        }
    }

    
    /**
     * The root is the only node that does not have
     * any component associated with it. 
     *
     * This allows easier reasoning in insertions 
     * and removals since the root cannot change.
    */
    private SplitNode root;

    public SplitLayout() {
        root = new SplitNode();
    }


    /**
     * Layout Manager 2 Methods.
     *
     * The rest of the code implements the derived functions
     * from LayoutManager2 (each of which have various degrees
     * of importance).
    */

    @Override
    public void addLayoutComponent(String name, Component c) {
        // N/A
    } 

    @Override
    public void addLayoutComponent(Component ins, Object constraints) {
        if(constraints instanceof SplitConstraints) {
            // Get Data
            SplitConstraints s = (SplitConstraints) constraints;
            Component split = s.getSplit();
            int orientation = s.getOrientation();

            root.insert(split, ins, orientation);
        } else {
            throw new IllegalArgumentException("Expecting SplitConstraints");
        }
    }

    @Override
    public void removeLayoutComponent(Component c) {
        root.remove(c);
    }


    /** 
     * Alignment methods.
     *
     * The components should always be center aligned considering
     * they reclaim any open (or newly opened) space.
    */
    @Override
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    
    /**
     * Size Methods.
     *
     * Recursively builds up the size of the layout by 
     * going through left and right subtrees. 
    */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return root.getMinimumSize();
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return root.getMaximumSize();
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return root.getPreferredSize();
    }


    /**
     * Layout Methods.
     *
     * The most important methods. The following two functions
     * will reset the tree and traverse the tree, setting up all 
     * component positions respectively.
    */
    @Override
    public void invalidateLayout(Container parent) {
        root = new SplitNode();
    } 

    @Override
    public void layoutContainer(Container parent) {

    }
}
