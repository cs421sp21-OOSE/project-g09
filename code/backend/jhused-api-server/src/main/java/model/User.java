package model;
import lombok.Data;

import java.util.List;

@Data
public class User {
	private String id;
	private String name;
	private String email;
	private String profileImage;
	private String location;
	private List<Post> postList;

	public User(String id, String name, String email, String profileImage, String location, List<Post> postList) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.profileImage = profileImage;
		this.location = location;
		this.postList = postList;
	}
}
