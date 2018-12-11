package cn.heima.myutilslibrary.bean;


import java.io.Serializable;

public class ConmentStandardBean extends BaseBean implements Serializable {

    private String comment_content;
    private int comment_id;
    private String comment_image_url;
    private String comment_nickname;
    private int comment_number;
    private String comment_time;
    private String comment_title;
    private String comment_parent_nickname;
    private boolean isSecond;

    public ConmentStandardBean() {
    }

    public ConmentStandardBean(String comment_content, int comment_id, String comment_image_url, String comment_nickname, int comment_number, String comment_time, String comment_title, String comment_parent_nickname, boolean isSecond) {
        this.comment_content = comment_content;
        this.comment_id = comment_id;
        this.comment_image_url = comment_image_url;
        this.comment_nickname = comment_nickname;
        this.comment_number = comment_number;
        this.comment_time = comment_time;
        this.comment_title = comment_title;
        this.comment_parent_nickname = comment_parent_nickname;
        this.isSecond = isSecond;
    }

    public boolean isSecond() {
        return isSecond;
    }

    public void setSecond(boolean second) {
        isSecond = second;
    }

    public String getComment_parent_nickname() {
        return comment_parent_nickname;
    }

    public void setComment_parent_nickname(String comment_parent_nickname) {
        this.comment_parent_nickname = comment_parent_nickname;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_image_url() {
        return comment_image_url;
    }

    public void setComment_image_url(String comment_image_url) {
        this.comment_image_url = comment_image_url;
    }

    public String getComment_nickname() {
        return comment_nickname;
    }

    public void setComment_nickname(String comment_nickname) {
        this.comment_nickname = comment_nickname;
    }

    public int getComment_number() {
        return comment_number;
    }

    public void setComment_number(int comment_number) {
        this.comment_number = comment_number;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public String getComment_title() {
        return comment_title;
    }

    public void setComment_title(String comment_title) {
        this.comment_title = comment_title;
    }
}
