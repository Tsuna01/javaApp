package service;

import model.JobEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CompletedAssignment extends JobAssignmentService {

    private double hourRate;

    public CompletedAssignment(JobEntity job, User student, String assignDate, double hourRate) {
        super(job, student, assignDate);
        this.hourRate = hourRate;
    }

    public CompletedAssignment() {
        super(null, null, null);
    }

    // เมธอดนี้ใช้ Connection แยก (Auto-commit) ปกติ เพราะเป็นการกระทำเดี่ยวๆ
    public boolean submitComplete(String jobId, String status) {
        String sql = "UPDATE job SET status = ? WHERE job_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int jobIdNew = Integer.parseInt(jobId);
            pstmt.setString(1, status);
            pstmt.setInt(2, jobIdNew);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void handleStatus() {
        String stdId = null;

        if (Auth.getAuthUser() != null) {
            stdId = Auth.getAuthUser().getStd_id();
        }

        if (student instanceof Student) {
            stdId = ((Student) student).getStdId();
        }

        if (stdId != null) {
            updateUserStats(stdId); // เรียกตัวแม่ (Connection แยก)
        } else {
            System.err.println("Error: Student ID is null, cannot update stats in CompletedAssignment.");
        }
    }

    @Override
    public String[] generateExportData() {
        double totalReward = job.getWorkingHours() * hourRate;
        return new String[] {
                assignDate,
                job.getTitle(),
                String.valueOf(job.getWorkingHours()),
                String.format("%.2f", totalReward)
        };
    }

    /**
     * ✅ UPDATE แบบ Transaction: อัปเดต Profile และ Status ของทุกคนพร้อมกัน
     * ถ้าคนใดคนหนึ่งพัง จะ Rollback ทั้งหมด
     */
    public void updateAllWorkersStatus(int jobId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // 1. เริ่ม Transaction (ห้ามบันทึกจนกว่าจะสั่ง)

            ArrayList<WorkerManager> workers = WorkerManager.getJobWorkers(jobId);

            if (workers != null && !workers.isEmpty()) {
                for (WorkerManager worker : workers) {
                    // 2. ส่ง conn ก้อนเดียวกันเข้าไปในทุกเมธอด
                    updateWorkerAssignmentStatus(conn, jobId, worker.stdId, "complete");

                    // เรียกเมธอด helper ที่สร้างไว้ข้างล่าง (รับ conn)
                    updateUserStats(conn, worker.stdId);

                    System.out.println("Pending update for: " + worker.stdId);
                }
            }

            conn.commit(); // 3. ยืนยันข้อมูลทั้งหมดลง Database ทีเดียว
            System.out.println("Transaction Committed Successfully for Job ID: " + jobId);

        } catch (SQLException e) {
            System.err.println("Transaction Failed! Rolling back...");
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            // 4. ปิด Connection เสมอเมื่อจบงาน
            if (conn != null) {
                try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * ✅ Helper 1: อัปเดตสถานะงาน (รับ Connection มาใช้ต่อ)
     * ห้ามใส่ try-with-resources ที่ปิด Connection เด็ดขาด
     */
    private void updateWorkerAssignmentStatus(Connection conn, int jobId, String stdId, String status) throws SQLException {
        // เพิ่มเงื่อนไข AND status != 'absent' เข้าไปใน WHERE clause
        String sql = "UPDATE job_assignment " +
                "SET status = ?, finished_at = ? " +
                "WHERE job_id = ? AND std_id = ? AND status != 'absent'";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setInt(3, jobId);
            pstmt.setString(4, stdId);

            int rows = pstmt.executeUpdate();

            // เช็คว่าอัปเดตสำเร็จไหม
            if (rows > 0) {
                System.out.println("Updated status to 'complete' for: " + stdId);
            } else {
                // ถ้า rows == 0 แปลว่าหาไม่เจอ หรือไม่ก็ status เป็น absent ไปแล้ว
                System.out.println("Skipped update for: " + stdId + " (Status might be 'absent' or invalid ID)");
            }
        }
    }

    /**
     * ✅ Helper 2: คำนวณสถิติ (Overload เพื่อรับ Connection)
     * จำเป็นต้องเขียนไว้ตรงนี้เพื่อให้ใช้ Connection เดียวกับ Transaction ได้
     */
    private void updateUserStats(Connection conn, String stdId) throws SQLException {
        String sqlSum = "SELECT COUNT(*) as total_jobs, SUM(hours_amount) as sum_hours, SUM(reward_amount) as sum_money " +
                "FROM job_assignment WHERE std_id = ? AND (status = 'complete' OR status = 'completed')";

        String sqlUpdate = "UPDATE profile SET finished_job_count = ?, total_hours = ?, total_money = ? WHERE std_id = ?";

        // ต้องมี Insert เผื่อยังไม่มี Profile
        String sqlInsert = "INSERT IGNORE INTO profile (std_id) VALUES (?)";

        // 1. Ensure profile exists
        try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
            pstmtInsert.setString(1, stdId);
            pstmtInsert.executeUpdate();
        }

        int totalJobs = 0;
        int totalHours = 0;
        double totalMoney = 0;

        // 2. Calculate Stats
        try (PreparedStatement pstmtSum = conn.prepareStatement(sqlSum)) {
            pstmtSum.setString(1, stdId);
            try (ResultSet rs = pstmtSum.executeQuery()) {
                if (rs.next()) {
                    totalJobs = rs.getInt("total_jobs");
                    totalHours = rs.getInt("sum_hours");
                    totalMoney = rs.getDouble("sum_money");
                }
            }
        }

        // 3. Update Stats
        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
            pstmtUpdate.setInt(1, totalJobs);
            pstmtUpdate.setInt(2, totalHours);
            pstmtUpdate.setDouble(3, totalMoney);
            pstmtUpdate.setString(4, stdId);
            pstmtUpdate.executeUpdate();
        }
    }
}