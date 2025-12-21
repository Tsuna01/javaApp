package model;

public class ProfilesEntity implements Profiles {

    // --- Attributes (ตรงตามชื่อคอลัมน์ใน DB) ---
    private int profile_id;
    private String std_id;        // [เพิ่ม]
    private int user_id;
    private String bio;
    private String image_path;

    // [เพิ่ม] ส่วนสถิติ
    private int finished_job_count;
    private int total_hours;
    private int total_money;

    // --- Constructor ---

    // 1. Constructor เปล่า
    public ProfilesEntity() {
    }

    // 2. Constructor แบบใส่ข้อมูลครบทุกช่อง
    public ProfilesEntity(int profile_id, String std_id, int user_id, String bio, String image_path,
                          int finished_job_count, int total_hours, int total_money) {
        this.profile_id = profile_id;
        this.std_id = std_id;
        this.user_id = user_id;
        this.bio = bio;
        this.image_path = image_path;
        this.finished_job_count = finished_job_count;
        this.total_hours = total_hours;
        this.total_money = total_money;
    }

    // --- Override Methods (Getter/Setter) ---

    @Override
    public int getProfileId() {
        return profile_id;
    }

    @Override
    public void setProfileId(int profileId) {
        this.profile_id = profileId;
    }

    @Override
    public String getStdId() {
        return std_id;
    }

    @Override
    public void setStdId(String stdId) {
        this.std_id = stdId;
    }

    @Override
    public int getUserId() {
        return user_id;
    }

    @Override
    public void setUserId(int userId) {
        this.user_id = userId;
    }

    @Override
    public String getBio() {
        return bio;
    }

    @Override
    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String getImagePath() {
        return image_path;
    }

    @Override
    public void setImagePath(String imagePath) {
        this.image_path = imagePath;
    }

    @Override
    public int getFinishedJobCount() {
        return finished_job_count;
    }

    @Override
    public void setFinishedJobCount(int finishedJobCount) {
        this.finished_job_count = finishedJobCount;
    }

    @Override
    public int getTotalHours() {
        return total_hours;
    }

    @Override
    public void setTotalHours(int totalHours) {
        this.total_hours = totalHours;
    }

    @Override
    public int getTotalMoney() {
        return total_money;
    }

    @Override
    public void setTotalMoney(int totalMoney) {
        this.total_money = totalMoney;
    }

    @Override
    public String toString() {
        return "ProfilesEntity{" +
                "profile_id=" + profile_id +
                ", std_id='" + std_id + '\'' +
                ", user_id=" + user_id +
                ", bio='" + bio + '\'' +
                ", image_path='" + image_path + '\'' +
                ", finished_job_count=" + finished_job_count +
                ", total_hours=" + total_hours +
                ", total_money=" + total_money +
                '}';
    }
}