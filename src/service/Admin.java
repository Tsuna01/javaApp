package service;

public class Admin extends User {
    public String status;

    public Admin(int id, String name, String email, String password ,String status) {
        super(id, name, email, password);
        this.status = status;
    }
    
    @Override
    public String getStatus() {
        return status;
    }
    
}
