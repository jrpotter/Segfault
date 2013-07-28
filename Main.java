package segfault;

import javax.swing.*;
import segfault.core.MainWindow;

public class Main
{
    public static void main(String[] args)
    {
        MainWindow frame = new MainWindow("Demo");
        
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
