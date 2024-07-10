package com.rido.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.CancellationReason;

public interface CancellationReasonRepository extends JpaRepository<CancellationReason, Long> {

}
