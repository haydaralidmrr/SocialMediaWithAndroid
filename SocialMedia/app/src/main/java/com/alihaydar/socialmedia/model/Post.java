package com.alihaydar.socialmedia.model;

public class Post {
   public String comment,image,userPhoto,userName,date;

    public Post(String comment, String image, String userPhoto, String userName, String date) {
        this.comment = comment;
        this.image = image;
        this.userPhoto = userPhoto;
        this.userName = userName;
        this.date = date;
    }
}
