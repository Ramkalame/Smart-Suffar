package com.rido.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.rido.entity.enums.PromocodeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
//@Table(name = "promo_code", uniqueConstraints = { @UniqueConstraint(columnNames = "code")})
public class PromoCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String code;

	private String codeDescription;

	private BigDecimal discountPercentage;

	@ManyToOne
	@JoinColumn(name = "admin_id")
	private Admin admin;

	private LocalDate expirationDate;

	@Enumerated(EnumType.STRING)
	private PromocodeType promocodeType;

}
