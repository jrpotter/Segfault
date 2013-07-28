package segfault;

import javax.swing.*;
import segfault.core.MainWindow;
import segfault.core.MultiPane;

public class Main
{
    public static void main(String[] args)
    {
        MainWindow frame = new MainWindow("Demo");

        MultiPane m = new MultiPane();
        //frame.add(new JButton("test"));
        //frame.add(m.getDivider());
        //frame.add(new JButton("BLAH"));
        
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
