package com.rido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rido.dto.ContactUsRequestDto;
import com.rido.entity.ContactUs;

@Service
public interface ContactUsService {
	
    ContactUs saveContactUs(ContactUsRequestDto contactUsRequestDto);

    List<ContactUs> getAllContactUs();


}
