package personal.project.android.blogapp;

import java.sql.Timestamp;
import java.util.Date;

public class BlogPost extends BlogPostId{
   public String user_id,url,title,description,uid;  //Names should be same as provided in put method in addData method
    public Date timeStamp;

    public BlogPost(String ui){
        this.uid=uid;
    }
    public BlogPost(){ }
    public BlogPost(String user_id, String url, String title, String description,Date timeStamp) {
        this.user_id = user_id;
        this.url = url;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
