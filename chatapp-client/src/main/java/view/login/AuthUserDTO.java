package view.login;

import java.util.HashSet;
import java.util.Set;

public class AuthUserDTO {
    private Long id;
    private String username;


    private String phoneNumber;

    private Set<RoleDTO> roles = new HashSet<>();

    public AuthUserDTO() {
    }

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

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }

    public void addRole(RoleDTO role) {
        this.getRoles().add(role);
    }


}
