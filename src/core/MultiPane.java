package segfault.core;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;

import java.util.LinkedList;

/**
 * MultiPane Class.
 *
 * The following serves as an abstraction of a
 * JSplitPane, allowing multiple nested JPanels
 * via reference to the objects within the MultiPane
 * and not to how the tree of nested split panes
 * are organized.
 *
 * @author: Joshua Potter
 * @version: 0.1
*/
public class MultiPane extends JPanel {

    private Component key;
    private MultiPane parent;


    // Constructors
    public MultiPane(Component key) {
        this(key, null);
    }

    private MultiPane(Component key, MultiPane parent) {
        this.key = key;
        this.parent = parent;

        add(key);

        // Allows expansion of key/split pane
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }


    // Convenience Methods
    private MultiPane left() {
        JSplitPane tmp = (JSplitPane) key;
        return (MultiPane) tmp.getLeftComponent();
    }

    private MultiPane right() {
        JSplitPane tmp = (JSplitPane) key;
        return (MultiPane) tmp.getRightComponent();
    }


    // Replace component with split pane
    public void split(Component c, Component ins, int orientation) {

        LinkedList<MultiPane> search = new LinkedList<MultiPane>();
        search.add(this);

        while(!search.isEmpty()) {
            MultiPane m = search.removeFirst();

            if(m.key == c) {
                m.remove(m.key);

                // Replace key with split pane holding insert/original
                JSplitPane tmp = new JSplitPane(orientation);
                tmp.setDividerSize(2);
                tmp.setResizeWeight(0.5d);

                tmp.setLeftComponent(new MultiPane(c, m));
                tmp.setRightComponent(new MultiPane(ins, m));
                
                m.key = tmp;
                m.add(m.key);
                break;
            } 

            else if(m.key instanceof JSplitPane){
                search.add(m.left());
                search.add(m.right());
            }
        }
    }


    // Replace split pane with other component
    public void detach(Component c) {

        LinkedList<MultiPane> search = new LinkedList<MultiPane>();
        search.add(this);

        while(!search.isEmpty()) {
            MultiPane m = search.removeFirst();

            if(m.key == c) {

                if(m.parent == null) 
                    throw new IllegalArgumentException("The MultiPane must have a component");
               

                // Replace parent key with the non-detachee
                MultiPane tmp = m.parent.left();
                if(m == tmp) tmp = m.parent.right();

                m.parent.remove(m.parent.key);
                m.parent.key = tmp.key;
                m.parent.add(tmp);

                break;
            } 

            else if(m.key instanceof JSplitPane){
                search.add(m.left());
                search.add(m.right());
            }
        }
    }
}
