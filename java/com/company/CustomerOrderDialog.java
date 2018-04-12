package com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

class CustomerOrderDialog extends JDialog
{
    private CustomerOrder order;
    private SellingPanel panel;
    private JButton cancelButton;
    private JButton confirmButton;
    private JLabel label;
    private JScrollPane scrollPane;
    private static final String[] dataNames= {"Order ID", "Customer", "Email", "Date", "Books amount"};
    private static final String[] columnNames= {"ISBN", "Title", "Release Date", "Price", "Authors"};

    CustomerOrderDialog(JFrame frame, CustomerOrder order, SellingPanel panel)
    {
        super(frame, order.login + " customer transaction", true);
        this.order=order;
        this.panel=panel;

        setLayout(new FlowLayout(FlowLayout.CENTER));
        setResizable(false);
        setSize(550, 450);

        cancelButton= new JButton("Cancel");
        confirmButton= new JButton("Confirm purchase");
        cancelButton.addActionListener(e -> cancel());
        confirmButton.addActionListener(e -> confirm());

        label= new JLabel();
        label.setFont(new Font("Verdana", Font.PLAIN, 22));

        String inf=dataNames[0]+": "+order.id+" \n "+ dataNames[1]+": "+order.login+" \n "+ dataNames[2]+
                ": "+order.email+" \n "+ dataNames[3]+": "+order.date+" \n "+ dataNames[4]+": "+order.books;

        label.setText("<html>" + inf.replaceAll("<","&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\n", "<br/>") + "</html>");



        add(label);
        add(cancelButton);
        add(confirmButton);
        scrollPane= new JScrollPane();
        add(scrollPane);
        readOrder();
        setVisible(true);
    }

    private void cancel()
    {
        dispose();
    }

    private void confirm()
    {
        int res=JOptionPane.showConfirmDialog(panel, "Confirm Order "+order.id+"?",
                "Confirmation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(res==0)
        {
            saveConfirmation();
            panel.readOrders();
            dispose();
        }
    }

    private void saveConfirmation()
    {
        cancelButton.setEnabled(false);
        confirmButton.setEnabled(false);
        DBCredentials dbCr = panel.dbCr;
        CallableStatement stmt=null;
        try{
            Class.forName(DBCredentials.JDBC_DRIVER);

            System.out.println("Connecting to database... ("+ dbCr.login + ")  ");
            dbCr.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            stmt = dbCr.conn.prepareCall("{call confirmCustomerOrder(?,?,?,?)}");
            stmt.setString(1, dbCr.login);
            stmt.setString(2, dbCr.password);
            stmt.registerOutParameter(3, Types.TINYINT);
            stmt.setLong(4, order.id);

            stmt.executeUpdate();

            int res = stmt.getInt(3);
            switch(res)
            {
                case 0:
                    JOptionPane.showMessageDialog(panel, "Order "+order.id+" confirmed",
                            "Confirmation", JOptionPane.PLAIN_MESSAGE);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(this, "Bad password characters", "Error", JOptionPane.ERROR_MESSAGE);

                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown error, operation doesn't succeed", "Error", JOptionPane.ERROR_MESSAGE);
            }

            stmt.close();
            dbCr.conn.close();
        }
        catch(SQLException| ClassNotFoundException se)
        {
            JOptionPane.showMessageDialog(this, se.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            cancelButton.setEnabled(true);
            confirmButton.setEnabled(true);
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

    private void readOrder()
    {
        cancelButton.setEnabled(false);
        confirmButton.setEnabled(false);
        DBCredentials dbCr = panel.dbCr;
        PreparedStatement stmt=null;
        try
        {
            Class.forName(DBCredentials.JDBC_DRIVER);
            System.out.println("Connecting to database... ("+ dbCr.login + ")  ");
            dbCr.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            stmt = dbCr.conn.prepareCall(
                    "SELECT B.ISBN AS isbn, B.title AS t, B.standardPrice AS p, B.releaseDate AS r, " +
                    "GROUP_CONCAT(CONCAT(A.firstname, ' ', A.surname, '; ')) AS s " +
                    "FROM BooksAndOrders AS BAO " +
                    "INNER JOIN BookCopies AS BC ON BAO.book=BC.ID " +
                    "INNER JOIN Books AS B ON B.ISBN=BC.ISBN " +
                    "INNER JOIN BooksAndAuthors AS BAA ON BAA.ISBN=B.ISBN " +
                    "INNER JOIN Authors AS A ON A.ID=BAA.authorID " +
                    "WHERE BAO.customerOrder="+ order.id + " " +
                    "GROUP BY isbn, t, p, r ");

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
                String isbn= rs.getString("isbn");
                String title= rs.getString("t");
                String authors= rs.getString("s");
                Date date= rs.getDate("r");
                int price= rs.getInt("p");

                data[i]=new Object[]{isbn, title, date, price, authors};
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
            cancelButton.setEnabled(true);
            confirmButton.setEnabled(true);
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
