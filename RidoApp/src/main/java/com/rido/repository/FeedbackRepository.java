package com.rido.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rido.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
