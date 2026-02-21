package model;

import java.util.Objects;

public class AuthData {
    private final String authToken;
    private final String username;

    public AuthData(String authToken, String username){
        this.authToken = authToken;
        this.username = username;
    }
    public String getUsername(){
        return username;
    }
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthData authData)) {
            return false;
        }
        return Objects.equals(getAuthToken(), authData.getAuthToken()) && Objects.equals(getUsername(), authData.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthToken(), getUsername());
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
