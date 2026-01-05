package model;

public interface PaidJob {

    // job_id (Primary Key)
    int getJobId();
    void setJobId(int jobId);

    // hour_rate (อัตราค่าจ้างต่อชั่วโมง)
    int getHourRate();
    void setHourRate(int hourRate);

    // user_id (ID ของผู้โพสต์งาน หรือเจ้าของงาน)
    int getUserId();
    void setUserId(int userId);
}
