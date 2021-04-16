package util.paginationSkeleton;

import java.util.Map;

public abstract class PaginationSkeleton {
  Map<String, Integer> pagination;
  Map<String, String> links;

  public void setPagination(Map<String, Integer> pagination) {
    this.pagination = pagination;
  }

  public void setLinks(Map<String, String> links) {
    this.links = links;
  }

  public Map<String, Integer> getPagination() {
    return pagination;
  }

  public Map<String, String> getLinks() {
    return links;
  }
}
