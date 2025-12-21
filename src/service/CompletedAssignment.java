package service;

import model.JobEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class CompletedAssignment extends JobAssignmentService {

    private double hourRate;

    public CompletedAssignment(JobEntity job, User student, String assignDate, double hourRate) {
        super(job, student, assignDate);
        this.hourRate = hourRate;
    }

    public CompletedAssignment() {
        super(null, null, null);
    }

    public boolean submitComplete(String jobId){
        String sql = "UPDATE job SET status = ? WHERE job_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int jobIdNew = Integer.parseInt(jobId);
            pstmt.setString(1, "complete");
            pstmt.setInt(2, jobIdNew);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void handleStatus() {
        String stdId = Auth.getAuthUser().getStd_id();

        if (student instanceof Student) {
            stdId = ((Student) student).getStdId();
        } else {


            stdId = ((Student) student).getStdId();
        }

        if (stdId != null) {
            updateUserStats(stdId);
        } else {
            System.err.println("Error: Student ID is null, cannot update stats.");
        }
    }

    @Override
    public String[] generateExportData() {
        double totalReward = job.getWorkingHours() * hourRate;
        return new String[] {
                assignDate,
                job.getTitle(),
                String.valueOf(job.getWorkingHours()),
                String.format("%.2f", totalReward)
        };
    }
}