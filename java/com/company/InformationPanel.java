package com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

class InformationPanel extends JPanel
{

    private DBCredentials dbCr;
    private JLabel loginLabel;
    private JLabel adminLabel;
    private JLabel emailLabel;
    private JLabel booksNumberLabel;
    private JLabel bookCopiesNumberLabel;
    private JLabel usersNumberLabel;
    private JLabel providersNumberLabel;

    InformationPanel(DBCredentials dbCredentials)
    {
        super(false);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        dbCr=dbCredentials;

        loginLabel = new JLabel("Login: "+dbCredentials.login);
        adminLabel = new JLabel("Account: Admin");
        emailLabel = new JLabel();
        booksNumberLabel = new JLabel();
        bookCopiesNumberLabel = new JLabel();
        usersNumberLabel = new JLabel();
        providersNumberLabel = new JLabel();
        loginLabel.setSize(300, 40);
        adminLabel.setSize(300, 40);
        emailLabel.setSize(300, 40);
        booksNumberLabel.setSize(300, 40);
        bookCopiesNumberLabel.setSize(300, 40);
        usersNumberLabel.setSize(300, 40);
        providersNumberLabel.setSize(300, 40);
        loginLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        adminLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        emailLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        booksNumberLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        bookCopiesNumberLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        usersNumberLabel.setFont(new Font("Serif", Font.PLAIN, 28));
        providersNumberLabel.setFont(new Font("Serif", Font.PLAIN, 28));

        add(loginLabel);
        add(adminLabel);
        add(emailLabel);
        add(booksNumberLabel);
        add(bookCopiesNumberLabel);
        add(usersNumberLabel);
        add(providersNumberLabel);

        loadInfo();
    }

    private void loadInfo()
    {
        CallableStatement stmt=null;
        try{
            Class.forName(DBCredentials.JDBC_DRIVER);

            System.out.println("Connecting to database... ("+ dbCr.login + ")  ");
            dbCr.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            stmt = dbCr.conn.prepareCall("{call getInformation(?,?,?,?,?,?,?,?)}");
            stmt.setString(1, dbCr.login);
            stmt.setString(2, dbCr.password);
            stmt.registerOutParameter(3, Types.TINYINT);
            stmt.registerOutParameter(4, Types.INTEGER);
            stmt.registerOutParameter(5, Types.BIGINT);
            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.registerOutParameter(7, Types.INTEGER);
            stmt.registerOutParameter(8, Types.VARCHAR);

            stmt.executeUpdate();

            int res = stmt.getInt(3);
            switch(res)
            {
            case 0:
                emailLabel.setText("Email: "+stmt.getString(8));
                booksNumberLabel.setText("Number of Titles: "+stmt.getString(4));
                bookCopiesNumberLabel.setText("Number of all book copies: "+stmt.getString(5));
                usersNumberLabel.setText("Number of all customers: "+stmt.getString(6));
                providersNumberLabel.setText("Number of all providers: "+stmt.getString(7));
                break;
            case 1:
                JOptionPane.showMessageDialog(this, "Bad password characters", "Error", JOptionPane.ERROR_MESSAGE);
                emailLabel.setText(stmt.getString(8));
                booksNumberLabel.setText(stmt.getString(4));
                bookCopiesNumberLabel.setText(stmt.getString(5));
                usersNumberLabel.setText(stmt.getString(6));
                providersNumberLabel.setText(stmt.getString(7));
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown error", "Error", JOptionPane.ERROR_MESSAGE);
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
