package com.company;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

class LoginDialog extends JDialog
{
    private DBCredentials dbCredentials;

    private JLabel nameLabel = new JLabel("Login : ");
    private JLabel passwordLabel = new JLabel("Password : ");

    private JTextField nameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");

    LoginDialog()
    {
        setupUI();
        setUpListeners();
        dbCredentials= new DBCredentials();

        setSize(400, 150);
        setLocation(400, 350);
        setVisible(true);
    }

    private void setupUI() {

        this.setTitle("Login");

        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        topPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        topPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        topPanel.add(passwordLabel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        topPanel.add(passwordField, gbc);

        this.add(topPanel);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setUpListeners()
    {
        passwordField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login(nameField.getText(), String.valueOf(passwordField.getPassword()));
                }
            }
        });
        okButton.addActionListener(e -> login(nameField.getText(), String.valueOf(passwordField.getPassword())));
        cancelButton.addActionListener(e -> dispose());
    }


    private void login(String login, String passw)
    {
        if(logToDatabase(login, passw))
        {
            LoginDialog.this.setVisible(false);
            dispose();
            dbCredentials.login=login;
            dbCredentials.password=passw;
            new MainFrame(dbCredentials);
        }
    }


    private boolean logToDatabase(String login, String password)
    {
        int res=4;
        CallableStatement stmt=null;
        try{
            Class.forName(DBCredentials.JDBC_DRIVER);

            System.out.println("Connecting to database... ("+ login + ") ");
            dbCredentials.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            stmt = dbCredentials.conn.prepareCall("{call logAsAdmin(?,?,?)}");
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.TINYINT);

            stmt.executeUpdate();

            res = stmt.getInt(3);
            switch(res)
            {
                case 1:
                    JOptionPane.showMessageDialog(this, "Bad login characters", "Failure", JOptionPane.WARNING_MESSAGE);
                break;
                case 2:
                    JOptionPane.showMessageDialog(this, "Bad password characters", "Failure", JOptionPane.WARNING_MESSAGE);
                break;
                case 3:
                    JOptionPane.showMessageDialog(this, "Bad login or password", "Failure", JOptionPane.WARNING_MESSAGE);
                break;
            }

            stmt.close();
            dbCredentials.conn.close();
        }
        catch(SQLException | ClassNotFoundException se)
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
                if(dbCredentials.conn!=null)
                {
                    dbCredentials.conn.close();
                }
            }
            catch(SQLException se)
            {
                JOptionPane.showMessageDialog(this, se.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
        return res==0;
    }

}
