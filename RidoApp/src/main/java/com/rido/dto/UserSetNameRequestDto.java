package com.rido.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class UserSetNameRequestDto {

	private String name;

//	private String firstName;
//
//	private String lastName;

}
