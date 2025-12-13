package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class RegisterManager {

    public static boolean register(User user) {
        System.out.println("Processing registration for: " + user.getName());

        if (user == null || user.getEmail().isEmpty() || user.getPassword().isEmpty()) {
            return false;
        }

        if (Auth.isEmailExists(user.getEmail())) {
            System.err.println("Error: Email taken -> " + user.getEmail());
            return false;
        }

        // --- แก้ SQL เพิ่ม std_id ---
        String sql = "INSERT INTO user (name, email, password, status, std_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getStatus());

            // --- เช็คว่าเป็น Student หรือไม่ เพื่อดึง stdId ---
            if (user instanceof Student) {
                // แปลงร่าง User เป็น Student เพื่อดึง getStdId()
                Student s = (Student) user;
                pstmt.setString(5, s.getStdId());
            } else {
                // ถ้าไม่ใช่ Student (เช่น Admin) ให้ใส่ NULL
                pstmt.setNull(5, Types.VARCHAR);
            }

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}