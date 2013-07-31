package segfault.core;

// Swing
import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.Insets;
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
 * The bulk of the organization resides in the Node,
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
    public static class Split {

        private Component split;
        private int orientation;

        public Split(Component split, int orientation) {
            if(orientation != VERTICAL && orientation != HORIZONTAL)
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
    private class Node extends MouseMotionAdapter {

        private Node parent;
        private Component key;

        // Vertical Splits
        private Node left;
        private double v_weight;
        private JSeparator v_div;

        // Horizontal Splits
        private Node right;
        private double h_weight;
        private JSeparator h_div;

        // Constructors
        public Node() {
            this(null);
        }

        public Node(Component key) {
            this(key, null);
        }

        public Node(Component key, Node parent) {
            this.key = key;
            this.parent = parent;

            left = right = null;
            h_div = v_div = null;
            h_weight = v_weight = 1.d;
        }

        
        // Convenience Methods
        private void replace(Node s, Node r) {
            if(left == s) left = r;
            else if(right == s) right = r;
        }

        private void add(Dimension fst, Dimension snd) {
            fst.width += snd.width;
            fst.height = Math.max(fst.height, snd.height);
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
                Node tmp = left;
                left = new Node(c, this);
                left.left = tmp;

                // First time a veritcal insertion is made on this node
                if(tmp == null) {
                    v_div = new JSeparator(orientation);
                    v_div.addMouseMotionListener(this);
                }    
            } else if(orientation == HORIZONTAL) {
                Node tmp = right;
                right = new Node(c, this);
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

            ArrayDeque<Node> search = new ArrayDeque<Node>();
            search.add(this);

            while(!search.isEmpty()) {
                Node s = search.removeFirst();
                
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
            
            ArrayDeque<Node> search = new ArrayDeque<Node>();
            search.add(this);

            while(!search.isEmpty()) {
                Node s = search.removeFirst();
                
                if(s.key == c) {
                    if(right != null) {

                        // Attach left tree to leftmost node of right
                        Node tmp = right;
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
         * Layout Container.
         *
         * In order to adjust all components in one pass, weights
         * are passed down a tree, and the collective weights
         * from all subtrees are passed back up.
         *
         * For example, if given a node with weight 1 (100%) and
         * a subtree of 2 nodes also with weight 1, 1 is passed down
         * 3 times for a total of 3 at the bottom node. The percentage
         * of space taken at the bottom node is therefore 1/3, which
         * is given to the parent node for handling (2/3) and then back
         * to the original for (3/3) or 100% of the space.
        */
        public void adjustSize(Dimension parent) {
            adjustSize(new Rectangle(parent), 0.d, 0.d, VERTICAL);
        }

        // Double only returned by right tree
        private void adjustSize(Rectangle free, double v_total, double h_total, int orientation) {

            // Adjust totals
            v_total += v_weight;
            h_total += h_weight;
            Rectangle tmp = new Rectangle(free);

            // Pass to vertical components
            if(left != null) left.adjustSize(free, v_total, 0.d, VERTICAL);

            // Reclaims space from free region
            if(orientation == VERTICAL) {
                free.height -= free.height * (v_weight / v_total);
                tmp.y = free.height;
                tmp.height -= free.height;
            } else if(orientation == HORIZONTAL) {
                free.width -= free.width * (h_weight / h_total);
                tmp.x = free.width;
                tmp.width -= free.width;
            }

            // Pass to horizontal components
            if(right != null) right.adjustSize(tmp, 0.d, h_total, HORIZONTAL);

            // Set component in place
            if(key != null) key.setBounds(tmp);
        }


        /**
         * Event Handling.
         *
         * If the horizontal bar is dragged, the weights
         * of the current component and the right component
         * must be recalculated.
         *
         * If the vertical bar is dragged, the weights of
         * the current component and the left component
         * must be recalculated.
        */
        @Override
        public void mouseDragged(MouseEvent e) {
            if(e.getSource() == h_div) {

                // Find percentage change
                double delta = h_div.getX() - e.getX();
                double p = Math.abs(delta) / key.getWidth();

                if(delta < 0) {
                    h_weight -= p;
                    right.h_weight += p;
                } else {
                    h_weight += p;
                    right.h_weight -= p;
                }

                // Translate to the left or right
                h_div.setLocation(e.getX(), h_div.getY());

            } else if(e.getSource() == v_div) {

                // Find percentage change
                int delta = h_div.getY() - e.getY();
                double p = Math.abs(delta) / key.getHeight();

                if(delta < 0) {
                    v_weight -= p;
                    left.v_weight += p;
                } else {
                    v_weight += p;
                    left.v_weight -= p;
                }

                // Translate to the left or right
                v_div.setLocation(v_div.getX(), e.getY());
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
    private Node root = new Node();


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
        if(constraints instanceof Split) {
            // Get Data
            Split s = (Split) constraints;
            Component split = s.getSplit();
            int orientation = s.getOrientation();

            root.insert(split, ins, orientation);
        } else {
            throw new IllegalArgumentException("Expecting Split");
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

    } 

    @Override
    public void layoutContainer(Container parent) {
        Insets inset = parent.getInsets();        
        Dimension space = new Dimension(0, 0);

        space.width = parent.getWidth() - inset.left - inset.right;
        space.height = parent.getHeight() - inset.top - inset.bottom;

        root.adjustSize(space);
    }
}
