package util.paginationSkeleton;

import lombok.Data;
import model.Post;

import java.util.List;

@Data
public class PostPaginationSkeleton extends PaginationSkeleton{
  List<Post> posts;
  public PostPaginationSkeleton(){
    super();
  }
}
