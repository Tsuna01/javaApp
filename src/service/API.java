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

    // Job Work
    public String jobId;
    public String title;
    public String details;
    public String location;
    public int workingHours;
    public String dateTime;
    public int vacancies;
    public String jobType;
    public String imagePath;

    // เปลี่ยน Return Type เป็น ArrayList<API> เพื่อส่งกลับรายการงานทั้งหมด
    public static ArrayList<API> getJobs() {
        ArrayList<API> list = new ArrayList<>();
        String sql = "SELECT * FROM job";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // 2. วนลูปอ่านข้อมูลทีละแถว
            while (rs.next()) {
                API job = new API();

                // 3. Map ข้อมูลจาก Database (ชื่อใน "..." ต้องตรงกับชื่อ column ใน DB)

                job.jobId = rs.getString("job_id");
                job.title = rs.getString("title");
                job.details = rs.getString("details");
                job.location = rs.getString("location");
                job.workingHours = rs.getInt("workingHours");

                // แปลง Date/Time เป็น String (หรือจะดึงเป็น Timestamp ก็ได้)
                job.dateTime = rs.getString("dateTime");
                job.vacancies = rs.getInt("vacancies");
                job.jobType = rs.getString("job_type");
                job.imagePath = rs.getString("imagePath");

                // เพิ่ม object ลงใน list
                list.add(job);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            return list;
        }
    }

    public static ArrayList<API> getJobDetail(String jobid) {
        ArrayList<API> list = new ArrayList<>();
        // เช็คชื่อ column ดีๆ ว่าใน DB ชื่อ 'jobid', 'job_id' หรือ 'id'
        String sql = "SELECT * FROM job WHERE job_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, jobid); // แทนที่ ? ด้วยค่า jobid ที่รับเข้ามา

            try (ResultSet rs = pstmt.executeQuery()) {
                // ใช้ if เพราะเราคาดหวังผลลัพธ์แค่ 1 แถว (ค้นหาตาม ID)
                if (rs.next()) {
                    API job = new API();
                    job.jobId = String.valueOf(rs.getInt("job_id"));

                    job.title = rs.getString("title");
                    job.details = rs.getString("details");
                    job.location = rs.getString("location");
                    job.workingHours = rs.getInt("workingHours");
                    job.dateTime = rs.getString("dateTime");
                    job.vacancies = rs.getInt("vacancies");
                    job.jobType = rs.getString("job_type"); // เช็คชื่อ column ใน DB ให้ตรง (เช่น job_type)

                    // เพิ่มลงใน List
                    list.add(job);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // แก้ไข Return Type เป็น ArrayList<JobAssignment>
    public static ArrayList<JobAssignment> getJobAssign() {
        // สร้าง List ที่เก็บ JobAssignment
        ArrayList<JobAssignment> list = new ArrayList<>();
        String sql = "SELECT * FROM job_assignment";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 1. สร้าง Object ของ JobAssignmentEntity (ไม่ใช่ API)
                JobAssignmentEntity assign = new JobAssignmentEntity();

                // 2. Map ข้อมูลโดยใช้ Setter (เพราะตัวแปรเป็น private)
                // ต้องเช็ค Data Type ใน Database ให้ตรงกับ rs.get...()

                assign.setAssignId(rs.getInt("assign_id"));     // int
                assign.setJobId(rs.getInt("job_id"));           // int
                assign.setStdId(rs.getString("std_id"));        // String
                assign.setStatus(rs.getString("status"));       // String

                // วันที่ใน DB มักเป็น Timestamp หรือ String
                // ถ้าใน Entity ประกาศเป็น String ให้ใช้ getString
                assign.setAssignAt(rs.getString("assign_at"));
                assign.setFinishedAt(rs.getString("finished_at"));

                assign.setRewardAmount(rs.getInt("reward_amount")); // int

                // 3. เพิ่มลงใน List
                list.add(assign);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<JobAssignment> getUserAssign(String std_id) {
        ArrayList<JobAssignment> list = new ArrayList<>();

        String sql = "SELECT * FROM job_assignment WHERE std_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, std_id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // แก้ไขจุดนี้: ใช้ JobAssignmentEntity แทน
                    JobAssignmentEntity assign = new JobAssignmentEntity();

                    // Map ข้อมูล
                    assign.setAssignId(rs.getInt("assign_id"));
                    assign.setJobId(rs.getInt("job_id"));
                    assign.setStdId(rs.getString("std_id"));
                    assign.setStatus(rs.getString("status"));
                    assign.setAssignAt(rs.getString("assign_at"));
                    assign.setFinishedAt(rs.getString("finished_at"));
                    assign.setRewardAmount(rs.getInt("reward_amount"));

                    // เพิ่มลง List (ทำได้เพราะ JobAssignmentEntity implements JobAssignment)
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

        // แก้ไขชื่อตารางตรงนี้ครับ (เดาว่าชื่อ profile)
        String sql = "SELECT * FROM profile WHERE user_id = ?";

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

    public static boolean saveProfile(int userId, String bio, String imagePath) {
        // 1. ลองเช็คก่อนว่ามี Profile หรือยัง
        boolean exists = false;
        String checkSql = "SELECT count(*) FROM profiles WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // 2. ถ้ามีแล้วให้ UPDATE, ถ้ายังไม่มีให้ INSERT
        String sql;
        if (exists) {
            sql = "UPDATE profiles SET bio = ?, image_path = ? WHERE user_id = ?";
        } else {
            sql = "INSERT INTO profiles (bio, image_path, user_id) VALUES (?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bio);
            pstmt.setString(2, imagePath);
            pstmt.setInt(3, userId); // Parameter ตัวที่ 3 คือ user_id ทั้งคู่

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
