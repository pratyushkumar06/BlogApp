package personal.project.android.blogapp;

public class User {
    private String Url,name;

    public User(){ }
    public User(String url, String name) {
        Url = url;
        this.name = name;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
