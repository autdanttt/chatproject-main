package view.register;

public class UserRegister {
    private String email;
    private String full_name;
    private String password;

    public UserRegister() { }

    public UserRegister(String email, String full_name, String password) {
        this.email = email;
        this.full_name = full_name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

