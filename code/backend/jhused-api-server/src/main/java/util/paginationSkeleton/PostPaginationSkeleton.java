package util.paginationSkeleton;

import model.Post;

import java.util.List;

public class PostPaginationSkeleton extends PaginationSkeleton {
  List<Post> posts;

  public PostPaginationSkeleton() {
    super();
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
  }

  public List<Post> getPosts() {
    return posts;
  }
}
