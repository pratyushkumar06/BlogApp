package personal.project.android.blogapp.models;

import java.util.Date;

import personal.project.android.blogapp.models.BlogPostId;

public class CommentsContent extends BlogPostId {
    private String message,uid;
    private Date timestamp;

    public CommentsContent(){
    }

    public CommentsContent(String message, String uid, Date timestamp) {
        this.message = message;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDate() {
        return timestamp;
    }

    public void setDate(Date timestamp) {
        this.timestamp = timestamp;
    }
}
