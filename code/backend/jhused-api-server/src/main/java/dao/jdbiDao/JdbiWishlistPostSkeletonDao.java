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
        String createWishlistEntrySql = "INSERT INTO wishlist_post(id, user_id) VALUES(:id, :user_id);";

        WishlistPostSkeleton newEntry = new WishlistPostSkeleton(postId, userId);

        try {
            return jdbi.inTransaction(handle -> {
                handle.createUpdate(createWishlistEntrySql).bindBean(newEntry).execute();
                return newEntry;
            });
        } catch (StatementException | IllegalStateException | NullPointerException ex) {
            throw new DaoException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<Post> readAllWishlistEntries(String userId) throws DaoException {
        List<WishlistPostSkeleton> wishlistSkeletonEntries = readAll(userId);

        List<Post> wishlistPosts = new ArrayList<>();

        for(WishlistPostSkeleton wishlistSkeletonEntry : wishlistSkeletonEntries) {
            wishlistPosts.add(postDao.read(wishlistSkeletonEntry.getId()));
        }
        return wishlistPosts;
    }

    private List<WishlistPostSkeleton> readAll(String userId) throws DaoException {
        String sql = "SELECT FROM wishlist_post WHERE wishlist_post.user_id = :userId;";

        try {
            return jdbi.inTransaction(handle ->
                    new ArrayList<>(handle.createQuery(sql)
                            .bind("user_id", userId)
                            .reduceResultSet(new LinkedHashMap<>(), wishListAccumulator)
                            .values()));
        } catch (StatementException | IllegalStateException ex) {
            throw new DaoException("Unable to read wishlist for userId " + userId, ex);
        }

    }



    @Override
    public WishlistPostSkeleton deleteWishlistEntry(String postId, String userId) throws DaoException {
        String deleteWishlistEntrySql = "WITH deleted AS (DELETE FROM wishlist_post WHERE id=:id AND user_id=:user_id RETURNING *) SELECT FROM deleted;";

        try {
            return jdbi.inTransaction(handle -> handle.createQuery(deleteWishlistEntrySql).bind("id", postId).bind("user_id", userId).mapToBean(WishlistPostSkeleton.class).findOne().orElse(null));
        } catch (StatementException | IllegalStateException | NullPointerException ex) {
            throw new DaoException(ex.getMessage(), ex);
        }
    }
}
