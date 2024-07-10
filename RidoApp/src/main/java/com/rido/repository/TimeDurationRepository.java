package com.rido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rido.entity.TimeDuration;


@Repository
public interface TimeDurationRepository extends JpaRepository<TimeDuration,Long> {

}
