package org.zoho.server.persist.connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection myconnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/easyshopper", "root", "Kokila@07");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
