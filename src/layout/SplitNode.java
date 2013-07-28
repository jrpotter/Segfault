package segfault.layout;

import java.util.ArrayDeque;

import java.awt.Component;
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
    */
    public void removeNode(Component c) {

    }


    /** 
     * Splitting.
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

        while(!search.isEmpty()){
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
}

