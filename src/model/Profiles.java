package model;

public interface Profiles {

    int getProfileId();
    void setProfileId(int profileId);

    String getStdId(); // [เพิ่ม]
    void setStdId(String stdId);

    int getUserId();
    void setUserId(int userId);

    String getBio();
    void setBio(String bio);

    String getImagePath();
    void setImagePath(String imagePath);

    // [เพิ่ม] ส่วนสถิติ
    int getFinishedJobCount();
    void setFinishedJobCount(int finishedJobCount);

    int getTotalHours();
    void setTotalHours(int totalHours);

    int getTotalMoney();
    void setTotalMoney(int totalMoney);
}