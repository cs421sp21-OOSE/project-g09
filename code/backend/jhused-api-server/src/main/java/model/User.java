package model;
import lombok.Data;

import java.util.List;

@Data
public class User {
	private String id;
	private String jhId;
	private String userName;
	private String email;
	private String passWord;
	private String profileImage;
	private String location;
	private List<Post> postList;

	public User(String id, String jhId, String userName, String email, String passWord, String profileImage, String location, List<Post> postList) {
		this.id = id;
		this.jhId = jhId;
		this.userName = userName;
		this.email = email;
		this.passWord = passWord;
		this.profileImage = profileImage;
		this.location = location;
		this.postList = postList;
	}
}
