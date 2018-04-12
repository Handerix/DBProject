package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;

class ToolsPanel extends JPanel
{

    private DBCredentials dbCr;
    private JButton backupButton;
    private JButton restoreButton;
    private JLabel backupLabel;
    private boolean backupExists;
    private final static String dateString="G:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\BookshopTime.txt";

    ToolsPanel(DBCredentials dbCredentials)
    {
        super(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 100, 200));

        dbCr=dbCredentials;
        backupButton= new JButton("Make new backup");
        restoreButton= new JButton("Back to last backup");
        backupLabel= new JLabel("No backup");
        backupButton.setSize(200, 80);
        restoreButton.setSize(200, 80);
        backupButton.setFont(backupButton.getFont().deriveFont(26f).deriveFont(Font.PLAIN));
        restoreButton.setFont(restoreButton.getFont().deriveFont(26f).deriveFont(Font.PLAIN));
        backupLabel.setFont(new Font("Courier", Font.PLAIN, 28));
        add(backupButton);
        add(restoreButton);
        add(backupLabel);
        backupButton.addActionListener(e -> makeBackup());
        restoreButton.addActionListener(e -> restore());

        backupExists=false;
        Path path= Paths.get(dateString);
        byte[] buff;
        try
        {
            buff=Files.readAllBytes(path);
            if(buff.length>3)
            {
                backupExists=true;
                backupLabel.setText(new String(buff, "ASCII"));
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if(!backupExists)
        {
            restoreButton.setEnabled(false);
        }
    }


    private void makeBackup()
    {
        backupButton.setEnabled(false);
        restoreButton.setEnabled(false);
        PreparedStatement stmt=null;
        try
        {
            Class.forName(DBCredentials.JDBC_DRIVER);
            System.out.println("Connecting to database... ("+ dbCr.login + ")  ");
            dbCr.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            dbCr.conn.setAutoCommit(false);
            stmt = dbCr.conn.prepareCall(
                    "LOCK TABLES authors READ, bookcopies READ, books READ, booksandauthors READ, booksandorders READ, customerorders READ, opinions READ, "
                            + " pricepromotions READ, providerorderlogs READ, providerorders READ, warehouses READ, users READ");


            stmt.execute();
            stmt.close();
            dbCr.conn.commit();

            try
            {
                ProcessBuilder builder = new ProcessBuilder(
                        "G:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump",
                        "-u", DBCredentials.DB_LOGIN, "-p"+DBCredentials.DB_PASSW, "Bookshop", ">", "Bookshop.sql");

                builder.redirectErrorStream(true);
                Process p = builder.start();
                p.waitFor();
                BufferedReader reader=new BufferedReader(
                        new InputStreamReader(p.getInputStream())
                );
                String line;
                do
                {
                    line=reader.readLine();
                }
                while(line != null);

            } catch (IOException| InterruptedException exp) {
                exp.printStackTrace();
            }

            stmt = dbCr.conn.prepareCall("UNLOCK TABLES");
            stmt.execute();
            stmt.close();

            backupExists=true;
            String backDate=Calendar.getInstance().getTime().toString();
            System.out.println(backDate);
            backupLabel.setText(backDate);
            try (Writer writer = new BufferedWriter(new FileWriter(dateString)))
            {
                writer.write(backDate);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            restoreButton.setEnabled(true);
            dbCr.conn.close();
        }
        catch(SQLException| ClassNotFoundException se)
        {
            try
            {
                dbCr.conn.rollback();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, se.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            backupButton.setEnabled(true);
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

    private void restore()
    {
        backupButton.setEnabled(false);
        restoreButton.setEnabled(false);
        PreparedStatement stmt=null;
        try
        {
            Class.forName(DBCredentials.JDBC_DRIVER);
            System.out.println("Connecting to database... ("+ dbCr.login + ")  ");
            dbCr.conn = DriverManager.getConnection(DBCredentials.DB_URL, DBCredentials.DB_LOGIN, DBCredentials.DB_PASSW);

            dbCr.conn.setAutoCommit(false);
            stmt = dbCr.conn.prepareCall(
                    "LOCK TABLES authors READ, bookcopies READ, books READ, booksandauthors READ, booksandorders READ, customerorders READ, opinions READ, "
                            + " pricepromotions READ, providerorderlogs READ, providerorders READ, warehouses READ, users READ");


            stmt.execute();
            stmt.close();
            dbCr.conn.commit();

            try
            {
                ProcessBuilder builder = new ProcessBuilder(
                        "G:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump",
                        "-u", DBCredentials.DB_LOGIN, "-p"+DBCredentials.DB_PASSW, "-D", "Bookshop",
                        "<", "Bookshop.sql");

                builder.redirectErrorStream(true);
                Process p = builder.start();
                p.waitFor();
                BufferedReader reader=new BufferedReader(
                        new InputStreamReader(p.getInputStream())
                );
                String line;
                do
                {
                    line=reader.readLine();
                }
                while(line != null);

            } catch (IOException| InterruptedException exp) {
                exp.printStackTrace();
            }

            stmt = dbCr.conn.prepareCall("UNLOCK TABLES");
            stmt.execute();
            stmt.close();
            dbCr.conn.close();
        }
        catch(SQLException| ClassNotFoundException se)
        {
            try
            {
                dbCr.conn.rollback();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, se.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        finally
        {
            backupButton.setEnabled(true);
            restoreButton.setEnabled(true);
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
