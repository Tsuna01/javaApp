package service;

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

                job.jobId = rs.getString("jobid");
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

    public static boolean addJob(){
        return true;
    }

    public static ArrayList<API> getJobDetail(String jobid) {
        ArrayList<API> list = new ArrayList<>();
        // เช็คชื่อ column ดีๆ ว่าใน DB ชื่อ 'jobid', 'job_id' หรือ 'id'
        String sql = "SELECT * FROM job WHERE jobid = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, jobid); // แทนที่ ? ด้วยค่า jobid ที่รับเข้ามา

            try (ResultSet rs = pstmt.executeQuery()) {
                // ใช้ if เพราะเราคาดหวังผลลัพธ์แค่ 1 แถว (ค้นหาตาม ID)
                if (rs.next()) {
                    API job = new API();
                    job.jobId = String.valueOf(rs.getInt("jobid"));

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


}