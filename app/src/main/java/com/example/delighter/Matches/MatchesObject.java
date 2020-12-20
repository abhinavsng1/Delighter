package com.example.delighter.Matches;

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImage;
    private String userSex;
    public MatchesObject(String userId,String name,String profileImage,String userSex){
        this.userId=userId;
        this.name=name;
        this.profileImage=profileImage;
        this.userSex=userSex;
    }
    public String getProfileImage(){ return profileImage; }
    public void setProfileImage(String profileImage){ this.profileImage=profileImage;}

    public String getUserSex(){ return userSex; }
    public void setUserSex(String userSex){ this.userSex=userSex;}

    public String getUserId(){ return userId; }
    public void setUserId(String userId){ this.userId=userId;}

    public String getName(){ return name; }
    public void setName(String name){ this.name=name;}
}
