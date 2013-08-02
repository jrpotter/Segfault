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
        pane.setLayout(new SplitLayout());

        // First TextArea
        JTextArea textarea = new JTextArea();
        pane.add(textarea, new Split());

        // Second Table
        String[] columnNames = {"First Name",
                                "Last Name",
                                "Sport",
                                "# of Years",
                                "Vegetarian"};

        Object[][] data = {
                            {"Kathy", "Smith",
                             "Snowboarding", new Integer(5), new Boolean(false)},
                            {"John", "Doe",
                             "Rowing", new Integer(3), new Boolean(true)},
                            {"Sue", "Black",
                             "Knitting", new Integer(2), new Boolean(false)},
                            {"Jane", "White",
                             "Speed reading", new Integer(20), new Boolean(true)},
                            {"Joe", "Brown",
                             "Pool", new Integer(10), new Boolean(false)}
                        };

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, new Split(textarea, SplitLayout.HORIZONTAL));

        // Tabbed Panes


        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
