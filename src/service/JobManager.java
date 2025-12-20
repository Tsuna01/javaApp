package service;

import model.Job;
import model.JobEntity; // ต้อง import Implementation class

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;

public class JobManager {

    // 1. + addJob()
    public boolean addJob(Job job, int hourRate) {
        String sqlJob = "INSERT INTO job (title, details, location, workingHours, dateTime, imagePath, vacancies, job_type, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // [แก้ไข 1] เพิ่ม job_id และ user_id ในคำสั่ง SQL
        String sqlPaid = "INSERT INTO paid_jobs (job_id, hour_rate, user_id) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmtJob = null;
        PreparedStatement pstmtPaid = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // เริ่ม Transaction

            // --- 1. Insert ลงตาราง JOB ---
            pstmtJob = conn.prepareStatement(sqlJob, Statement.RETURN_GENERATED_KEYS);
            pstmtJob.setString(1, job.getTitle());
            pstmtJob.setString(2, job.getDetails());
            pstmtJob.setString(3, job.getLocation());
            pstmtJob.setInt(4, job.getWorkingHours());
            pstmtJob.setString(5, job.getDateTime());
            pstmtJob.setString(6, job.getImagePath());
            pstmtJob.setInt(7, job.getVacancies());
            pstmtJob.setString(8, job.getJobType());
            pstmtJob.setInt(9, job.getUserId());

            int affectedRows = pstmtJob.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating job failed, no rows affected.");
            }

            // --- 2. ดึง job_id ที่เพิ่งสร้าง ---
            int newJobId = 0;
            generatedKeys = pstmtJob.getGeneratedKeys();
            if (generatedKeys.next()) {
                newJobId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating job failed, no ID obtained.");
            }

            // --- 3. Insert ลงตาราง PAID_JOBS (ถ้าเป็นงานจ้าง) ---
            if ("paid".equalsIgnoreCase(job.getJobType())) {
                pstmtPaid = conn.prepareStatement(sqlPaid);

                // [แก้ไข 2] ส่งค่า job_id, hour_rate และ user_id ให้ครบ
                pstmtPaid.setInt(1, newJobId);   // job_id ที่เพิ่งได้มา
                pstmtPaid.setInt(2, hourRate);   // hour_rate
                pstmtPaid.setInt(3, job.getUserId()); // user_id

                pstmtPaid.executeUpdate();
            }

            conn.commit(); // ยืนยันข้อมูล
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // ย้อนกลับถ้ามีปัญหา
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmtJob != null) pstmtJob.close();
                if (pstmtPaid != null) pstmtPaid.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 2. + removeJob()
    public boolean removeJob(int jobId) {
        String sql = "DELETE FROM job WHERE job_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean applyJob(int job_id, int hoursAmount) {

        // [แก้ไข] SQL ไม่ต้อง Insert title แล้ว
        String sqlJob = "INSERT INTO job_assignment " +
                "(job_id, std_id, status, assign_at, finished_at, reward_amount, hours_amount) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sqlJob);

            pstmt.setInt(1, job_id);

            String currentStdId = null;
            User currentUser = Auth.getAuthUser();

            if (currentUser instanceof Student) {
                currentStdId = ((Student) currentUser).getStdId();
            } else {
                currentStdId = currentUser.getStd_id();
            }

            if (currentStdId == null) {
                System.err.println("Error: Current Student ID is NULL. Cannot apply job.");
                return false;
            }
            pstmt.setString(2, currentStdId);
            pstmt.setString(3, "pending");
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setNull(5, java.sql.Types.TIMESTAMP);
            pstmt.setObject(6, null);
            pstmt.setInt(7, hoursAmount);

            // ไม่ต้อง set title แล้ว

            int rowAffected = pstmt.executeUpdate();
            return rowAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static boolean isJobApplied(int jobId) {
        User user = Auth.getAuthUser();
        if (user == null) return false;

        String stdId = null;
        if (user instanceof Student) {
            stdId = ((Student) user).getStdId();
        } else {
            stdId = user.getStd_id();
        }

        if (stdId == null) return false;

        String sql = "SELECT assign_id FROM job_assignment WHERE job_id = ? AND std_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jobId);
            pstmt.setString(2, stdId);

            try (ResultSet rs = pstmt.executeQuery()) {
                // ถ้ามีข้อมูล (next() เป็น true) แสดงว่าสมัครไปแล้ว
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}