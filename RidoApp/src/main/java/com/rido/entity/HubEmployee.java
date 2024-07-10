package com.rido.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "hubEmployee", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phoneNo") })
public class HubEmployee {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long hubEmployeeId;

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

	private String password;

	private String profileImgLink;
	private String signatuePic;
	private String PassbookPic;
	private String passbookImg;

	private String EmpSignature;

	private Long adharNo;

	private String panNo;

	private String address;
	private String uidNo;

	@ManyToOne
	@JoinColumn(name = "hub_id")
	private Hub hub;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "hubEmployee_roles", joinColumns = @JoinColumn(name = "hubEmployee_hubEmployeeId"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles = new HashSet<>();

}
