package model;

public class HistoryEntity implements History {

    private int logId;
    private String stdId; // เปลี่ยนเป็น String
    private int jobId;
    private String status;
    private String createAt;
    private int hours; // เพิ่มใหม่

    public HistoryEntity() {
    }

    public HistoryEntity(int logId, String stdId, int jobId, String status, String createAt, int hours) {
        this.logId = logId;
        this.stdId = stdId;
        this.jobId = jobId;
        this.status = status;
        this.createAt = createAt;
        this.hours = hours;
    }

    @Override
    public int getLogId() { return logId; }
    @Override
    public void setLogId(int logId) { this.logId = logId; }

    @Override
    public String getStdId() { return stdId; }
    @Override
    public void setStdId(String stdId) { this.stdId = stdId; }

    @Override
    public int getJobId() { return jobId; }
    @Override
    public void setJobId(int jobId) { this.jobId = jobId; }

    @Override
    public String getStatus() { return status; }
    @Override
    public void setStatus(String status) { this.status = status; }

    @Override
    public String getCreateAt() { return createAt; }
    @Override
    public void setCreateAt(String createAt) { this.createAt = createAt; }

    @Override
    public int getHours() { return hours; }
    @Override
    public void setHours(int hours) { this.hours = hours; }

    @Override
    public String toString() {
        return "HistoryEntity{" +
                "logId=" + logId +
                ", stdId='" + stdId + '\'' +
                ", jobId=" + jobId +
                ", status='" + status + '\'' +
                ", createAt='" + createAt + '\'' +
                ", hours=" + hours +
                '}';
    }
}