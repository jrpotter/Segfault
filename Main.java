package segfault;

import java.awt.*;
import javax.swing.*;
import segfault.core.MainWindow;
import segfault.core.MultiPane;

public class Main
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Demo");
        Container pane = frame.getContentPane();

        JTextArea root = new JTextArea("Left");
        MultiPane multi = new MultiPane(root);
        multi.split(root, new JTextArea("Right"), 1);
        multi.split(root, new JTextArea("Bottom"), 0);
        pane.add(multi);
       

        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
