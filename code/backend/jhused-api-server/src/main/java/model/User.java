package model;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Data
public class User {
	private String id;
	private String name;
	private String email;
	private String profileImage;
	private String location;
	private List<Post> posts;
	private List<Post> wishlist;

	public User() {
		this.posts = new ArrayList<>();
		this.wishlist = new ArrayList<>();
	}

	/**
	 * Constructor using all but wishlist.
	 * @param id
	 * @param name
	 * @param email
	 * @param profileImage
	 * @param location
	 * @param posts
	 */
	public User(String id, String name, String email, String profileImage, String location, List<Post> posts) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.profileImage = profileImage;
		this.location = location;
		this.posts = posts;
	}

	/**
	 * Full constructor including wishlist
	 * @param id
	 * @param name
	 * @param email
	 * @param profileImage
	 * @param location
	 * @param posts
	 * @param wishlist
	 */
	public User(String id, String name, String email, String profileImage, String location, List<Post> posts, List<Post> wishlist) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.profileImage = profileImage;
		this.location = location;
		this.posts = posts;
		this.wishlist = wishlist;
	}

	/**
	 * Constructor excluding posts and wishlist.
	 * @param id
	 * @param name
	 * @param email
	 * @param profileImage
	 * @param location
	 */
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
	public void addPosts(Post post) {
		if (posts == null)
			posts = new ArrayList<>();
		post.setUserId(this.id);
		posts.add(post);
	}

	/**
	 * Add post to this user's wishlist.
	 * @param post
	 */
	public void addWishlistPost(Post post) {
		if (wishlist == null)
			wishlist = new ArrayList<>();
		wishlist.add(post);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(email, user.email) && Objects.equals(profileImage, user.profileImage) && Objects.equals(location, user.location) && Objects.equals(new HashSet(posts),new HashSet(user.posts));
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, email, profileImage, location,new HashSet(posts));
	}
}
