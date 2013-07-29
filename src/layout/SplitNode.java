package segfault.layout;

import java.util.ArrayDeque;

import java.awt.Insets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JSeparator;


/**
 * The split node works by abstracting the idea of recursive
 * JSplitPanes to a tree (a naturally recursive model).
 *
 * A left subtree denotes a vertical split and a right subtree
 * denotes a horizontal one. Note that during laying, the tree
 * is traversed depth-first, in a preorder manner moving from
 * from the right subtree (horizontal elements) to the left.
 *
 * @author: Joshua Potter
 * @version: 0.1
*/
public class SplitNode {

    private Component comp;

    // Vertical subcomponents
    private SplitNode left;
    private JSeparator v_divider;

    // Horizontal subcomponents
    private SplitNode right;
    private JSeparator h_divider;

    // Constructors
    public SplitNode() {
        this(null);
    }

    public SplitNode(Component c) {
        comp = c;
        left = right = null;
        v_divider = h_divider = null;
    }

    public Component getComponent() {
        return comp;
    }

    public SplitNode getLeft() {
        return left;
    }

    public SplitNode getRight() {
        return right;
    }

    /** 
     * Insertion methods.
     *
     * An insertion works in the same manner for both
     * the left and right subtree, though mirrored. A 
     * right subtree is built if a horizontal split
     * is requested on a component. The left is built
     * if a vertical split is requested.
    */
    private void addVerticalSplit(Component c) {
        SplitNode tmp = left;
        left = new SplitNode(c);
        left.left = tmp;

        // First split encountered on current node 
        if(tmp == null) {
            v_divider = new JSeparator(SplitLayout.VERTICAL);
            v_divider.addMouseMotionListener(new MouseMotionAdapter() {

                @Override
                public void mouseDragged(MouseEvent e) {

                }

            });
        }
    }

    private void addHorizontalSplit(Component c) {
        SplitNode tmp = right;
        right = new SplitNode(c);
        right.right = tmp;

        // First split encountered on current node 
        if(tmp == null) {
            h_divider = new JSeparator(SplitLayout.HORIZONTAL);
            h_divider.addMouseMotionListener(new MouseMotionAdapter() {
                
                @Override
                public void mouseDragged(MouseEvent e) {

                } 

            });
        }
    }

    public void addNode(Component c, int orientation) {
        if(orientation == SplitLayout.VERTICAL) {
            addVerticalSplit(c);
        } else if(orientation == SplitLayout.HORIZONTAL) {
            addHorizontalSplit(c);
        } else {
            throw new IllegalArgumentException("Invalid orientation specified.");
        }
    }

    /** 
     * Removal Methods.
     * 
     * Recursively checks left and right subtrees
     * for a match, deleting in a manner similar to
     * a linked list once found. Returns true if 
     * the node has been deleted.
     *
     * TODO: Traverses entire tree currently
    */
    public void removeNode(Component c) {
        removeNode(this, c);
    }

    private SplitNode removeNode(SplitNode s, Component c) {
        if(s == null) return null;

        if(s.comp == c) {
            if(s.right != null) {
                // Place left element on end of right's left subtree
                SplitNode tmp = s.right;
                while(tmp.left != null) {
                    tmp = tmp.left;
                }                
                
                tmp.left = s.left;
                return s.right;
            } else {
                return s.left;
            }
        }

        s.left = removeNode(s.left, c);
        s.right = removeNode(s.right, c);
        return s;
    }

    /** 
     * Splitting.
     *
     * The following method finds the node containing the
     * @component. Note the solution is iterative to allow
     * shortcircuiting once the correct component is found.
     *
     * TODO: Should use a HashMap<Component, SplitNode> and
     * add parent pointer in node. Allow quick finding of 
     * desired nodes.
    */
    public void split(Component split, Component add, int orientation) {

        ArrayDeque<SplitNode> search = new ArrayDeque<SplitNode>();
        search.add(this);

        while(!search.isEmpty()) {
            SplitNode s = search.removeFirst();
            if(s.comp == split) {
                s.addNode(add, orientation);
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
     * Used by the SplitLayout, the following three functions return
     * the nodes sizes (calling the subcomponents corresponding functions).
    */
    public Dimension minimumSize() {
        Dimension base = new Dimension(0, 0);
        
        // Find size of left tree
        if(left != null) {
            Dimension left_min = left.minimumSize();
            base.width += left_min.width;
            base.height = Math.max(left_min.height, base.height);
        }        

        // Find size of right tree
        if(right != null) {
            Dimension right_min = right.minimumSize();
            base.width += right_min.width;
            base.height = Math.max(right_min.height, base.height);
        }

        // Find size of current component
        Dimension c_min = comp.getMinimumSize();
        base.width += c_min.width;
        base.height += c_min.height;

        return base;
    }

    public Dimension maximumSize() {
        Dimension base = new Dimension(0, 0);
        
        // Find size of left tree
        if(left != null) {
            Dimension left_max = left.maximumSize();
            base.width += left_max.width;
            base.height = Math.max(left_max.height, base.height);
        }        

        // Find size of right tree
        if(right != null) {
            Dimension right_max = right.maximumSize();
            base.width += right_max.width;
            base.height = Math.max(right_max.height, base.height);
        }

        // Find size of current component
        Dimension c_max = comp.getMaximumSize();
        base.width += c_max.width;
        base.height += c_max.height;

        return base;
    }

    public Dimension preferredSize() {
        Dimension base = new Dimension(0, 0);
        
        // Find size of left tree
        if(left != null) {
            Dimension left_pref = left.preferredSize();
            base.width += left_pref.width;
            base.height = Math.max(left_pref.height, base.height);
        }        

        // Find size of right tree
        if(right != null) {
            Dimension right_pref = right.preferredSize();
            base.width += right_pref.width;
            base.height = Math.max(right_pref.height, base.height);
        }

        // Find size of current component
        Dimension c_pref = comp.getPreferredSize();
        base.width += c_pref.width;
        base.height += c_pref.height;

        return base;
    }
}

