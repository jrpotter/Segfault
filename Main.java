package segfault;

import javax.swing.*;
import segfault.core.MainWindow;
import segfault.core.SplitLayout;

public class Main
{
    public static void main(String[] args)
    {
        MainWindow frame = new MainWindow("Demo");
        frame.setLayout(new SplitLayout());
        frame.add(new JTextArea("hi"), new SplitLayout.Split(null, 0));

        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
