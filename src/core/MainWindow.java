package segfault.core;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import segfault.text.TextEdit;

public class MainWindow extends JFrame {

    public MainWindow(String title){
        super(title);

        // Menu bar
        JMenuBar menu_bar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.add(new JMenuItem("Save"));
        menu_bar.add(file);
    
        // Text Editor
        add(new TextEdit());

        // Init
        pack();
        setVisible(true);
        setJMenuBar(menu_bar);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
