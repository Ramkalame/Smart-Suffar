package com.rido.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "admin", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phoneNo"),
		@UniqueConstraint(columnNames = "adminUniqeId") })
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long adminId;

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

	private String address;

	@Lob
	@Column(length = 1000000)
	private byte[] profileImg;

	private String profileImgLink;

	private String adminUniqeId;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "admin_roles", joinColumns = @JoinColumn(name = "admin_adminId"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles = new HashSet<>();

	public Admin(String name, String username, String email, String phoneNo, String password) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.phoneNo = phoneNo;
		this.password = password;
	}

}
