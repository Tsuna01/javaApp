package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class WorkerManager {
    public String stdId;
    public String name;
    public String status;
    public String assignAt;

    public WorkerManager(String stdId, String name, String status, String assignAt) {
        this.stdId = stdId;
        this.name = name;
        this.status = status;
        this.assignAt = assignAt;
    }

    public static int getJobVacancies(int jobId) {
        String sql = "SELECT vacancies FROM job WHERE job_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("vacancies");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // ถ้าหาไม่เจอ หรือ Error ให้ถือว่าเป็น 0 ไว้ก่อน
    }

    public static ArrayList<WorkerManager> getJobWorkers(int jobId) {
        ArrayList<WorkerManager> list = new ArrayList<>();
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

                    list.add(new WorkerManager(stdId, name, status, assignAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR in getJobWorkers: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static int getCurrentWorkerCount(int jobId) {
        // นับทุกสถานะที่เป็นการจองที่ (เช่น pending, working, done) ยกเว้น cancelled
        String sql = "SELECT COUNT(*) AS count FROM job_assignment WHERE job_id = ? AND status != 'cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Add a worker to a job
    public static boolean addWorkerToJob(int jobId, String stdId) {
        // --- 1. เช็คว่างานเต็มหรือยัง ---
        int maxVacancies = getJobVacancies(jobId);
        int currentCount = getCurrentWorkerCount(jobId);

        if (currentCount >= maxVacancies) {
            System.err.println("Job is full! (Max: " + maxVacancies + ", Current: " + currentCount + ")");
            // หรือใช้ JOptionPane แจ้งเตือนตรงนี้เลยก็ได้ถ้าเป็น GUI App แบบง่าย
            return false; // ส่งกลับเป็น false เพื่อบอกว่าเพิ่มไม่ได้
        }

        // --- 2. เช็คว่ามีคนนี้อยู่แล้วหรือยัง (โค้ดเดิม) ---
        String checkSql = "SELECT assign_id FROM job_assignment WHERE job_id = ? AND std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, jobId);
            checkStmt.setString(2, stdId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    System.err.println("Worker already assigned to this job");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // --- 3. ทำการ Insert (โค้ดเดิม) ---
        String sql = "INSERT INTO job_assignment (job_id, std_id, status, assign_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            pstmt.setString(2, stdId);
            pstmt.setString(3, "pending"); // ตั้งค่าเริ่มต้นเป็น pending
            pstmt.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Remove a worker from a job
    public static boolean removeWorkerFromJob(int jobId, String stdId) {
        String sql = "DELETE FROM job_assignment WHERE job_id = ? AND std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            pstmt.setString(2, stdId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("ERROR in removeWorkerFromJob: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isStudentExists(String stdId) {
        String sql = "SELECT std_id FROM user WHERE std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("ERROR in isStudentExists: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static String getStudentName(String stdId) {
        String sql = "SELECT name FROM user WHERE std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR in getStudentName: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static int getUserIdByStdId(String stdId) {
        String sql = "SELECT user_id FROM user WHERE std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR in getUserIdByStdId: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public static String getStudentEmail(String stdId) {
        String sql = "SELECT email FROM user WHERE std_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR in getStudentEmail: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
