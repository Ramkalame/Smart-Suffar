package com.rido.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ResponseStatus(value = HttpStatus.BAD_REQUEST) // 400 status code
public class BusinessException extends RuntimeException{
	
	private String errorCode;
	private String errorMessage;
	

}

