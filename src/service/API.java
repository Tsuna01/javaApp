package service;

import model.JobAssignment;
import model.JobAssignmentEntity;
import model.Profiles;
import model.ProfilesEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class API {
    // ... (ส่วนของตัวแปรและ getJobs, getJobDetail คงเดิม) ...
    public String jobId;
    public String title;
    public String details;
    public String location;
    public int workingHours;
    public String dateTime;
    public int vacancies;
    public String jobType;
    public String imagePath;

    public static ArrayList<API> getJobs() {
        // ... (โค้ดเดิมของคุณสำหรับดึงงานทั้งหมด) ...
        ArrayList<API> list = new ArrayList<>();
        String sql = "SELECT * FROM job";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                API job = new API();
                job.jobId = rs.getString("job_id");
                job.title = rs.getString("title");
                job.details = rs.getString("details");
                job.location = rs.getString("location");
                job.workingHours = rs.getInt("workingHours");
                job.dateTime = rs.getString("dateTime");
                job.vacancies = rs.getInt("vacancies");
                job.jobType = rs.getString("job_type");
                job.imagePath = rs.getString("imagePath");
                list.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<API> getJobDetail(String jobid) {
        // ... (โค้ดเดิมของคุณ) ...
        ArrayList<API> list = new ArrayList<>();
        String sql = "SELECT * FROM job WHERE job_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jobid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    API job = new API();
                    job.jobId = String.valueOf(rs.getInt("job_id"));
                    job.title = rs.getString("title");
                    job.details = rs.getString("details");
                    job.location = rs.getString("location");
                    job.workingHours = rs.getInt("workingHours");
                    job.dateTime = rs.getString("dateTime");
                    job.vacancies = rs.getInt("vacancies");
                    job.jobType = rs.getString("job_type");
                    job.imagePath = rs.getString("imagePath");
                    list.add(job);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // มาแสดง
    public static ArrayList<JobAssignment> getUserAssign(String std_id) {
        ArrayList<JobAssignment> list = new ArrayList<>();

        // SQL: Join ตาราง job เพื่อเอา title, imagePath, location, dateTime
        String sql = "SELECT ja.*, j.title, j.imagePath, j.location, j.dateTime " +
                "FROM job_assignment ja " +
                "JOIN job j ON ja.job_id = j.job_id " +
                "WHERE ja.std_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, std_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    JobAssignmentEntity assign = new JobAssignmentEntity();

                    assign.setAssignId(rs.getInt("assign_id"));
                    assign.setJobId(rs.getInt("job_id"));
                    assign.setStdId(rs.getString("std_id"));
                    assign.setStatus(rs.getString("status"));
                    assign.setAssignAt(rs.getString("assign_at"));
                    assign.setFinishedAt(rs.getString("finished_at"));
                    assign.setHoursAmount(rs.getInt("hours_amount"));
                    assign.setRewardAmount(rs.getInt("reward_amount"));

                    // ดึง title, imagePath, location, dateTime จากตาราง job ที่ Join มา
                    assign.setTitle(rs.getString("title"));
                    assign.setImagePath(rs.getString("imagePath"));
                    assign.setLocation(rs.getString("location"));
                    assign.setDateTime(rs.getString("dateTime"));

                    list.add(assign);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ... (method getProfile, saveProfile คงเดิม) ...
    public static ArrayList<Profiles> getProfile(int userId) {
        ArrayList<Profiles> list = new ArrayList<>();
        String sql = "SELECT * FROM profile WHERE user_id = ?"; // เช็คชื่อตาราง profile/profiles
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ProfilesEntity profile = new ProfilesEntity();
                    profile.setProfileId(rs.getInt("profile_id"));
                    profile.setUserId(rs.getInt("user_id"));
                    profile.setBio(rs.getString("bio"));
                    profile.setImagePath(rs.getString("image_path"));
                    list.add(profile);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<API> getHistoryJobAdmin(int user_id) {
        ArrayList<API> list = new ArrayList<>();
        String sql = "SELECT * FROM job WHERE user_id = ?";
        System.out.println("DEBUG API: Executing query: " + sql + " with user_id: " + user_id);
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user_id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    API job = new API();
                    job.jobId = String.valueOf(rs.getInt("job_id"));
                    job.title = rs.getString("title");
                    job.details = rs.getString("details");
                    job.location = rs.getString("location");
                    job.workingHours = rs.getInt("workingHours");
                    job.dateTime = rs.getString("dateTime");
                    job.vacancies = rs.getInt("vacancies");
                    job.jobType = rs.getString("job_type");
                    job.imagePath = rs.getString("imagePath");
                    list.add(job);
                    System.out.println("DEBUG API: Added job - " + job.title);
                }
            }
            System.out.println("DEBUG API: Total jobs fetched: " + list.size());
        } catch (SQLException e) {
            System.err.println("ERROR in getHistoryJobAdmin: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean saveProfile(int userId, String bio, String imagePath) {
        // ... (โค้ดเดิมของคุณ) ...
        boolean exists = false;
        String checkSql = "SELECT count(*) FROM profile WHERE user_id = ?"; // เช็คชื่อตาราง
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0)
                    exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql;
        if (exists)
            sql = "UPDATE profile SET bio = ?, image_path = ? WHERE user_id = ?";
        else
            sql = "INSERT INTO profile (bio, image_path, user_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bio);
            pstmt.setString(2, imagePath);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Inner class to hold worker info
    public static class WorkerInfo {
        public String stdId;
        public String name;
        public String status;
        public String assignAt;

        public WorkerInfo(String stdId, String name, String status, String assignAt) {
            this.stdId = stdId;
            this.name = name;
            this.status = status;
            this.assignAt = assignAt;
        }
    }

    // Get workers assigned to a specific job
    public static ArrayList<WorkerInfo> getJobWorkers(int jobId) {
        ArrayList<WorkerInfo> list = new ArrayList<>();
        String sql = "SELECT ja.std_id, ja.status, ja.assign_at, u.name " +
                "FROM job_assignment ja " +
                "LEFT JOIN user u ON ja.std_id = u.std_id " +
                "WHERE ja.job_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String stdId = rs.getString("std_id");
                    String name = rs.getString("name");
                    String status = rs.getString("status");
                    String assignAt = rs.getString("assign_at");

                    // Use std_id as name if name is null
                    if (name == null || name.trim().isEmpty()) {
                        name = "Student " + stdId;
                    }

                    list.add(new WorkerInfo(stdId, name, status, assignAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR in getJobWorkers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

}