package model;

import lombok.Data;

@Data
public class WishlistPostSkeleton {
    private String postId;  //of post
    private String userId;  // of user who has it wish-listed.

    public WishlistPostSkeleton(String post_id, String user_id) {
        this.postId = post_id;
        this.userId = user_id;
    }

    //Don't need set/get functions! Lombok automates these.

}
