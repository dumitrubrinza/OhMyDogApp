package ie.wit.ohmydog.models;

/**
 * Created by dumitrubrinza on 10/04/2017.
 */

public class Dog {

    public String breed;
    public String desc;
    public String image;
    public String lostORfound;
    public String username;
    public String lat;
    public String lon;
    public String uid;

    public Dog(){
    }

    public Dog(String breed, String desc, String image, String lostORfound, String username, String lat, String lon, String uid) {
        this.breed = breed;
        this.desc = desc;
        this.image = image;
        this.lostORfound = lostORfound;
        this.username = username;
        this.lat = lat;
        this.lon = lon;
        this.uid = uid;
    }

    @Override
    public String toString(){
        return super.toString();
    }


    public String getBreed() {
        return breed;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getLostORfound() {
        return lostORfound;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLostORfound(String lostORfound) {
        this.lostORfound = lostORfound;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
