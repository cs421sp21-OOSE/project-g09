package model;

import lombok.Data;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WishlistPostSkeleton)) return false;
        WishlistPostSkeleton skeleton = (WishlistPostSkeleton) o;
        return Objects.equals(postId, skeleton.postId) && Objects.equals(userId, skeleton.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, userId);
    }
}
