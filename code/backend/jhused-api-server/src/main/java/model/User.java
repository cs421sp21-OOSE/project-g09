package model;
import lombok.Data;

@Data
public class User {
	private String jhId;
	private String userName;
	private String profileUrl;
	private String location;
	private String passWord;

	public User(String jhId, String userName, String profileUrl, String location, String passWord) {
		this.jhId = jhId;
		this.userName = userName;
		this.profileUrl = profileUrl;
		this.location = location;
		this.passWord = passWord;
	}
}
