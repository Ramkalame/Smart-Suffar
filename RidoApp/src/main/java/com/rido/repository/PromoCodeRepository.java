package com.rido.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.PromoCode;
import com.rido.entity.enums.PromocodeType;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

	Optional<PromoCode> findByCode(String code);
//	PromoCode findByCode(String code);

	Optional<PromoCode> findByAdmin_AdminId(Long adminId);

	List<PromoCode> findAllByPromocodeType(PromocodeType promocodeType);

}
