package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class JobService {
    // ฟังก์ชันสำหรับกรองงานที่ user ปัจจุบันสมัครไปแล้ว
    static public void filterAppliedJobs(ArrayList<API> allJobs) {
        // ถ้ายังไม่ได้ Login ให้ข้ามไป
        if (Auth.getAuthUser() == null)
            return;

        List<String> appliedJobIds = new ArrayList<>();
        String currentStdId = null;

        // เช็คว่าเป็น Student หรือไม่ เพื่อดึง ID ให้ถูกต้อง
        if (Auth.getAuthUser() instanceof Student) {
            Student s = (Student) Auth.getAuthUser();
            currentStdId = s.getStdId();
        } else {
            currentStdId = Auth.getAuthUser().getStd_id();
        }

        if (currentStdId == null) {
            return;
        }

        String sql = "SELECT job_id FROM job_assignment WHERE std_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentStdId);
            ResultSet rs = pstmt.executeQuery();

            // เก็บ ID ของงานที่สมัครแล้วไว้ใน List
            while (rs.next()) {
                appliedJobIds.add(String.valueOf(rs.getInt("job_id")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ลบงานที่มี ID ตรงกับใน appliedJobIds ออกจาก allJobs
        allJobs.removeIf(job -> appliedJobIds.contains(job.jobId));
    }
}
