package com.rido.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 status code
public class DriverNotFoundException  extends RuntimeException {
	public DriverNotFoundException(String message) {
		super(message);
	}

}


