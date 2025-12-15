package service;

import model.JobAssignment;
import model.JobAssignmentEntity;

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

    public JobAssignment jobAssignment;

    // 2 ตัวนี้อาจจะต้องดึงแยก หรือปล่อยว่างไว้ก่อนถ้าในตาราง job ไม่มีคอลัมน์นี้ตรงๆ
    public ArrayList<String> applicants = new ArrayList<>();
    public ArrayList<String> assignments = new ArrayList<>();

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

        // return list ออกไป (ไม่ต้องใช้ finally ก็ได้ในกรณีนี้)
        return list;
    }
}
