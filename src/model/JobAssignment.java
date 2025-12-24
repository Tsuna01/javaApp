package model;

public interface JobAssignment {
    int getAssignId();

    void setAssignId(int assignId);

    int getJobId();

    void setJobId(int jobId);

    // ยังต้องมี getTitle เพื่อใช้แสดงผลหน้าจอ (ดึงมาจากการ JOIN)
    String getTitle();

    void setTitle(String title);

    String getStdId();

    void setStdId(String stdId);

    String getStatus();

    void setStatus(String status);

    String getAssignAt();

    void setAssignAt(String assignAt);

    String getFinishedAt();

    void setFinishedAt(String finishedAt);

    int getHoursAmount();

    void setHoursAmount(int hoursAmount);

    int getRewardAmount();

    void setRewardAmount(int rewardAmount);

    // Additional fields for UI display
    String getImagePath();

    void setImagePath(String imagePath);

    String getLocation();

    void setLocation(String location);

    String getDateTime();

    void setDateTime(String dateTime);

    // [เพิ่ม] สำหรับ filter Job Type
    String getJobType();

    void setJobType(String jobType);
}