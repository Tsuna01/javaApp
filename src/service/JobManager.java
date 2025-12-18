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

    // 3. + getJobById()
    public Job getJobById(int jobId) {
        String sql = "SELECT * FROM job WHERE job_id = ?";
        Job job = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                job = mapResultSetToJob(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return job;
    }

    // 4. + searchJobsByTitle()
    public ArrayList<Job> searchJobsByTitle(String keyword) {
        String sql = "SELECT * FROM job WHERE title LIKE ?";
        ArrayList<Job> jobList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                jobList.add(mapResultSetToJob(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    // 5. + searchJobsByLocation()
    public ArrayList<Job> searchJobsByLocation(String location) {
        String sql = "SELECT * FROM job WHERE location LIKE ?";
        ArrayList<Job> jobList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + location + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                jobList.add(mapResultSetToJob(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobList;
    }

    // 6. + sortJobsByDate()
    public ArrayList<Job> sortJobsByDate() {
        ArrayList<Job> allJobs = getAllJobs();
        // แก้ไข: ใช้ getDateTime ตาม Interface
        allJobs.sort(Comparator.comparing(Job::getDateTime));
        return allJobs;
    }

    // 7. + sortJobsByVacancies()
    public ArrayList<Job> sortJobsByVacancies() {
        ArrayList<Job> allJobs = getAllJobs();
        allJobs.sort(Comparator.comparingInt(Job::getVacancies).reversed());
        return allJobs;
    }

    // --- Helper Methods ---

    private ArrayList<Job> getAllJobs() {
        String sql = "SELECT * FROM job";
        ArrayList<Job> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToJob(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper: แปลง ResultSet เป็น Job Object
    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        Job job = new JobEntity();
        job.setJobId(rs.getInt("job_id"));
        job.setTitle(rs.getString("title"));
        job.setDetails(rs.getString("details"));
        job.setLocation(rs.getString("location"));
        job.setWorkingHours(rs.getInt("workingHours"));
        job.setDateTime(rs.getString("dateTime"));
        job.setImagePath(rs.getString("imagePath"));
        job.setVacancies(rs.getInt("vacancies"));
        job.setJobType(rs.getString("job_type"));
        job.setUserId(rs.getInt("user_id"));
        return job;
    }
}