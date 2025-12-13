package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    private static User currentUser;

    // --- Login Function ---
    public static boolean login(String email, String password) {
        // SQL เดิมของคุณ ไม่ต้องแก้ DB
        String sql = "SELECT * FROM user WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("user_id");
                    String name = rs.getString("name");

                    // อ่านค่า status จาก DB (ซึ่งเก็บคำว่า 'Student' หรือ 'Admin' ไว้)
                    String status = rs.getString("status");

                    if (status != null && status.equalsIgnoreCase("ADMIN")) {
                        currentUser = new Admin(id, name, email, password, status);
                    } else {

                        String stdIdFromDB = rs.getString("std_id");

                        if (stdIdFromDB == null) stdIdFromDB = "";

                        currentUser = new Student(id, name, email, password, status, stdIdFromDB);
                    }

                    System.out.println("Login Success as: " + status);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // --- Logout ---
    public static void logout() {
        currentUser = null;
    }

    public static User getAuthUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    static boolean isEmailExists(String email) {
        String sql = "SELECT user_id FROM user WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }


}