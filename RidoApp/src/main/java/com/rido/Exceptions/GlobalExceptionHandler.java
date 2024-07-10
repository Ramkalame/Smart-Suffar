package com.rido.Exceptions;

import java.util.NoSuchElementException;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.rido.utils.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
		ApiResponse<String> response = new ApiResponse<>();
		response.setData(null);
		response.setStatus(HttpStatus.CONFLICT);
		response.setSuccess(false);
		response.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ApiResponse<?>> handleNoSuchElementException(NoSuchElementException ex) {
		ApiResponse<?> response = new ApiResponse<>();
		response.setData(null);
		response.setStatus(HttpStatus.NOT_FOUND);
		response.setSuccess(false);
		response.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<Object> handleNoDataFoundException(DataAccessException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(DriverNotFoundException.class)
	public ResponseEntity<String> DriverNotFoundException(UserNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("driver  not found " + ex.getMessage());
	}
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
		
		ApiResponse<String> response = new ApiResponse<>();
		response.setData(null);
		response.setStatus(HttpStatus.NOT_FOUND);
		response.setSuccess(false);
		response.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		
	}

	

}
