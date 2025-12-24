package model;

public class JobAssignmentEntity implements JobAssignment {
    private int assignId;
    private int jobId;
    private String title; // เก็บ Title ไว้ใช้แสดงผล
    private String stdId;
    private String status;
    private String assignAt;
    private String finishedAt;
    private int hoursAmount;
    private int rewardAmount;
    private String imagePath;
    private String location;
    private String dateTime;

    public JobAssignmentEntity() {
    }

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
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
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
    public int getHoursAmount() {
        return hoursAmount;
    }

    @Override
    public void setHoursAmount(int hoursAmount) {
        this.hoursAmount = hoursAmount;
    }

    @Override
    public int getRewardAmount() {
        return rewardAmount;
    }

    @Override
    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getDateTime() {
        return dateTime;
    }

    @Override
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    // [เพิ่ม] สำหรับ filter Job Type
    private String jobType;

    @Override
    public String getJobType() {
        return jobType;
    }

    @Override
    public void setJobType(String jobType) {
        this.jobType = jobType;
    }
}