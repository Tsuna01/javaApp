package model;

public interface History {

    // log_id
    int getLogId();
    void setLogId(int logId);

    // std_id (เปลี่ยนจาก user_id เป็น std_id และใช้ String ตาม varchar)
    String getStdId();
    void setStdId(String stdId);

    // job_id
    int getJobId();
    void setJobId(int jobId);

    // status
    String getStatus();
    void setStatus(String status);

    // create_at
    String getCreateAt();
    void setCreateAt(String createAt);

    // hours (เพิ่มใหม่)
    int getHours();
    void setHours(int hours);
}