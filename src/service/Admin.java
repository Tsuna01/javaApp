package service;

import ui.Profile;

import javax.swing.*;

public class Admin extends User {
    public User user;
    public String status;

    public Admin(int id, String name, String email, String password ,String status) {
        super(id, name, email, password);
        this.status = status;
    }

    @Override
    public void viewProfile() {
        User acc = Auth.getAuthUser();
        SwingUtilities.invokeLater(() -> new Profile().setVisible(true));

        System.out.printf("USER ID     : %s%n", acc.getId());
        System.out.printf("USER EMAIL  : %s%n", acc.getEmail());
        System.out.printf("USER NAME   : %s%n", acc.getName());
        System.out.printf("USER STATUS : %s%n", acc.getStatus());
    }

    @Override
    public String getStatus() {
        return status;
    }
    
}
