package segfault;

import java.awt.*;
import javax.swing.*;
import segfault.layout.*;
import segfault.core.MainWindow;

public class Main
{
    public static void main(String[] args)
    {
        MainWindow frame = new MainWindow("Demo");
        Container pane = frame.getContentPane();
        pane.setLayout(new SplitLayout(pane));

        // First TextArea
        JTextArea textarea = new JTextArea("left");
        pane.add(textarea, new Split(SplitLayout.HORIZONTAL));

        // Second
        JTextArea txtarea2 = new JTextArea("right");
        pane.add(txtarea2, new Split(textarea, SplitLayout.HORIZONTAL));

        // Third
        JButton btn = new JButton("test");
        pane.add(btn, new Split(textarea, SplitLayout.VERTICAL));

        pane.remove(textarea);

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
