package util.paginationSkeleton;

import lombok.Data;

import java.util.Map;

@Data
public abstract class PaginationSkeleton {
  Map<String, Integer> pagination;
  Map<String, String> links;
}
