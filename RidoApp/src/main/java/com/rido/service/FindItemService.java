package com.rido.service;

import org.springframework.stereotype.Service;

import com.rido.entity.FindItem;

@Service
public interface FindItemService {
	
	public String FindLostItem(String adminId, FindItem findItem);

}
