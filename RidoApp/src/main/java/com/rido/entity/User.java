package com.rido.entity;

import java.util.Date

;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phoneNo") })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;

	private String name;

	@NotBlank
	@Size(max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@NotBlank
	@Size(max = 15)
	@Column(name = "phoneNo")
	private String phoneNo;

	@NotBlank
	@Size(max = 120)
	private String password;

//	private String firstName;
//
//	private String lastName;

	private String otp;

	private String alternativeNo;

	private String gender;

	private Date Dob;

	private String image;

	private String imageProfileLink;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_userId"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles = new HashSet<>();

	public User(String name, String username, String email, String phoneNo, String password) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.phoneNo = phoneNo;
		this.password = password;
	}

	public User(String username2, String email2, String phoneNo2, String encode) {

		this.username = username2;
		this.email = email2;
		this.phoneNo = phoneNo2;
		this.password = encode;
	}

}
