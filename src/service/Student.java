package service;

import ui.Profile;

import javax.swing.*;

public class Student extends User {
    public int volunteerHours;
    public String status;
    public String std_id;


    // Constructor หลัก
    public Student(int id, String name, String email, String password, String status, String std_id) {
        super(id, name, email, password);
        this.status = "student";
        this.std_id = std_id;
    }

    public String getStdId() {
        return std_id;
    }


    public int getVolunteerHours() {
        return volunteerHours;
    }

    @Override
    public void viewProfile() {
        User acc = Auth.getAuthUser();

        System.out.printf("USER ID     : %s%n", acc.getId());
        System.out.printf("USER EMAIL  : %s%n", acc.getEmail());
        System.out.printf("USER NAME   : %s%n", acc.getName());
        System.out.printf("USER VOLUNTEER : %s%n", getVolunteerHours());
        System.out.printf("USER STATUS : %s%n", acc.getStatus());

        SwingUtilities.invokeLater(() -> new Profile().setVisible(true));
    }

    public void setVolunteerHours(int volunteerHours) {
        this.volunteerHours = volunteerHours;
    }

    @Override
    public String getStatus() {
        return status;
    }

}
