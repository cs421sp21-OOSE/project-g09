package dao.jdbiDao;

import dao.PostDao;
import dao.WishlistPostSkeletonDao;
import exceptions.DaoException;
import model.Post;
import model.User;
import model.WishlistPostSkeleton;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;
import util.jdbiResultSetHandler.ResultSetLinkedHashMapAccumulatorProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class JdbiWishlistPostSkeletonDao implements WishlistPostSkeletonDao {
    private final Jdbi jdbi;
    private final JdbiPostDao postDao;
    private final ResultSetLinkedHashMapAccumulatorProvider<WishlistPostSkeleton> wishListAccumulator;

    public JdbiWishlistPostSkeletonDao(Jdbi jdbi) {
        this.jdbi = jdbi;
        postDao = new JdbiPostDao(jdbi);
        wishListAccumulator = new ResultSetLinkedHashMapAccumulatorProvider<>(WishlistPostSkeleton.class);
    }

    @Override
    public WishlistPostSkeleton createWishListEntry(String postId, String userId) throws DaoException {
        String createWishlistEntrySql = "WITH inserted AS (INSERT INTO wishlist_post(post_id, user_id) VALUES(:post_id, :user_id) RETURNING *) SELECT * FROM inserted;";

        try {
            return jdbi.inTransaction(handle ->
                    handle.createQuery(createWishlistEntrySql)
                            .bind("post_id", postId)
                            .bind("user_id", userId)
                            .mapToBean(WishlistPostSkeleton.class)
                            .findOne()).orElse(null);
        } catch (StatementException | IllegalStateException | NullPointerException ex) {
            throw new DaoException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<Post> readAllWishlistEntries(String user_id) throws DaoException {
        List<WishlistPostSkeleton> wishlistSkeletonEntries = readAll(user_id);

        List<Post> wishlistPosts = new ArrayList<>();

        for(WishlistPostSkeleton wishlistSkeletonEntry : wishlistSkeletonEntries) {
            wishlistPosts.add(postDao.read(wishlistSkeletonEntry.getPostId()));
        }
        return wishlistPosts;
    }

    private List<WishlistPostSkeleton> readAll(String userId) throws DaoException {
        String sql = "SELECT * FROM wishlist_post WHERE user_id = :user_id;";

        try {
            return jdbi.inTransaction(handle ->
                    handle.createQuery(sql)
                            .bind("user_id", userId)
                            .mapToBean(WishlistPostSkeleton.class)
                            .list());
        } catch (StatementException | IllegalStateException ex) {
            throw new DaoException("Unable to read wishlist for userId " + userId, ex);
        }

    }


    @Override
    public WishlistPostSkeleton deleteWishlistEntry(String post_id, String user_id) throws DaoException {
        String deleteWishlistEntrySql = "WITH deleted AS (DELETE FROM wishlist_post WHERE post_id=:post_id AND user_id=:user_id RETURNING *) SELECT FROM deleted;";

        try {
            return jdbi.inTransaction(handle -> handle.createQuery(deleteWishlistEntrySql).bind("post_id", post_id).bind("user_id", user_id).mapToBean(WishlistPostSkeleton.class).findOne().orElse(null));
        } catch (StatementException | IllegalStateException | NullPointerException ex) {
            throw new DaoException(ex.getMessage(), ex);
        }
    }
}
