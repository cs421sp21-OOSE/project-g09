package model;

import lombok.Data;

@Data
public class WishlistPostSkeleton {
    private String id;  //of post
    private String user_id;  // of user who has it wish-listed.

    public WishlistPostSkeleton(String id, String user_id) {
        this.id = id;
        this.user_id = user_id;
    }

    //Don't need set/get functions! Lombok automates these.

}
