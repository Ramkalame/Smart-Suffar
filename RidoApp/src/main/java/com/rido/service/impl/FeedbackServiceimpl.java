package com.rido.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rido.entity.Feedback;
import com.rido.repository.FeedbackRepository;
import com.rido.service.FeedbackService;
@Service
public class FeedbackServiceimpl implements FeedbackService {
	@Autowired
	private FeedbackRepository feedbackRepo;

	

	@Override
	public List<Feedback> getAllFeedback() {
		return feedbackRepo.findAll();
	}

	@Override
	public Feedback getfeedbackById(Long feedbackid) {
		
		Optional<Feedback> optionalfeedback=feedbackRepo.findById(feedbackid);
		return optionalfeedback.orElse(null);
	}

	@Override
	public Feedback createfeedback(Feedback feedback) {
		return feedbackRepo.save(feedback);
	}



}
