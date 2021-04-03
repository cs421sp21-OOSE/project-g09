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
     * @param post_id of the post to be "wish-listed"
     * @param user_id of the user who has "wish-listed" this post
     * @return The Post object created.
     * @throws DaoException A generic exception for CRUD operations.
     */
    WishlistPostSkeleton createWishListEntry(String post_id, String user_id) throws DaoException;

    /**
     * Read all Wishlist entries from the database with userId.
     *
     * @param user_id A search term.
     * @return All Posts retrieved.
     * @throws DaoException A generic exception for CRUD operations.
     */
    List<Post> readAllWishlistEntries(String user_id) throws DaoException;

    /**
     * Delete a wishlist entry.
     *
     * @param post_id of the post to be "wish-listed"
     * @param user_id of the user who has "wish-listed" this post
     * @return The Post object deleted from the data source.
     * @throws DaoException A generic exception for CRUD operations.
     */
    WishlistPostSkeleton deleteWishlistEntry(String post_id, String user_id) throws DaoException;

    List<WishlistPostSkeleton> readAll(String userId) throws DaoException;
}
