package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/volunteer_db";
    private static final String USER = "root";
    private static final String PASS = "123456"; // XAMPP ปกติว่างไว้ ถ้ามีรหัสให้ใส่

    // โหลด Driver แค่ครั้งเดียว
    static {
        try {
            // ไม่ต้องใช้ Class.forName() ใน Java เวอร์ชันใหม่ๆ แต่ใส่ไว้กันเหนียวได้
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("ไม่เจอ Driver MySQL กรุณาเช็ค Library!");
        }
    }

    // เมธอดสำหรับขอ Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
