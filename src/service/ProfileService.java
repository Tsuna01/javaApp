package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileService {

    public static boolean updateProfile(User user, String bio) {
        System.out.println("Processing updateProfile for: " + user.getName());

        // คำสั่ง SQL Update แบบ Join
        String sql = "UPDATE user " +
                "INNER JOIN profile ON user.user_id = profile.user_id " +
                "SET user.name = ?, " +
                "    user.email = ?, " +
                "    profile.bio = ? " +
                "WHERE user.user_id = ?;";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Mapping ค่าลงใน Parameter (?)
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, bio);
            pstmt.setInt(4, user.getId());

            // --- จุดที่สำคัญที่สุดที่ขาดไป ---
            int affectedRows = pstmt.executeUpdate(); // รันคำสั่ง SQL

            // คืนค่า true ถ้ามีการอัปเดตข้อมูลสำเร็จ (แถวที่ถูกผลกระทบ > 0)
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateImage(User user, String image_path){
        String sql = "UPDATE profile SET image_path = ?  WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Mapping ค่าลงใน Parameter (?)
            pstmt.setString(1, image_path);
            pstmt.setInt(2, user.getId());

            // --- จุดที่สำคัญที่สุดที่ขาดไป ---
            int affectedRows = pstmt.executeUpdate(); // รันคำสั่ง SQL

            // คืนค่า true ถ้ามีการอัปเดตข้อมูลสำเร็จ (แถวที่ถูกผลกระทบ > 0)
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}