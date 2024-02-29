package com.geta.dao.connection;

import java.sql.*;

public class Connect {

    public Connect() {

    }

    public static Connection getConnectionPostgresql() {
        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dao", "postgres", "popo");
            con.setAutoCommit(false);
        } catch (Exception e) {
            System.out.println(e);

        }
        return con;
    }

    public static Connection getConnection(String connectionString, String username, String password,
            String driverName, Boolean autoCommit) {
        Connection con = null;
        try {
            Class.forName(driverName);

            con = DriverManager.getConnection(connectionString, username, driverName);
            con.setAutoCommit(autoCommit);

        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

}
