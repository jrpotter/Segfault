package segfault.layout;

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
import java.util.LinkedList;

/**
 * SplitLayout Class.
 *
 * The SplitLayout provides a non-recursive interface
 * for managing multiple splitted widgets. The bulk of 
 * organization resides in the Node nested class, a 
 * tree structure that maps well to the recursive nature 
 * of nested splits.
 *
 * A further explanation can be found in the comment before
 * the Node class definition.
 *
 * @author: Joshua Potter
 * @version: 0.1
*/
public class SplitLayout implements LayoutManager2 {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    /**
     * Node Class.
     *
     * The Node serves as an abstraction of recursively
     * nested split panes. A left subtree denotes a 
     * vertical split; a right subtree denotes a
     * horizontal split.
     *
     * Note that during laying the tree is traversed
     * depth-first, in a preorder manner moving from the 
     * left subtree to the right.
    */
    private class Node extends MouseMotionAdapter {
        
        private Node parent;
        private Component key;

        // Split Members
        private Node left, right;
        private JSeparator v_sep, h_sep;
        private double v_weight, h_weight;

        // Constructors
        public Node(Component key) {
            this(key, null);
        }

        public Node(Component key, Node parent) {
            this.key = key;
            this.parent = parent;

            left = right = null;
            v_sep = h_sep = null;
            v_weight = h_weight = 1.d;
        }


        /**
         * Helper Methods.
         *
         * Push appends the left subtree of the current
         * node to the leftmost position of the right
         * subtree (if not null).
         *
         * Widen builds up a dimension in a rectangular
         * region with fixed height (fixed used rather 
         * loosely- a minimum sized widget with greater
         * height can not be shortened).
        */
        private void push() {
            if(right != null) {
                Node tmp = right;
                while(tmp.left != null) {
                    tmp = tmp.left;
                }
                tmp.left = left;
            }
        }

        private void widen(Dimension fst, Dimension snd) {
            fst.width += snd.width;
            fst.height = Math.max(fst.height, snd.height);
        }


        /**
         * Insertion Methods.
         *
         * The right subtree of a node is extended if
         * a horizontal split is requested (the left
         * subtree otherwise). 
        */
        private void split(Component ins, int orientation) {
            if(orientation == VERTICAL) {

                Node tmp = left;
                left = new Node(ins, this);
                left.left = tmp;

                // Separator was not yet created
                if(tmp == null) {
                    v_sep = new JSeparator(orientation);
                    v_sep.addMouseMotionListener(this);
                }

            } else if(orientation == HORIZONTAL) {

                Node tmp = right;
                right = new Node(ins, this);
                right.right = tmp;

                if(tmp == null) {
                    h_sep = new JSeparator(orientation);
                    h_sep.addMouseMotionListener(this);
                }                
            }
        }

        // First find @find and split if found
        public void insert(Component find, Component ins, int orientation) {

            if(orientation != VERTICAL && orientation != HORIZONTAL)
                throw new IllegalArgumentException("Invalid orientation");
            
            LinkedList<Node> search = new LinkedList<Node>();
            search.add(this);

            while(!search.isEmpty()) {
                Node s = search.removeFirst();

                if(s.key == find) {
                    s.split(ins, orientation);
                    break;
                } else {
                    if(s.left != null) search.add(s.left);
                    if(s.right != null) search.add(s.right);
                }
            }
        }

        
        /**
         * Removal Methods.
         *
         * Searches subtrees until a node containing
         * the matching passed component is found. At 
         * this point the parent (if not null) adopts
         * the current nodes children. 
         *
         * If the parent is null replaces itself with
         * the left node and appends the right node
         * on the left node's rightmost element.
        */
        private void unsplit(Node self) {

            // Root Node
            if(parent == null) {
                if(right == null) self = left;
                else { push(); self = right; }
                return;
            }

            // Non-Root Node - must change parent links
            if(parent.left == this) {
                if(right == null) parent.left = left;
                else { push(); parent.left = right; }
            } else {
                if(right == null) parent.right = left;
                else { push(); parent.right = right; }
            }
        }

