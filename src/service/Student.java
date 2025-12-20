package service;

import ui.Profile;
import javax.swing.*;

public class Student extends User {
    // เก็บเฉพาะสิ่งที่ User ไม่มี คือชั่วโมงจิตอาสา
    private int volunteerHours;

    // Constructor เปล่า (เผื่อใช้)
    public Student() {
        super(0, "", "", "");
        this.setStatus("STUDENT");
    }

    // Constructor หลัก
    public Student(int id, String name, String email, String password, String status, String std_id) {
        super(id, name, email, password); // ส่งค่าพื้นฐานให้ User

        // [แก้ไข] ใช้ Setter ของ User เพื่อเก็บค่า จะได้ไม่ซ้ำซ้อน
        this.setStatus(status);
        this.setStd_id(std_id);
    }

    // [แก้ไข] เรียกใช้ getStd_id() จาก User โดยตรง
    public String getStdId() {
        return super.getStd_id();
    }

    public int getVolunteerHours() {
        return volunteerHours;
    }

    public void setVolunteerHours(int volunteerHours) {
        this.volunteerHours = volunteerHours;
    }

    @Override
    public void viewProfile() {
        // ใช้ this แทน Auth.getAuthUser() เพราะตัวมันเองคือ User ที่ login อยู่แล้ว
        System.out.printf("USER ID     : %s%n", getId());
        System.out.printf("USER EMAIL  : %s%n", getEmail());
        System.out.printf("USER NAME   : %s%n", getName());
        System.out.printf("USER VOLUNTEER : %s%n", getVolunteerHours());
        System.out.printf("USER STATUS : %s%n", getStatus());

        SwingUtilities.invokeLater(() -> new Profile().setVisible(true));
    }
}