package model;

import lombok.Data;

@Data
public class WishlistPostSkeleton {
    private String post_id;  //of post
    private String user_id;  // of user who has it wish-listed.

    public WishlistPostSkeleton(String post_id, String user_id) {
        this.post_id = post_id;
        this.user_id = user_id;
    }

    //Don't need set/get functions! Lombok automates these.

}
