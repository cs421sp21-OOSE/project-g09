package dao;

import exceptions.DaoException;
import model.Post;
import model.WishlistPostSkeleton;

import java.util.List;

public interface WishlistPostSkeletonDao {

    /**
     * Create an entry in wishlist_posts from a WishlistPostSkeleton item.
     *
     * @param wishlistPostSkeleton The wishlist entry to be added
     * @return The the post that has been "wish-listed"
     * @throws DaoException A generic exception for CRUD operations.
     */
    //Post createWishlistEntry(WishlistPostSkeleton wishlistPostSkeleton) throws DaoException;

    /**
     * Create a an entry in wishlist_posts from the postId and the userId
     *
     * @param postId of the post to be "wish-listed"
     * @param userId of the user who has "wish-listed" this post
     * @return The Post object created.
     * @throws DaoException A generic exception for CRUD operations.
     */
    WishlistPostSkeleton createWishListEntry(String postId, String userId) throws DaoException;

    /**
     * Read all Wishlist entries from the database with userId.
     *
     * @param userId A search term.
     * @return All Posts retrieved.
     * @throws DaoException A generic exception for CRUD operations.
     */
    List<Post> readAllWishlistEntries(String userId) throws DaoException;

    /**
     * Delete a wishlist entry.
     *
     * @param postId of the post to be "wish-listed"
     * @param userId of the user who has "wish-listed" this post
     * @return The Post object deleted from the data source.
     * @throws DaoException A generic exception for CRUD operations.
     */
    Post deleteWishlistEntry(String postId, String userId) throws DaoException;

}
