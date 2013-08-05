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
     * left subtree to the right or right to left
     * depending on the node's priority member.
    */
    private class Node extends MouseMotionAdapter {
        
        private Component key;
        private Node parent = null;
        private int priority = VERTICAL;

        // Vertical Split
        private Node left = null;
        private double v_weight = 1.d;
        private JSeparator v_sep = null;

        // Horizontal Split
        private Node right = null;
        private double h_weight = 1.d;
        private JSeparator h_sep = null;

        // Constructors
        public Node(Component key, int priority) {
            this(key, null, priority);
        }

        public Node(Component key, Node parent) {
            this(key, parent, VERTICAL);
        }

        public Node(Component key, Node parent, int priority) {
            this.key = key;
            this.parent = parent;
            this.priority = priority;
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
        private void split(Component ins, Split s) {
            int priority = s.getPriority();
            int orientation = s.getOrientation();

            if(orientation == VERTICAL) {
                Node tmp = left;
                left = new Node(ins, this, priority);
                left.left = tmp;

                // Separator was not yet created
                if(tmp == null) {
                    v_sep = new JSeparator(orientation);
                    v_sep.addMouseMotionListener(this);
                }

            } else {
                Node tmp = right;
                right = new Node(ins, this, priority);
                right.right = tmp;

                if(tmp == null) {
                    h_sep = new JSeparator(orientation);
                    h_sep.addMouseMotionListener(this);
                }                
            }
        }

        // First find key and split if found
        public void insert(Component ins, Split s) {

            LinkedList<Node> search = new LinkedList<Node>();
            search.add(this);

            while(!search.isEmpty()) {
                Node tmp = search.removeFirst();

                if(tmp.key == s.getComponent()) {
                    tmp.split(ins, s);
                    break;
                } else {
                    if(tmp.left != null) search.add(tmp.left);
                    if(tmp.right != null) search.add(tmp.right);
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
         * A component and its left or right subtree has a collective
         * weight of 2- a component takes up a percentage of free space
         * proportional to its weight over 2.
        */
        private void adjustSize(Rectangle free, int orientation) {
    
            Rectangle tmp = new Rectangle(free);

            if(orientation == VERTICAL) {
            
                // Adjust rectangle vertically
                free.height -= (int)(free.height * (v_weight / 2.d));
                tmp.y = free.y + tmp.height - free.height;
                tmp.height = free.height;
                left.adjustSize(tmp);

                // Adjust horizontal separator (v_sep)

            } else {

                // Adjust rectangle horizontally
                free.width -= (int)(free.width * (h_weight / 2.d));
                tmp.x = free.x + tmp.width - free.width;
                tmp.width = free.width;
                right.adjustSize(tmp);

                // Adjust vertical separator (h_sep)
            }
        }
 
        public void adjustSize(Rectangle free) {

            if(priority == VERTICAL) {
                if(left != null) adjustSize(free, VERTICAL);
                if(right != null) adjustSize(free, HORIZONTAL);
            } else {
                if(right != null) adjustSize(free, HORIZONTAL);
                if(left != null) adjustSize(free, VERTICAL);
            }

            // Set component in place
            if(key != null) key.setBounds(free);
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
    private Container container;

    public SplitLayout(Container container) {
        this.container = container;
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

        if(constraints instanceof Split) {
            Split s = (Split) constraints;

            // Initialize root if necessary
            if(root == null) root = new Node(ins, s.getPriority());
            else root.insert(ins, s);

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
        // Remove cached data (i.e. reset weights)
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

            root.adjustSize(new Rectangle(space));
        }
    }
}
