package com.granat.simpleblog;

public class Blog {
    private String post_title, post_desc, post_image_url;
    private String title;

    public Blog(){

    }

    public Blog(String post_title, String post_desc, String post_image_url) {
        this.post_title = post_title;
        this.post_desc = post_desc;
        this.post_image_url = post_image_url;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_desc() {
        return post_desc;
    }

    public void setPost_desc(String post_desc) {
        this.post_desc = post_desc;
    }

    public String getPost_image_url() {
        return post_image_url;
    }

    public void setPost_image_url(String post_image_url) {
        this.post_image_url = post_image_url;
    }
}
