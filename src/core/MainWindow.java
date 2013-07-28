package segfault.core;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainWindow extends JFrame {

    public MainWindow(String title){
        super(title);

        // Menu bar
        JMenuBar menu_bar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Save"));
        menu_bar.add(file);
    
        setJMenuBar(menu_bar);
    }
}
