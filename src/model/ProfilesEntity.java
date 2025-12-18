package model;

public class ProfilesEntity implements Profiles {

    // --- Attributes (ห้ามแก้ไข ตามที่คุณกำหนด) ---
    private int profile_id;
    private int user_id;
    private String bio;
    private String image_path;

    // --- Constructor ---

    // 1. Constructor เปล่า (จำเป็น)
    public ProfilesEntity() {
    }

    // 2. Constructor แบบใส่ข้อมูลครบ
    public ProfilesEntity(int profile_id, int user_id, String bio, String image_path) {
        this.profile_id = profile_id;
        this.user_id = user_id;
        this.bio = bio;
        this.image_path = image_path;
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

    // toString สำหรับ Debug ดูค่า
    @Override
    public String toString() {
        return "ProfilesEntity{" +
                "profile_id=" + profile_id +
                ", user_id=" + user_id +
                ", bio='" + bio + '\'' +
                ", image_path='" + image_path + '\'' +
                '}';
    }
}