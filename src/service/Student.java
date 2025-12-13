package service;

public class Student extends User {
    public int volunteerHours;
    public String status;
    public String std_id;


    // Constructor หลัก
    public Student(int id, String name, String email, String password, String status, String std_id) {
        super(id, name, email, password);
        this.status = "STUDENT";
        this.std_id = std_id;
    }

    public String getStdId() {
        return std_id;
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
