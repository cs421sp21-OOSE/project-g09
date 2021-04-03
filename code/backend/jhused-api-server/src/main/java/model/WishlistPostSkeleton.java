package model;

import lombok.Data;

@Data
public class WishlistPostSkeleton {
    private String postId;  //of post
    private String userId;  // of user who has it wish-listed.

    public WishlistPostSkeleton() {

    }

    public WishlistPostSkeleton(String postId, String userId) {
        this.postId = postId;
        this.userId = userId;
    }

    //Don't need set/get functions! Lombok automates these.

}
