package service;

import model.JobEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AbsentAssignment extends JobAssignmentService {
    private double hourRate = 0.0;

    public AbsentAssignment(JobEntity job, User student, String assignDate, double hourRate) {
        super(job, student, assignDate);
        this.hourRate = 0.0;
    }

    public AbsentAssignment() {
        super(null, null, null);
    }

    /**
     * อัปเดตสถานะ job_assignment ของ Worker เป็น 'absent' (ไม่มาทำงาน)
     * 
     * @param jobId รหัสงาน
     * @param stdId รหัสนักศึกษา
     * @return true ถ้าสำเร็จ
     */
    public boolean markAsAbsent(int jobId, String stdId) {
        String sql = "UPDATE job_assignment SET status = 'absent', finished_at = ? WHERE job_id = ? AND std_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, jobId);
            pstmt.setString(3, stdId);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                // อัปเดต Profile (แต่ไม่รวมชั่วโมงเพราะขาดงาน)
                System.out.println("Marked as absent: " + stdId + " for job " + jobId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error marking as absent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void handleStatus() {
        String stdId = null;

        if (student instanceof Student) {
            stdId = ((Student) student).getStdId();
        }
        // ถ้าไม่มี ให้เช็คจากคนที่ Login อยู่
        else if (Auth.getAuthUser() != null) {
            stdId = Auth.getAuthUser().getStd_id();
        }

        // 2. อัปเดตสถิติ
        if (stdId != null) {
            updateUserStats(stdId);
            System.out.println("Recorded absence for Student ID: " + stdId);
        } else {
            System.err.println("Error: Student ID not found for AbsentAssignment.");
        }
    }

    @Override
    public String[] generateExportData() {
        // คำนวณค่าตอบแทน (ซึ่งจะได้ 0 เพราะ hourRate = 0)
        double totalReward = 0.0;

        // ชั่วโมงทำงานจริงควรเป็น "0" เพราะขาดงาน (แม้ Job จะมีเวลากำหนดไว้ก็ตาม)
        String workingHours = "0";

        return new String[] {
                assignDate,
                job.getTitle() + " (Absent)",
                workingHours,
                String.format("%.2f", totalReward)
        };
    }
}