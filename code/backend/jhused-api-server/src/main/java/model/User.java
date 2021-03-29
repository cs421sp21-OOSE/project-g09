package model;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class User {
	private String id;
	private String name;
	private String email;
	private String profileImage;
	private String location;
	private List<Post> postList;

	public User() {
	}

	public User(String id, String name, String email, String profileImage, String location, List<Post> postList) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.profileImage = profileImage;
		this.location = location;
		this.postList = postList;
	}

	public User(String id, String name, String email, String profileImage, String location) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.profileImage = profileImage;
		this.location = location;
	}

//	public void setPostList(List<Post> postList) {
//		if (postList != null) {
//			for (Post post: postList) {
//				post.setUserId(this.id);
//			}
//		}
//		this.postList = postList;
//	}
	public void addPostList(Post post) {
		if (postList == null)
			postList = new ArrayList<>();
		post.setUserId(this.id);
		postList.add(post);
	}
}
