package com.company;

import javax.swing.*;
import java.awt.event.KeyEvent;

class MainFrame extends JFrame
{
    private DBCredentials dbCredentials;

    MainFrame(DBCredentials dbCredentials)
    {
        super("Bookshops Administration");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        setSize(850, 650);
        setLocation(50,50);

        this.dbCredentials=dbCredentials;

        JTabbedPane tabbedPane = new JTabbedPane();
        getContentPane().add(tabbedPane);

        JPanel panel1 = new SellingPanel(dbCredentials, this);
        tabbedPane.addTab("Customer Orders", panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JPanel panel2 = new ProvidingPanel(dbCredentials, this);
        tabbedPane.addTab("Provider Orders", panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_3);

        JPanel panel3 = new ToolsPanel(dbCredentials);
        tabbedPane.addTab("Database Tools", panel3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_2);

        JPanel panel4 = new InformationPanel(dbCredentials);
        tabbedPane.addTab("Info", panel4);
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_3);

    }


}
