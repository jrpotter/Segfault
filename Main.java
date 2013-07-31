package segfault;

import java.awt.*;
import javax.swing.*;
import segfault.core.MainWindow;
import segfault.core.SplitLayout;

public class Main
{
    public static void main(String[] args)
    {
        MainWindow frame = new MainWindow("Demo");

        /*
        Container pane = frame.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        pane.add(new JTextArea());
        */ 

        Container pane = frame.getContentPane();
        pane.setLayout(new SplitLayout());

        // Add Textedit (first must be null)
        SplitLayout.Split s = new SplitLayout.Split(null, SplitLayout.VERTICAL);
        JTextArea test = new JTextArea();
        pane.add(test, s);

        // Demo - add right button with a bottom button
        //s = new SplitLayout.Split(test, SplitLayout.HORIZONTAL);
        //JButton test2 = new JButton("Right");
        //pane.add(test2, s);

        s = new SplitLayout.Split(test, SplitLayout.VERTICAL);
        JButton test3 = new JButton("Left bottom");
        pane.add(test3, s);
        

        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
