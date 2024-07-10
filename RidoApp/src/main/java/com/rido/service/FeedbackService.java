package com.rido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.entity.Feedback;

@Service
public interface FeedbackService {

	

	List<Feedback> getAllFeedback();

	Feedback getfeedbackById(Long feedbackid);

	Feedback createfeedback(Feedback feedback);

	//Feedback createfeedback(Feedback feedback, String username);

}
