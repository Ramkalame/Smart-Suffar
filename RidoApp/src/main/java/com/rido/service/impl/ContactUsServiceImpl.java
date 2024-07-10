package com.rido.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rido.dto.ContactUsRequestDto;
import com.rido.entity.ContactUs;
import com.rido.repository.ContactUsRepository;
import com.rido.service.ContactUsService;

@Service
public class ContactUsServiceImpl implements ContactUsService{
	
	 @Autowired
	    private ContactUsRepository contactUsRepository;

	    @Override
	    public ContactUs saveContactUs(ContactUsRequestDto contactUsRequestDto) {
	        ContactUs contactUs = new ContactUs();
	        contactUs.setName(contactUsRequestDto.getName());
	        contactUs.setEmail(contactUsRequestDto.getEmail());
	        contactUs.setPhoneNo(contactUsRequestDto.getPhoneNo());
	        contactUs.setMessage(contactUsRequestDto.getMessage());
			return contactUsRepository.save(contactUs);
	    }

	    @Override
	    public List<ContactUs> getAllContactUs() {
	        return contactUsRepository.findAll();
	    }

}
