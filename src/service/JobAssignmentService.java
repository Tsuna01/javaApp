package service;

import model.JobEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class JobAssignmentService {
    protected JobEntity job;
    protected User student;      // ใช้ service.User
    protected String assignDate;

    public JobAssignmentService(JobEntity job, User student, String assignDate) {
        this.job = job;
        this.student = student;
        this.assignDate = assignDate;
    }

    public abstract void handleStatus();
    public abstract String[] generateExportData();

    protected void updateUserStats(String stdId) {
        // ... (โค้ดส่วนนี้เหมือนเดิมครับ ไม่ต้องแก้) ...
        String sqlSum = "SELECT " +
                "COUNT(*) as total_jobs, " +
                "SUM(hours_amount) as sum_hours, " +
                "SUM(reward_amount) as sum_money " +
                "FROM job_assignment " +
                "WHERE std_id = ? AND (status = 'complete' OR status = 'completed')";

        String sqlUpdate = "UPDATE profile SET " +
                "finished_job_count = ?, " +
                "total_hours = ?, " +
                "total_money = ? " +
                "WHERE std_id = ?";

        String sqlInsert = "INSERT IGNORE INTO profile (std_id) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                pstmtInsert.setString(1, stdId);
                pstmtInsert.executeUpdate();
            }

            int totalJobs = 0;
            int totalHours = 0;
            double totalMoney = 0;

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

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setInt(1, totalJobs);
                pstmtUpdate.setInt(2, totalHours);
                pstmtUpdate.setDouble(3, totalMoney);
                pstmtUpdate.setString(4, stdId);
                pstmtUpdate.executeUpdate();
                System.out.println("Updated stats for user " + stdId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}