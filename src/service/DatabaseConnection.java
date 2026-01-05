package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/volunteer_db";
    private static final String USER = "root";
    private static final String PASS = "123456";

    // โหลด Driver แค่ครั้งเดียว
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("ไม่เจอ Driver MySQL กรุณาเช็ค Library!");
        }
    }


    // เมธอดสำหรับขอ Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
