package com.company;

import java.sql.Connection;

class DBCredentials
{
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/Bookshop?autoReconnect=true&useSSL=false&serverTimezone=UTC&noAccessToProcedureBodies=true";
    static final String DB_LOGIN= "Admin";
    static final String DB_PASSW= "789";

    String login;
    String password;

    Connection conn = null;
}
