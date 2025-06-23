package view.login;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AuthUser {
    private Long id;
    private String username;


    private String phoneNumber;

    private List<String> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
    public void addRole(String role) {
        this.getRoles().add(role);
    }
}
