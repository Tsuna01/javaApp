package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    // เก็บ User ที่ล็อกอินอยู่ปัจจุบัน (Static เพราะทั้งโปรแกรมมีคนล็อกอินคนเดียว)
    private static User currentUser;

    // --- Login Function ---
    // รับ Email และ Password มาเช็คกับ Database
    public static boolean login(String email, String password) {
        // เพิ่ม role ใน SQL (หรือ select *)
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String status = rs.getString("status");

                    String role = rs.getString("status");

                    //เช็ค Role เพื่อสร้าง Object ให้ถูกประเภท
                    if (role != null && role.equalsIgnoreCase("ADMIN")) {
                        currentUser = new Admin(id, name, email, password, status);
                    } else if (role != null && role.equalsIgnoreCase("STUDENT")) {
                        currentUser = new Student(id, name, email, password, status);
                    } else {
                        // กรณีหา role ไม่เจอ
                        throw new RuntimeException(" Status ไม่ตรงนะ ตรวจสอบใหม่!!");
                    }

                    System.out.println("Login Success as: " + role);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // --- Register Function ---
    public static boolean register(String name, String email, String password) {
        // 1. เช็คก่อนว่าอีเมลซ้ำไหม
        if (isEmailExists(email)) {
            System.out.println("Register Failed: Email already exists");
            return false;
        }

        // 2. ถ้าไม่ซ้ำ ให้บันทึกลง DB
        String sql = "INSERT INTO users (name, email, password, status) VALUES (?, ?, ?, 'Student')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Logout ---
    public static void logout() {
        currentUser = null;
    }

    // --- Helper: Get Current User ---
    public static User getAuthUser() {
        return currentUser;
    }

    // --- Helper: Check Login Status ---
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // --- Internal Helper: Check Duplicate Email ---
    private static boolean isEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // ถ้ามีข้อมูล return true
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // กันไว้ก่อนว่า Error คือห้ามสมัคร
        }
    }
}