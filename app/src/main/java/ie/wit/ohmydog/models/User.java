package ie.wit.ohmydog.models;

/**
 * Created by dumitrubrinza on 21/04/2017.
 */

public class User {

    public String name;
    public String image;

    public User(){
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public User(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
