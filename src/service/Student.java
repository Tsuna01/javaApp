package service;

public class Student extends User {
    public int volunteerHours;
    public String status;

    public Student(int id, String name, String email, String password, String status) {
        super(id, name, email, password);
        this.status = "STUDENT";
    }

    public int getVolunteerHours() {
        return volunteerHours;
    }

    public void setVolunteerHours(int volunteerHours) {
        this.volunteerHours = volunteerHours;
    }

    @Override
    public String getStatus() {
        return status;
    }

}
