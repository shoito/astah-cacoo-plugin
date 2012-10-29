package com.github.astah.cacoo2astah;


public class APIException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public APIException(Exception e) {
		super(e);
	}

}
