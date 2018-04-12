package com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

class SellingPanel extends JPanel
{

    private JFrame frame;
    DBCredentials dbCr;
    private JButton chooseButton;
    private JButton refreshButton;
    private JScrollPane scrollPane;
    private static final String[] columnNames = {"Order ID", "Customer", "Email", "Date", "Books amount"};

    SellingPanel(DBCredentials dbCredentials, JFrame frame)
    {
        super(false);

        this.dbCr=dbCredentials;
        this.frame=frame;

        Object[][] data = {};
        JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(600, 500));
        table.setFillsViewportHeight(true);

        chooseButton= new JButton("chose");
        chooseButton.setFont(chooseButton.getFont().deriveFont(16f));
        refreshButton= new JButton("refresh");
        refreshButton.setFont(refreshButton.getFont().deriveFont(16f));
        chooseButton.addActionListener(e -> chooseRow());
        refreshButton.addActionListener(e -> readOrders());

        scrollPane=new JScrollPane(table);
        add(scrollPane);
        add(chooseButton);
        add(refreshButton);
        readOrders();
    }


    private void chooseRow()
    {
        JViewport viewport= scrollPane.getViewport();
        JTable table=(JTable) viewport.getView();
        int row= table.getSelectedRow();
        if(row>=0)
        {
            CustomerOrder order= new CustomerOrder();
            order.id=(long) table.getValueAt(row, 0);
            order.login=(String) table.getValueAt(row, 1);
            order.email=(String) table.getValueAt(row, 2);
            order.date=(Date) table.getValueAt(row, 3);
            order.books=(int) table.getValueAt(row, 4);

            SwingUtilities.invokeLater(() -> new CustomerOrderDialog(frame, order, this));
        }
    }



    void readOrders()
    {
        refreshButton.setEnabled(false);
        chooseButton.setEnabled(false);
        PreparedStatement stmt=null;
        try
        {
            Class.forName(DBCredentials.JDBC_DRIVER);
            System.out.println("Connecting to database... ("+ dbCr.login + ")  ");
            dbCr.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            stmt = dbCr.conn.prepareCall("SELECT CO.ID AS i, U.login AS l, U.email AS e, CO.datee AS d, COUNT(BAO.Book) AS c " +
                                                "FROM Users AS U INNER JOIN CustomerOrders AS CO ON U.login=CO.customer " +
                                                "INNER JOIN BooksAndOrders AS BAO ON BAO.customerOrder=CO.ID " +
                                                "GROUP BY U.login, U.email, CO.datee, CO.ID");

            ResultSet rs= stmt.executeQuery();
            int rowsNumber= 0;
            if (rs.last())
            {
                rowsNumber = rs.getRow();
                rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
            }
            Object[][] data = new Object[rowsNumber][5];
            int i=0;
            while(rs.next())
            {
                long id= rs.getLong("i");
                String login= rs.getString("l");
                String email= rs.getString("e");
                Date date= rs.getDate("d");
                int books= rs.getInt("c");

                Object[] objects={id, login, email, date, books};
                data[i]=objects;
            }

            JTable table= new JTable( data, columnNames);
            table.setPreferredScrollableViewportSize(new Dimension(600, 500));
            table.setFillsViewportHeight(true);
            scrollPane.setViewportView(table);
            stmt.close();
            dbCr.conn.close();
        }
        catch(SQLException| ClassNotFoundException se)
        {
            JOptionPane.showMessageDialog(this, se.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            refreshButton.setEnabled(true);
            chooseButton.setEnabled(true);
            try
            {
                if(stmt!=null)
                    stmt.close();
            }
            catch(SQLException ignored)
            {
            }
            try
            {
                if(dbCr.conn!=null)
                {
                    dbCr.conn.close();
                }
            }
            catch(SQLException se)
            {
                JOptionPane.showMessageDialog(this, se.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}
