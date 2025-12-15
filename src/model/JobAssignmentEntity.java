package model;

public class JobAssignmentEntity implements JobAssignment {
    private int assignId;
    private int jobId;
    private String stdId;

    // --- แก้ไขจุดนี้ ---
    // เปลี่ยนจาก enum เป็น String เพื่อให้เก็บค่าได้และตรงกับ Interface
    private String status;
    // -----------------

    private String assignAt;
    private String finishedAt;
    private int rewardAmount;

    // --- Constructor ---
    public JobAssignmentEntity() {
        // Empty Constructor
    }

    public JobAssignmentEntity(int assignId, int jobId, String stdId, String status, int rewardAmount) {
        this.assignId = assignId;
        this.jobId = jobId;
        this.stdId = stdId;
        this.status = status;
        this.rewardAmount = rewardAmount;
    }

    // --- Override Methods from Interface ---

    @Override
    public int getAssignId() {
        return assignId;
    }

    @Override
    public void setAssignId(int assignId) {
        this.assignId = assignId;
    }

    @Override
    public int getJobId() {
        return jobId;
    }

    @Override
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    @Override
    public String getStdId() {
        return stdId;
    }

    @Override
    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getAssignAt() {
        return assignAt;
    }

    @Override
    public void setAssignAt(String assignAt) {
        this.assignAt = assignAt;
    }

    @Override
    public String getFinishedAt() {
        return finishedAt;
    }

    @Override
    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Override
    public int getRewardAmount() {
        return rewardAmount;
    }

    @Override
    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }
}