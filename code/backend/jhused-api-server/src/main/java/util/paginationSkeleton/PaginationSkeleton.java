package util.paginationSkeleton;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class PaginationSkeleton {
  Map<String, Integer> pagination;
  Map<String, String> links;

  public PaginationSkeleton(){
    pagination = new LinkedHashMap<>();
    pagination.put("page",1);
    pagination.put("limit",50);
    pagination.put("last",-1);
    pagination.put("total",-1);
    links = new LinkedHashMap<>();
    links.put("first","");
    links.put("last","");
    links.put("prev","");
    links.put("next","");
  }

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
