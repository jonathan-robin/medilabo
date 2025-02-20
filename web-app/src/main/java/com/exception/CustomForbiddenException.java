package com.exception;

public class CustomForbiddenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomForbiddenException(String message) {
        super(message);
    }
}