        // Find @find, deleting node if found
        public void remove(Component find) {

            LinkedList<Node> search = new LinkedList<Node>();
            search.add(this);

            while(!search.isEmpty()) {
                Node s = search.removeFirst();

                if(s.key == find) {
                    s.unsplit(s);
                    break;
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
            if(left != null) widen(base, left.getMinimumSize());
            if(right != null) widen(base, right.getMinimumSize());

            // Find size of separators
            if(h_sep != null) widen(base, h_sep.getMinimumSize());
            if(v_sep != null) widen(base, v_sep.getMinimumSize());

            // Find size of current component
            if(key != null) widen(base, key.getMinimumSize());

            return base;
        }

        public Dimension getMaximumSize() {
            Dimension base = new Dimension(0, 0);
            
            // Find size of subtrees
            if(left != null) widen(base, left.getMaximumSize());
            if(right != null) widen(base, right.getMaximumSize());

            // Find size of separators
            if(h_sep != null) widen(base, h_sep.getMaximumSize());
            if(v_sep != null) widen(base, v_sep.getMaximumSize());

            // Find size of current component
            if(key != null) widen(base, key.getMaximumSize());

            return base;
        }

        public Dimension getPreferredSize() {
            Dimension base = new Dimension(0, 0);
            
            // Find size of subtrees
            if(left != null) widen(base, left.getPreferredSize());
            if(right != null) widen(base, right.getPreferredSize());

            // Find size of separators
            if(h_sep != null) widen(base, h_sep.getPreferredSize());
            if(v_sep != null) widen(base, v_sep.getPreferredSize());

            // Find size of current component
            if(key != null) widen(base, key.getPreferredSize());

            return base;
        }


        /**
         * Layout Container.
         *
         * In order to adjust all components in one pass, weights
         * are passed down a tree, and the collective weights
         * from all subtrees are passed back up.
         *
         * For example, if given a node with weight 1 and
         * a subtree of 2 nodes also with weight 1, 1 is passed down
         * 3 times for a total of 3 at the bottom node. The percentage
         * of space taken at the bottom node is therefore 1/3, which
         * is given to the parent node for handling (1/2) and then back
         * to the original for (1/1) or 100% of the remaining space.
        */
        public void adjustSize(Rectangle free, double v_total, double h_total, int orientation) {

            v_total += v_weight;
            h_total += h_weight;
            Rectangle tmp = new Rectangle(free);

            if(orientation == VERTICAL) {

                // Must propogate vertically first
                if(left != null) left.adjustSize(free, v_total, 0.d, VERTICAL);

                int height = free.height;
                free.height -= free.height * (v_weight / v_total);

                tmp.y = free.height;
                tmp.height = height - free.height;

                // Propogate horizontally afterward
                if(right != null) right.adjustSize(tmp, 0.d, h_total, HORIZONTAL);

            } else {

                // Must propogate horizontally first
                if(right != null) right.adjustSize(free, 0.d, h_total, HORIZONTAL);

                int width = free.width;
                free.width -= free.width * (h_weight / h_total);

                tmp.x = free.width;
                tmp.width = width - free.width;

                // Propogate vertically afterward
                if(left != null) left.adjustSize(tmp, v_total, 0.d, VERTICAL);
            }

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
            if(e.getSource() == h_sep) {

                // Find percentage change
                double delta = h_sep.getX() - e.getX();
                double p = Math.abs(delta) / key.getWidth();

                if(delta < 0) {
                    h_weight -= p;
                    right.h_weight += p;
                } else {
                    h_weight += p;
                    right.h_weight -= p;
                }

                // Translate to the left or right
                h_sep.setLocation(e.getX(), h_sep.getY());

            } else if(e.getSource() == v_sep) {

                // Find percentage change
                int delta = h_sep.getY() - e.getY();
                double p = Math.abs(delta) / key.getHeight();

                if(delta < 0) {
                    v_weight -= p;
                    left.v_weight += p;
                } else {
                    v_weight += p;
                    left.v_weight -= p;
                }

                // Translate to the left or right
                v_sep.setLocation(v_sep.getX(), e.getY());
            }
        }
    }


    /**
     * Root Node.
     *
     * The root will be replaced with a Node during the
     * first request to add a component.
    */
    private Node root = null;


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
            Split s = (Split) constraints;
            Component key = s.getComponent();

            // Initialize root if necessary
            if(root == null) root = new Node(ins);
            else root.insert(key, ins, s.getOrientation());

        } else throw new IllegalArgumentException("Expecting Split");
    }

    @Override
    public void removeLayoutComponent(Component c) {
        if(root != null) root.remove(c);
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
     * component positions, respectively.
    */
    @Override
    public void invalidateLayout(Container parent) {
        // Remove cached data (i.e. weights)
        LinkedList<Node> search = new LinkedList<Node>();
        if(root != null) search.add(root);

        while(!search.isEmpty()) {
            Node s = search.removeFirst();

            s.v_weight = s.h_weight = 1.d; 
            if(s.left != null) search.add(s.left);
            if(s.right != null) search.add(s.right);
        }
    } 

    @Override
    public void layoutContainer(Container parent) {
        if(root != null) {
            Insets inset = parent.getInsets();        

            Dimension space = new Dimension(0, 0);
            space.width = parent.getWidth() - inset.left - inset.right;
            space.height = parent.getHeight() - inset.top - inset.bottom;

            root.adjustSize(new Rectangle(space), 0.d, 0.d, VERTICAL);
        }
    }
}
