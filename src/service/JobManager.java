package service;

import model.Job;
import model.JobEntity;
import service.Auth; // อย่าลืม import Auth

import java.sql.*;
import java.util.ArrayList;

public class JobManager {

    public boolean addJob(Job job, int hourRate) {
        // [แก้ไข] เพิ่ม end_date ใน SQL
        String sqlJob = "INSERT INTO job (title, details, location, workingHours, dateTime, end_date, imagePath, vacancies, job_type, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlPaid = "INSERT INTO paid_jobs (job_id, hour_rate, user_id) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmtJob = null;
        PreparedStatement pstmtPaid = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // --- 1. Insert ลงตาราง JOB ---
            pstmtJob = conn.prepareStatement(sqlJob, Statement.RETURN_GENERATED_KEYS);
            pstmtJob.setString(1, job.getTitle());
            pstmtJob.setString(2, job.getDetails());
            pstmtJob.setString(3, job.getLocation());
            pstmtJob.setInt(4, job.getWorkingHours());
            pstmtJob.setString(5, job.getDateTime());

            // [เพิ่ม] เช็ค end_date ว่าเป็น null หรือไม่
            if (job.getEndDate() != null && !job.getEndDate().isEmpty()) {
                pstmtJob.setString(6, job.getEndDate());
            } else {
                pstmtJob.setNull(6, java.sql.Types.TIMESTAMP);
            }

            pstmtJob.setString(7, job.getImagePath());
            pstmtJob.setInt(8, job.getVacancies());
            pstmtJob.setString(9, job.getJobType());
            pstmtJob.setInt(10, job.getUserId()); // ขยับ index เป็น 10

            int affectedRows = pstmtJob.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating job failed, no rows affected.");
            }

            // --- 2. ดึง job_id ---
            int newJobId = 0;
            generatedKeys = pstmtJob.getGeneratedKeys();
            if (generatedKeys.next()) {
                newJobId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating job failed, no ID obtained.");
            }

            // --- 3. Insert ลงตาราง PAID_JOBS ---
            if ("paid".equalsIgnoreCase(job.getJobType())) {
                pstmtPaid = conn.prepareStatement(sqlPaid);
                pstmtPaid.setInt(1, newJobId);
                pstmtPaid.setInt(2, hourRate);
                pstmtPaid.setInt(3, job.getUserId());
                pstmtPaid.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
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

    public ArrayList<Job> getJobs() {
        ArrayList<Job> jobList = new ArrayList<>();
        // ดึงข้อมูล job ทั้งหมด (อาจจะ join paid_jobs ด้วยก็ได้ถ้าต้องการ rate)
        String sql = "SELECT * FROM job ORDER BY job_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // สร้าง Object JobEntity (ซึ่ง implement Job)
                JobEntity job = new JobEntity();

                // Map ข้อมูลจาก Database เข้า Object
                // ** หมายเหตุ: ต้องมี Setter ใน JobEntity ให้ครบ **
                job.setJobId(rs.getInt("job_id")); // แปลง int เป็น String เพื่อให้ตรงกับ SearchEngine
                job.setTitle(rs.getString("title"));
                job.setDetails(rs.getString("details"));
                job.setLocation(rs.getString("location"));
                job.setWorkingHours(rs.getInt("workingHours"));
                job.setDateTime(rs.getString("dateTime"));

                // จัดการ end_date
                if (rs.getTimestamp("end_date") != null) {
                    job.setEndDate(rs.getString("end_date")); // ต้องดูว่าใน Model เก็บเป็น String หรือ Date
                }

                job.setImagePath(rs.getString("imagePath"));
                job.setVacancies(rs.getInt("vacancies"));
                job.setJobType(rs.getString("job_type"));
                job.setUserId(rs.getInt("user_id"));

                // เพิ่มเข้า List
                jobList.add(job);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return jobList;
    }

    // 2. + removeJob() (เหมือนเดิม)
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

    // 3. + applyJob() (เหมือนเดิม)
    public static boolean applyJob(int job_id, int hoursAmount) {

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

    // 4. + isJobApplied() (เหมือนเดิม)
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
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}