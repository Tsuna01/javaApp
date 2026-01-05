package model;

/**
 * PaidJobEntity คลาสสำหรับเก็บข้อมูลจากตาราง paid_jobs
 */
public class PaidJobEntity implements PaidJob {

    private int jobId;
    private int hourRate;
    private int userId;

    // Constructor เปล่า
    public PaidJobEntity() {
    }

    // Constructor สำหรับรับค่าทั้งหมด
    public PaidJobEntity(int jobId, int hourRate, int userId) {
        this.jobId = jobId;
        this.hourRate = hourRate;
        this.userId = userId;
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
    public int getHourRate() {
        return hourRate;
    }

    @Override
    public void setHourRate(int hourRate) {
        this.hourRate = hourRate;
    }

    @Override
    public int getUserId() {
        return userId;
    }

    @Override
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Method สำหรับ Debug ข้อมูล
    @Override
    public String toString() {
        return "PaidJobEntity{" +
                "jobId=" + jobId +
                ", hourRate=" + hourRate +
                ", userId=" + userId +
                '}';
    }
}