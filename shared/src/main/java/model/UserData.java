package model;

import java.util.Objects;

public class UserData {
    private final String username;
    private final String password;
    private final String email;

    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserData userData)) {
            return false;
        }
        return Objects.equals(getUsername(), userData.getUsername()) && Objects.equals(getPassword(), userData.getPassword()) && Objects.equals(getEmail(), userData.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getPassword(), getEmail());
    }

    @Override
    public String toString() {
        return "UserData{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
