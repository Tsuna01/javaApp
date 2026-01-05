package service;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class API {

    public String jobId;
    public String title;
    public String details;
    public String location;
    public int workingHours;
    public String dateTime;
    public String endDate;
    public int vacancies;
    public String status;
    public String jobType;
    public String imagePath;

    public static ArrayList<API> getJobs() {

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
                job.endDate = rs.getString("end_date");
                job.vacancies = rs.getInt("vacancies");
                job.jobType = rs.getString("job_type");
                job.status = rs.getString("status");
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
                    job.endDate = rs.getString("end_date");
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

        // SQL: Join ตาราง job เพื่อเอา title, imagePath, location, dateTime, job_type
        String sql = "SELECT ja.*, j.title, j.imagePath, j.location, j.dateTime, j.job_type " +
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

                    // ดึง title, imagePath, location, dateTime, job_type จากตาราง job ที่ Join มา
                    assign.setTitle(rs.getString("title"));
                    assign.setImagePath(rs.getString("imagePath"));
                    assign.setLocation(rs.getString("location"));
                    assign.setDateTime(rs.getString("dateTime"));
                    assign.setJobType(rs.getString("job_type")); // [เพิ่ม]

                    list.add(assign);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

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
                    job.endDate = rs.getString("end_date");
                    job.status = rs.getString("status");
                    job.vacancies = rs.getInt("vacancies");
                    job.jobType = rs.getString("job_type");
                    job.imagePath = rs.getString("imagePath");
                    list.add(job);

                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR in getHistoryJobAdmin: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<HistoryEntity> getHistory(String stdId){
        ArrayList<HistoryEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HistoryEntity history = new HistoryEntity();
                    history.setLogId(rs.getInt("log_id"));
                    history.setStdId(rs.getString("std_id"));
                    history.setJobId(rs.getInt("job_id"));
                    history.setStatus(rs.getString("status"));
                    history.setCreateAt(rs.getString("create_at"));
                    history.setHours(rs.getInt("hours"));
                    list.add(history);

                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR in getHistory: " + e.getMessage());
            e.printStackTrace();
        }
        return list;

    }

    public  static  ArrayList<PaidJobEntity> getPaidJob(String jobId){
        ArrayList<PaidJobEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM paid_jobs WHERE job_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, jobId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PaidJobEntity paid = new PaidJobEntity();
                    paid.setJobId(rs.getInt("job_id"));
                    paid.setHourRate(rs.getInt("hour_rate"));
                    paid.setUserId(rs.getInt("user_id"));

                    list.add(paid);

                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR in getPaidJob: " + e.getMessage());
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

    /**
     * ดึงข้อมูลงานที่เสร็จแล้วสำหรับ Export
     * 
     * @param stdId รหัสนักศึกษา
     * @return ArrayList ของ String[] ที่มี [วันที่, ชื่องาน, ชั่วโมง, จำนวนเงิน]
     */
    public static ArrayList<String[]> getCompletedAssignmentsForExport(String stdId) {
        ArrayList<String[]> list = new ArrayList<>();

        String sql = "SELECT ja.assign_at, ja.hours_amount, ja.reward_amount, j.title " +
                "FROM job_assignment ja " +
                "JOIN job j ON ja.job_id = j.job_id " +
                "WHERE ja.std_id = ? AND (ja.status = 'complete' OR ja.status = 'completed')";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, stdId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String assignAt = rs.getString("assign_at");
                    String title = rs.getString("title");
                    int hours = rs.getInt("hours_amount");
                    int reward = rs.getInt("reward_amount");

                    // แปลงวันที่เป็น format ที่อ่านง่าย
                    String dateFormatted = assignAt;
                    if (assignAt != null && assignAt.length() >= 10) {
                        dateFormatted = assignAt.substring(0, 10);
                    }

                    String[] row = new String[] {
                            dateFormatted,
                            title,
                            String.valueOf(hours),
                            String.valueOf(reward)
                    };
                    list.add(row);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in getCompletedAssignmentsForExport: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

}