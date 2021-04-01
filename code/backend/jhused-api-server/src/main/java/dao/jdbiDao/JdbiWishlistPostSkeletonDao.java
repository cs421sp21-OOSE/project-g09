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
    private final PostDao postDao;

    public JdbiWishlistPostSkeletonDao(Jdbi jdbi) {
        this.jdbi = jdbi;
        this.postDao = new JdbiPostDao(jdbi);
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

    //TODO need help here
    @Override
    public List<Post> readAllWishlistEntries(String userId) throws DaoException {
        String sql = "SELECT FROM wishlist_post WHERE wishlist_post.user_id = :userId;";
        try {
            return jdbi.inTransaction(handle -> new ArrayList<>(handle.createQuery(sql)
                    .bind("userId", userId)
                    .reduceResultSet(new LinkedHashMap<>(), postAccumulator)
                    .values()));
        } catch (StatementException | IllegalStateException ex) {
            throw new DaoException("Unable to read a post for userId" + userId, ex);
        }
    }

    @Override
    public Post deleteWishlistEntry(String postId, String userId) throws DaoException {
        return null;
    }
}
