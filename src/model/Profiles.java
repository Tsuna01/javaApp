package model;

public interface Profiles {

    int getProfileId();
    void setProfileId(int profileId);

    int getUserId();
    void setUserId(int userId);

    String getBio();
    void setBio(String bio);

    String getImagePath();
    void setImagePath(String imagePath);
}