package com.nichi.nikkieindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://192.168.1.92:5432/nikkie";
    private static final String USERNAME = "keerthanhv";
    private static final String PASSWORD = "nichi-in";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

}
