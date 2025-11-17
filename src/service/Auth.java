package service;

public class Auth {
    public static boolean login(String username, String password) {
        return username.equals("admin") && password.equals("admin");
    }

    public static boolean register(String username, String password) {
        return true;
    }

    public static boolean logout() {
        return true;
    }

    public static boolean forgotPassword(String username) {
        return true;
    }

    public static boolean changePassword(String username, String password) {
        return true;
    }

    public static boolean resetPassword(String username, String password) {
        return true;
    }

    public static boolean checkUsername(String username) {
        return true;
    }
}
