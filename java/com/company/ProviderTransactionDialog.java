package com.company;

import javax.swing.*;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

class ProviderTransactionDialog extends JDialog
{
    private JButton cancelButton;
    private JButton confirmButton;
    private ProviderOrder order;
    private ProvidingPanel panel;

    ProviderTransactionDialog(JFrame frame, ProviderOrder order, ProvidingPanel panel)
    {
        super(frame, order.login + " provider transaction", true);
        this.order=order;
        this.panel=panel;

        setVisible(true);
        setResizable(false);
        setSize(550, 450);

        cancelButton= new JButton("Cancel");
        confirmButton= new JButton("Confirm purchase");
        cancelButton.addActionListener(e -> cancel());
        confirmButton.addActionListener(e -> confirm());

        add(cancelButton);
        add(confirmButton);
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

            stmt = dbCr.conn.prepareCall("{call confirmProviderOrder(?,?,?,?)}");
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


}
