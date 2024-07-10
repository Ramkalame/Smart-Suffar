package com.rido.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hub", uniqueConstraints = { @UniqueConstraint(columnNames = "hubName"),
		@UniqueConstraint(columnNames = "email"), @UniqueConstraint(columnNames = "phoneNo"),
		@UniqueConstraint(columnNames = "hubName"), @UniqueConstraint(columnNames = "hubUniqeId") })
public class Hub {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long hubId;
	private String managerName;
	private String email;
	private String uidNo;
	private String phoneNo;
	private String profileImgLink;
	private String signatuePic;
	private String passbookPic;
	private String password;

	private String hubName;
	private String city;
	private String state;

//	private String designation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id")
	private Admin admin;

	private String hubUniqeId;

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(name = "hub_roles", joinColumns = @JoinColumn(name = "hub_hubId"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles = new HashSet<>();

}
