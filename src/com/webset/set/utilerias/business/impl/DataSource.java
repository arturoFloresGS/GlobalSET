package com.webset.set.utilerias.business.impl;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private Connection con;
    private static DataSource instancia;
    private String url, username, password;
    private DataSource() {
            
            try {
                    
                    Properties prop = new Properties();
                    prop.load(new FileInputStream("datasource.properties"));
                    
                    Class.forName(prop.getProperty("driver"));
                    
                    String host = prop.getProperty("host");
                    String port = prop.getProperty("port");
                    String database = prop.getProperty("database");
                    url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
                    username = prop.getProperty("username");
                    password = prop.getProperty("password");
            }
            catch (Exception e) {
                    System.out.println("Parametros de conexion incorrectos.");
            }
    }
    public static synchronized DataSource getInstance() {
            if (instancia == null)
                    instancia = new DataSource();
            return instancia;
    }
    public synchronized Connection getConnection() throws SQLException {            
            
            if (con == null || con.isClosed())
                    con = DriverManager.getConnection(url, username, password);
            return con;
    }
    public synchronized void close() throws SQLException {
            if (con != null && !con.isClosed())
                    con.close();
    }
}