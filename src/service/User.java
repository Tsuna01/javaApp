package service;

public abstract class User {
    // Attribute คงเดิมตามที่ขอ
    private int id;
    private String name;
    private String email;
    private String password;
    private String status;
    private String std_id;

    abstract public void viewProfile();

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = "STUDENT";
    }


    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // ในการใช้งานจริง ควร Hash Password ก่อน set เข้ามานะครับ
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    // เพิ่ม setter สำหรับ status (เดิมไม่มี ทำให้เปลี่ยนสถานะไม่ได้)
    public void setStatus(String status) {
        this.status = status;
    }


    // --- Helper Method ---


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}'; // ไม่โชว์ password
    }
}