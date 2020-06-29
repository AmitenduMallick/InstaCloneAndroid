package Model;

public class User {
    String Email;
    String ImageUrl;
    String Username;
    String bio;
    String id;

    public User() {
    }

    public User(String email, String imageUrl, String username, String bio, String id) {
        Email = email;
        ImageUrl = imageUrl;
        Username = username;
        this.bio = bio;
        this.id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
