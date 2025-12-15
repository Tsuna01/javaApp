package model;

public interface JobAssignment {
    // ID การมอบหมายงาน
    int getAssignId();
    void setAssignId(int assignId);

    // ID ของงาน
    int getJobId();
    void setJobId(int jobId);

    // ID ของนักศึกษา (เพิ่มให้เพราะใน Entity มี)
    String getStdId();
    void setStdId(String stdId);

    // สถานะ
    String getStatus();
    void setStatus(String status);

    // เวลาที่มอบหมาย
    String getAssignAt();
    void setAssignAt(String assignAt);

    // เวลาที่เสร็จ
    String getFinishedAt();
    void setFinishedAt(String finishedAt);

    // ค่าตอบแทน
    int getRewardAmount();
    void setRewardAmount(int rewardAmount);
}